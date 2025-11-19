package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList

class EntityStoreImpl : EntityStore {

    private val entities = Entities()
    private val activeEntities = BitSet.Companion(1024)
    private val recycledEntityIds = IntFastList(256)

    override val size: Int get() = activeEntities.size

    override fun create(): Entity {
        val entity = if (recycledEntityIds.isNotEmpty()) {
            val entityIdValue = recycledEntityIds.removeLast()
            check(entityIdValue in 0 until entities.size) { "Invalid recycled entity ID: $entityIdValue" }
            entities[entityIdValue].upgrade()
        } else {
            val entity = Entity(entities.size, 0)
            entities.add(entity)
            entity
        }
        activeEntities.set(entity.id)
        entities[entity.id] = entity
        return entity
    }

    override fun create(entityId: Int): Entity {
        // 如果实体已激活，直接返回
        if (entityId in activeEntities) {
            return entities[entityId]
        }

        // 确保 entities 数组足够大以容纳 entityId
        // 扩展时创建占位符实体（version = -1）并添加到回收列表
        while (entityId >= entities.size) {
            val index = entities.size
            val placeholder = Entity(index, -1)
            recycledEntityIds.add(index)
            entities.add(placeholder)
        }
        // 检查 entityId 是否在 recycledEntityIds 中
        val recycledIndex = recycledEntityIds.indexOfLast { it == entityId }
        if (recycledIndex != -1) {
            // 实体在回收列表中，重用并升级版本
            recycledEntityIds.removeAt(recycledIndex)
            val entity = entities[entityId]
            val newEntity = entity.upgrade()
            entities[newEntity.id] = newEntity
            activeEntities.set(newEntity.id)
            return newEntity
        }

        // 边界情况：entityId 不在 activeEntities 中，也不在 recycledEntityIds 中
        // 但 entities[entityId] 存在且版本为 -1（可能是之前扩展数组时创建的占位符）
        val existingEntity = entities[entityId]
        if (existingEntity.version == -1) {
            // 占位符实体，直接升级并激活
            val newEntity = existingEntity.upgrade()
            entities[newEntity.id] = newEntity
            activeEntities.set(newEntity.id)
            return newEntity
        }

        // 异常状态：实体存在但版本不是 -1，且不在 activeEntities 中
        // 这可能是状态不一致，但我们尝试恢复：将其添加到回收列表并重新创建
        recycledEntityIds.add(entityId)
        val entity = entities[entityId]
        val newEntity = entity.upgrade()
        entities[newEntity.id] = newEntity
        activeEntities.set(newEntity.id)
        return newEntity
    }

    override fun contains(entity: Entity): Boolean = entity.id in activeEntities && entities[entity.id] == entity

    override fun get(entityId: Int): Entity? = if (entityId !in activeEntities) null else entities[entityId]

    override fun destroy(entity: Entity) {
        if (entity !in this) return
        activeEntities.clear(entity.id)
        recycledEntityIds.insertLast(entity.id)
    }

    override fun iterator(): Iterator<Entity> = iterator {
        var index = activeEntities.nextSetBit(0)
        while (index >= 0) {
            yield(entities[index])
            index = activeEntities.nextSetBit(index + 1)
        }
    }

    private fun Entity.upgrade(): Entity = Entity(id, version + 1)
}