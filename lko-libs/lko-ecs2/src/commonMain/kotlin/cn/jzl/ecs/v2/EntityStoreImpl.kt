package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

internal class EntityStoreImpl(capacity: Int = 1024) : EntityStore {
    private val allEntities = ObjectFastList<Entity>(capacity)
    private val recycledEntities = IntFastList(64)
    private val activeEntities = BitSet.Companion(capacity)
    private val entityCount = atomic(0)
    private val lock = ReentrantLock()

    override val size: Int get() = entityCount.value
    override val entities: Sequence<Entity> = activeEntities.asSequence().map { allEntities[it] }

    override fun create(): Entity = lock.withLock {
        val entity = if (recycledEntities.isNotEmpty()) {
            recycledEntities.removeLast().let { allEntities[it] }
        } else {
            Entity(allEntities.size, -1).also { allEntities.add(it) }
        }
        activeEntities[entity.id] = true
        entityCount.incrementAndGet()
        val newEntity = entity.upgrade()
        allEntities[entity.id] = newEntity
        newEntity
    }

    override fun create(id: Int): Entity = lock.withLock {
        if (id >= allEntities.size) {
            val size = allEntities.size
            val count = id + 1 - size
            allEntities.safeInsertLast(count) {
                for (entityId in size until id + 1) {
                    unsafeInsert(Entity(entityId, -1))
                }
            }
            recycledEntities.safeInsertLast(count) {
                for (entityId in size until id + 1) {
                    unsafeInsert(entityId)
                }
            }
        }
        val entityId = recycledEntities.indexOfLast { id == it }
        check(entityId != -1) { "Entity $id is not recycled" }
        recycledEntities.removeAt(entityId)
        val entity = allEntities[entityId]
        activeEntities[entity.id] = true
        entityCount.incrementAndGet()
        val newEntity = entity.upgrade()
        allEntities[entity.id] = newEntity
        return newEntity
    }

    override fun contains(entity: Entity): Boolean = activeEntities[entity.id] && allEntities[entity.id] == entity

    override fun get(entityId: Int): Entity = allEntities[entityId]

    override fun minusAssign(entity: Entity): Unit = lock.withLock {
        if (!contains(entity)) return
        activeEntities[entity.id] = false
        entityCount.decrementAndGet()
        recycledEntities.add(entity.id)
    }

    override fun clear(): Unit = lock.withLock {
        activeEntities.clear()
        entityCount.value = 0
        recycledEntities.clear()
        allEntities.clear()
    }

    private fun Entity.upgrade(): Entity = Entity(id, version + 1)
}