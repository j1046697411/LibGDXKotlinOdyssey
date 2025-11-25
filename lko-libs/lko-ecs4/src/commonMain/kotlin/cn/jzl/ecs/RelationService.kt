package cn.jzl.ecs

import cn.jzl.datastructure.list.SortSet
import kotlin.jvm.JvmInline
@JvmInline
value class RelationService(private val world: World) {

    @PublishedApi
    internal val entityService: EntityService get() = world.entityService

    fun hasRelation(entity: Entity, relation: Relation): Boolean = entityService.runOn(entity) {
        relation in this
    }

    fun getRelation(entity: Entity, relation: Relation): Any? = entityService.runOn(entity) { entityIndex ->
        val componentIndex = table.entityType.indexOf(relation).takeIf { it != -1 } ?: return@runOn null
        return@runOn getRelation(this, relation, entityIndex, componentIndex)
    }

    @Suppress("NOTHING_TO_INLINE")
    internal inline fun getRelation(archetype: Archetype, relation: Relation, entityIndex: Int, componentIndex: Int): Any? {
        return if (world.componentService.isShadedComponent(relation)) {
            world.shadedComponentService[relation]
        } else {
            archetype.table[entityIndex, componentIndex]
        }
    }

    @PublishedApi
    internal fun updateRelations(entity: Entity, operates: SortSet<BatchEntityEditor.EntityRelationOperate>) {
        entityService.runOn(entity) { entityIndex ->
            if (operates.isEmpty()) return@runOn
            val newArchetype = calculateNewArchetype(operates)
            val newEntityIndex = migrateEntityData(entity, entityIndex, newArchetype, operates)
            updateEntityRecords(entity, entityIndex, newArchetype, newEntityIndex, entityService)
            emitComponentModifyEvent(entity, this, newArchetype, operates)
        }
    }

    private fun emitComponentModifyEvent(
        entity: Entity,
        oldArchetype: Archetype,
        newArchetype: Archetype,
        operates: SortSet<BatchEntityEditor.EntityRelationOperate>
    ) {
        var oldRelationIndex = 0
        var newRelationIndex = 0
        while (oldRelationIndex < oldArchetype.entityType.size || newRelationIndex < newArchetype.entityType.size) {
            val oldRelation = oldArchetype.entityType.getOrNull(oldRelationIndex)
            val newRelation = newArchetype.entityType.getOrNull(newRelationIndex)
            if (oldRelation == null) {
                if (newRelation != null) {
                    world.observeService.dispatch(entity, world.componentService.components.onInserted, null, newRelation)
                    newRelationIndex++
                }
                continue
            }
            if (newRelation == null) {
                world.observeService.dispatch(entity, world.componentService.components.onRemoved, null, oldRelation)
                oldRelationIndex++
                continue
            }
            if (oldRelation == newRelation) {
                if (operates.any { it.relation == oldRelation && it is BatchEntityEditor.AddEntityRelationOperateWithData }) {
                    world.observeService.dispatch(entity, world.componentService.components.onUpdated, null, newRelation)
                }
                oldRelationIndex++
                newRelationIndex++
            } else if (oldRelation > newRelation) {
                world.observeService.dispatch(entity, world.componentService.components.onInserted, null, newRelation)
                newRelationIndex++
            } else {
                world.observeService.dispatch(entity, world.componentService.components.onRemoved, null, oldRelation)
                oldRelationIndex++
            }
        }
    }

    private fun Archetype.calculateNewArchetype(operates: SortSet<BatchEntityEditor.EntityRelationOperate>): Archetype {
        return operates.fold(this) { acc, operate ->
            when (operate) {
                is BatchEntityEditor.RemoveEntityRelationOperate -> acc - operate.relation
                else -> acc + operate.relation
            }
        }
    }

    private fun Archetype.migrateEntityData(
        entity: Entity,
        oldEntityIndex: Int,
        newArchetype: Archetype,
        operates: SortSet<BatchEntityEditor.EntityRelationOperate>
    ): Int {
        val holdsData = operates.asSequence().filterIsInstance<BatchEntityEditor.AddEntityRelationOperateWithData>().iterator()
        var nextData: BatchEntityEditor.AddEntityRelationOperateWithData? = null
        var currentIndex = 0

        if (this.id == newArchetype.id) {
            if (holdsData.hasNext()) {
                table.entityType.forEachIndexed { index, relation ->
                    if (nextData == null && holdsData.hasNext()) {
                        nextData = holdsData.next()
                    }
                    val newNextData = nextData ?: return@forEachIndexed
                    if (relation == newNextData.relation) {
                        table[oldEntityIndex, index] = newNextData.data
                    }
                }
            }
            return oldEntityIndex
        }
        return newArchetype.table.insert(entity) {
            if (nextData == null && holdsData.hasNext()) {
                nextData = holdsData.next()
            }
            val newNextData = nextData
            if (newNextData != null && newNextData.relation == this) {
                nextData = null
                if (currentIndex < table.entityType.size && table.entityType[currentIndex] == this) {
                    currentIndex++
                }
                newNextData.data
            } else {
                while (this > table.entityType[currentIndex]) currentIndex++
                table[oldEntityIndex, currentIndex++]
            }
        }
    }

    private fun Archetype.updateEntityRecords(
        entity: Entity,
        oldEntityIndex: Int,
        newArchetype: Archetype,
        newEntityIndex: Int,
        entityService: EntityService
    ) {
        if (id == newArchetype.id) return
        // 更新实体记录
        val isNotLast = oldEntityIndex != table.size - 1
        table.remove(oldEntityIndex)
        val movedEntity = if (table.size > 1 && isNotLast) table[oldEntityIndex] else null
        entityService.updateEntityRecord(entity, newArchetype, newEntityIndex)
        if (movedEntity != null) {
            entityService.updateEntityRecord(movedEntity, this, oldEntityIndex)
        }
    }
}