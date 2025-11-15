package cn.jzl.ecs.entity

import cn.jzl.datastructure.list.LongFastList
import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import cn.jzl.ecs.Archetype
import cn.jzl.ecs.EntityId
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World

class EntityService(@PublishedApi internal val world: World) {

    @PublishedApi
    internal val entityRecords = LongFastList()

    @PublishedApi
    internal val entityEditor = object : EntityEditor {
        override fun addRelation(entityId: EntityId, relation: Relation, data: Any) {
            world.relationService.addRelation(entityId, relation, data)
        }

        override fun addRelation(entityId: EntityId, relation: Relation) {
            world.relationService.addRelation(entityId, relation)
        }

        override fun removeRelation(entityId: EntityId, relation: Relation) {
            world.relationService.removeRelation(entityId, relation)
        }
    }

    inline fun create(
        configuration: EntityCreateContext.(Entity) -> Unit
    ): Entity = postCreate(world.entityStore.create(), configuration)

    inline fun create(
        entityId: Int,
        configuration: EntityCreateContext.(Entity) -> Unit
    ): Entity = postCreate(world.entityStore.create(entityId), configuration)

    inline fun configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) {
        val entityCreateContext = EntityUpdateContext(world, entityEditor)
        entityCreateContext.configuration(entity)
    }

    @PublishedApi
    internal inline fun postCreate(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entity.also {
        val rootArchetype = world.archetypeService.rootArchetype
        val entityIndex = rootArchetype.table.insert(entity.entityId) { }
        updateEntityRecord(entity.entityId, rootArchetype, entityIndex)
        val entityUpdateContext = EntityUpdateContext(world, entityEditor)
        entityUpdateContext.configuration(entity)
    }

    @PublishedApi
    internal fun updateEntityRecord(entityId: EntityId, archetype: Archetype, row: Int) {
        entityRecords.ensureCapacity(entityId.id + 1, -1)
        entityRecords[entityId.id] = Long.Companion.fromLowHigh(archetype.id, row)
    }

    inline fun <R> runOn(entityId: EntityId, block: Archetype.(Int) -> R): R {
        check(entityId.id in entityRecords.indices) { "entity id $entityId is out of bounds" }
        check(entityId in world.entityStore) { "entity id $entityId is not valid" }
        val record = entityRecords[entityId.id]
        val archetype = world.archetypeService[record.low]
        return archetype.block(record.high)
    }

    fun destroy(entityId: EntityId) {
        world.entityStore.destroy(entityId)
        updateEntityRecord(entityId, world.archetypeService.rootArchetype, -1)
    }
}