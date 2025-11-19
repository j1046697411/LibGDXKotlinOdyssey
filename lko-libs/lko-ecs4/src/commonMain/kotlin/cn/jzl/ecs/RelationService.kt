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
        val componentIndex = table.entityType.indexOf(relation)
        if (componentIndex != -1) table[entityIndex, componentIndex] else null
    }

    @PublishedApi
    internal fun updateRelations(entity: Entity, operates: SortSet<BatchEntityEditor.EntityRelationOperate>): Unit = entityService.runOn(entity) { entityIndex ->
        if (operates.isEmpty()) return@runOn
        val newOperates = operates.sortedBy { operate -> operate.relation.data }
        val newArchetype = newOperates.fold(this) { acc, operate ->
            if (operate is BatchEntityEditor.RemoveEntityRelationOperate) {
                acc - operate.relation
            } else {
                acc + operate.relation
            }
        }
        val holdsData = operates.asSequence().filterIsInstance<BatchEntityEditor.AddEntityRelationOperateWithData>().iterator()
        var nextData: BatchEntityEditor.AddEntityRelationOperateWithData? = null
        var currentIndex = 0
        val newEntityIndex =newArchetype.table.insert(entity) {
            if (nextData == null && holdsData.hasNext()) {
                nextData = holdsData.next()
            }
            val newNextData = nextData
            if (newNextData != null && newNextData.relation == this) {
                nextData = null
                if (currentIndex < table.entityType.size && table.entityType[currentIndex] == this) {
                    currentIndex ++
                }
                newNextData.data
            } else {
                table[entityIndex, currentIndex ++]
            }
        }
        // 更新实体记录
        val isNotLast = entityIndex != table.size - 1
        table.remove(entityIndex)
        val movedEntity = if (table.size > 1 && isNotLast) table[entityIndex] else null
        entityService.updateEntityRecord(entity, newArchetype, newEntityIndex)
        if (movedEntity != null) {
            entityService.updateEntityRecord(movedEntity, this, entityIndex)
        }
    }
}