package cn.jzl.ecs.entity

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.ecs.EntityId
import cn.jzl.ecs.World

class EntityStoreImpl(private val world: World) : EntityStore {

    private val entities = ObjectFastList<Entity>(1024)
    private val activeEntities = BitSet(1024)
    private val recycledEntityIds = IntFastList(256)

    override fun create(): Entity {
        val entity = if (recycledEntityIds.isNotEmpty()) {
            val entityId = recycledEntityIds.removeLast()
            entities[entityId].upgrade()
        } else {
            val entity = Entity(world, EntityId(entities.size, 0))
            entities.insertLast(entity)
            entity
        }
        activeEntities.set(entity.entityId.id)
        entities[entity.entityId.id] = entity
        return entity
    }

    override fun create(entityId: Int): Entity {
        if (entityId in activeEntities) return entities[entityId]
        if (entityId >= entities.size) {
            for (i in entities.size..entityId) {
                val entity = Entity(world, EntityId.Companion(i, -1))
                recycledEntityIds.add(entity.entityId.data)
                entities.add(entity)
            }
        }
        val index = recycledEntityIds.indexOfLast { it == entityId }
        check(index != -1) { "entityId($entityId) is active" }
        val entity = entities[recycledEntityIds.removeAt(index)]
        val newEntity = entity.upgrade()
        entities[newEntity.entityId.id] = newEntity
        activeEntities.set(newEntity.entityId.id)
        return newEntity
    }

    override fun contains(entityId: EntityId): Boolean = entityId.id in activeEntities

    override fun destroy(entityId: EntityId) {
        if (entityId !in this) return
        activeEntities.clear(entityId.id)
        recycledEntityIds.insertLast(entityId.id)
    }

    override fun iterator(): Iterator<Entity> = iterator {
        var index = activeEntities.nextSetBit(0)
        while (index >= 0) {
            yield(entities[index])
            index = activeEntities.nextSetBit(index + 1)
        }
    }

    private fun Entity.upgrade(): Entity = Entity(world, EntityId.Companion(entityId.id, entityId.version + 1))
}