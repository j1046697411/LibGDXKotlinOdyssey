package cn.jzl.ecs

import cn.jzl.datastructure.list.LongFastList
import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low

class EntityService(@PublishedApi internal val world: World) {

    @PublishedApi
    internal val entityRecords = LongFastList()

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun get(entityId: Int): Entity? = world.entityStore[entityId]

    inline fun create(
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = postCreate(world.entityStore.create(), configuration)

    inline fun create(
        entityId: Int,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = postCreate(world.entityStore.create(entityId), configuration)

    inline fun configure(entity: Entity, configuration: EntityUpdateContext.(Entity) -> Unit) {
        val batchEntityEditor = BatchEntityEditor(entity)
        val entityCreateContext = EntityUpdateContext(world, batchEntityEditor)
        entityCreateContext.configuration(entity)
        batchEntityEditor.apply(world)
    }

    @PublishedApi
    internal inline fun postCreate(entity: Entity, configuration: EntityCreateContext.(Entity) -> Unit): Entity = entity.also {
        val rootArchetype = world.archetypeService.rootArchetype
        val entityIndex = rootArchetype.table.insert(entity) { }
        updateEntityRecord(entity, rootArchetype, entityIndex)
        val entityEditor = BatchEntityEditor(entity)
        val entityUpdateContext = EntityUpdateContext(world, entityEditor)
        entityUpdateContext.configuration(entity)
        entityEditor.apply(world)
    }

    @PublishedApi
    internal fun updateEntityRecord(entity: Entity, archetype: Archetype, row: Int) {
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

    fun destroy(entity: Entity): Unit = runOn(entity) {
        world.entityStore.destroy(entity)
        updateEntityRecord(entity, world.archetypeService.rootArchetype, -1)
        table.remove(it)
    }
}