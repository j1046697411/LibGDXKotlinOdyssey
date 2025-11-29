package cn.jzl.ecs

import cn.jzl.datastructure.list.LongFastList
import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import cn.jzl.ecs.query.forEach

@PublishedApi
internal class EntityService(val world: World) {

    val entityRecords = LongFastList()

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun get(entityId: Int): Entity? = world.entityStore[entityId]

    inline fun create(
        event: Boolean = true,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = postCreate(world.entityStore.create(), event, configuration)

    inline fun create(
        entityId: Int,
        event: Boolean = true,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = postCreate(world.entityStore.create(entityId), event, configuration)

    inline fun configure(entity: Entity, event: Boolean = true, configuration: EntityUpdateContext.(Entity) -> Unit) {
        val batchEntityEditor = BatchEntityEditor(world, entity)
        val entityCreateContext = EntityUpdateContext(world, batchEntityEditor)
        entityCreateContext.configuration(entity)
        batchEntityEditor.apply(world)
        if (event) {
            world.observeService.dispatch(entity, world.components.onEntityUpdated)
        }
    }

    inline fun postCreate(entity: Entity, event: Boolean = true, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entity.also {
        val rootArchetype = world.archetypeService.rootArchetype
        val entityIndex = rootArchetype.table.insert(entity) { }
        updateEntityRecord(entity, rootArchetype, entityIndex)
        val entityEditor = BatchEntityEditor(world, entity)
        val entityUpdateContext = EntityUpdateContext(world, entityEditor)
        entityUpdateContext.configuration(entity)
        entityEditor.apply(world)
        if (event) {
            world.observeService.dispatch(entity, world.components.onEntityCreated)
        }
    }

    inline fun childOf(
        parent: Entity,
        event: Boolean = true,
        configuration: EntityCreateContext.(Entity) -> Unit
    ): Entity = create(event) {
        configuration(it)
        it.addRelation(world.components.childOf, parent)
    }

    inline fun childOf(
        parent: Entity,
        entityId: Int,
        event: Boolean = true,
        configuration: EntityCreateContext.(Entity) -> Unit
    ): Entity = create(entityId, event) {
        configuration(it)
        it.addRelation(world.components.childOf, parent)
    }

    fun updateEntityRecord(entity: Entity, archetype: Archetype, row: Int) {
        entityRecords.ensureCapacity(entity.id + 1, -1)
        entityRecords[entity.id] = Long.fromLowHigh(archetype.id, row)
    }

    inline fun <R> runOn(entity: Entity, block: Archetype.(Int) -> R): R {
        check(entity.id in entityRecords.indices) { "entity id $entity is out of bounds" }
        // contains 方法现在会检查版本是否匹配
        check(entity in world.entityStore) {
            "entity id $entity is not valid (may have been destroyed and recreated with different version)"
        }
        val record = entityRecords[entity.id]
        val archetype = world.archetypeService[record.low]
        return archetype.block(record.high)
    }

    fun destroy(entity: Entity): Unit = runOn(entity) { entityIndex ->
        world.observeService.dispatch(entity, world.componentService.components.onEntityDestroyed)
        configure(entity) {
            it.children.forEach { destroy(this.entity) }
        }
        entityType.forEach {
            world.observeService.dispatch(entity, world.components.onRemoved, null, it)
        }
        world.entityStore.destroy(entity)
        updateEntityRecord(entity, world.archetypeService.rootArchetype, -1)
        table.remove(entityIndex)
    }
}