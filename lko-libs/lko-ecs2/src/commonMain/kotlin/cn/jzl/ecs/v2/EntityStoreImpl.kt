package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * EntityStoreImpl - 实体存储接口的具体实现
 * 
 * 核心功能：
 * - 实现高效的实体ID分配和回收机制
 * - 支持实体版本控制，防止悬挂引用
 * - 线程安全的实体操作
 * - 预分配容量，减少动态扩容开销
 * 
 * 实现细节：
 * - 使用ObjectFastList存储所有实体（包括已回收的）
 * - 使用IntFastList跟踪可回收的实体ID
 * - 使用BitSet标记活动实体
 * - 使用ReentrantLock确保线程安全
 * - 使用原子变量维护实体计数
 */
internal class EntityStoreImpl(capacity: Int = 1024) : EntityStore {
    /**
     * 存储所有实体的快速列表，包括活动和已回收的实体
     * 使用预分配容量的快速列表提高性能
     */
    private val allEntities = ObjectFastList<Entity>(capacity)

    /**
     * 存储可回收实体ID的快速列表
     * 用于复用已销毁实体的ID
     */
    private val recycledEntities = IntFastList(64)

    /**
     * 标记活动实体的位集合
     * 位索引对应实体ID，值为true表示实体处于活动状态
     */
    private val activeEntities = BitSet.Companion(capacity)

    /**
     * 原子变量，跟踪活动实体的数量
     */
    private val entityCount = atomic(0)

    /**
     * 重入锁，确保实体操作的线程安全
     */
    private val lock = ReentrantLock()

    /**
     * 获取活动实体的数量
     * 
     * @return 当前存储中的实体数量
     */
    override val size: Int get() = entityCount.value

    /**
     * 获取所有活动实体的序列
     * 通过将活动实体的位集转换为序列，并映射到对应的实体对象
     * 
     * @return 活动实体的序列
     */
    override val entities: Sequence<Entity> = activeEntities.asSequence().map { allEntities[it] }

    /**
     * 创建一个新的实体
     * 
     * 实现逻辑：
     * 1. 优先从回收列表中获取可复用的实体ID
     * 2. 如果没有可回收的ID，则创建新的实体
     * 3. 标记实体为活动状态
     * 4. 增加实体计数
     * 5. 升级实体版本（防止悬挂引用）
     * 
     * @return 新创建的实体
     */
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

    /**
     * 使用指定ID创建实体
     * 
     * 实现逻辑：
     * 1. 确保存储容量足够容纳指定ID
     * 2. 验证指定ID是否在回收列表中
     * 3. 从回收列表中移除该ID
     * 4. 标记实体为活动状态
     * 5. 增加实体计数
     * 6. 升级实体版本
     * 
     * @param id 指定的实体ID
     * @return 使用指定ID创建的实体
     * @throws IllegalStateException 如果指定ID不在回收列表中
     */
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

    /**
     * 检查实体是否存在并处于活动状态
     * 
     * 实现逻辑：
     * 1. 检查实体ID对应的位是否被标记为活动
     * 2. 验证实体版本是否匹配（防止悬挂引用）
     * 
     * @param entity 要检查的实体
     * @return 如果实体存在且处于活动状态返回true
     */
    override fun contains(entity: Entity): Boolean = activeEntities[entity.id] && allEntities[entity.id] == entity

    /**
     * 通过ID获取实体
     * 
     * @param entityId 实体ID
     * @return 对应的实体
     * @throws IndexOutOfBoundsException 如果ID超出范围
     */
    override fun get(entityId: Int): Entity = allEntities[entityId]

    /**
     * 从存储中移除实体
     * 
     * 实现逻辑：
     * 1. 检查实体是否存在且活动
     * 2. 标记实体为非活动状态
     * 3. 减少实体计数
     * 4. 将实体ID添加到回收列表
     * 
     * @param entity 要移除的实体
     */
    override fun minusAssign(entity: Entity): Unit = lock.withLock {
        if (!contains(entity)) return
        activeEntities[entity.id] = false
        entityCount.decrementAndGet()
        recycledEntities.add(entity.id)
    }

    /**
     * 清空存储中的所有实体
     * 
     * 实现逻辑：
     * 1. 清空活动实体位集
     * 2. 重置实体计数为0
     * 3. 清空回收列表
     * 4. 清空实体列表
     */
    override fun clear(): Unit = lock.withLock {
        activeEntities.clear()
        entityCount.value = 0
        recycledEntities.clear()
        allEntities.clear()
    }

    /**
     * 升级实体版本
     * 
     * 每次实体被重新创建时，版本号递增，
     * 这可以防止对已销毁实体的悬挂引用
     * 
     * @return 升级版本后的新实体
     */
    private fun Entity.upgrade(): Entity = Entity(id, version + 1)
}