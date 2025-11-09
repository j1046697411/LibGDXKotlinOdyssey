package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.signal.Signal
import kotlinx.atomicfu.atomic

/**
 * Family - 实体家族类
 * 
 * 核心职责：
 * - 维护符合特定组件过滤条件的实体集合
 * - 提供高效的实体查询和遍历功能
 * - 发布实体加入/离开家族的信号通知
 * - 实现EntityComponentContext接口，提供组件访问能力
 * 
 * 实体家族是ECS框架中的重要概念，它允许系统针对特定组件组合的实体进行操作，
 * 而不需要手动过滤所有实体。通过BitSet高效存储符合条件的实体ID，确保查询和更新性能。
 * 
 * @property world ECS世界实例
 * @property familyDefinition 家族定义，包含实体过滤条件
 */
class Family(
    override val world: World,
    internal val familyDefinition: FamilyDefinition
) : EntityComponentContext by world.entityUpdateContext {
    /**
     * 实体位集 - 存储符合条件的实体ID
     * 
     * 使用BitSet实现高效的集合操作，每个位表示对应ID的实体是否在家族中
     */
    private val entityBits = BitSet()
    
    /**
     * 实体计数 - 原子变量，确保线程安全
     * 
     * 维护家族中实体的数量，避免每次都需要计算BitSet
     */
    private val entityCount = atomic(0)

    /**
     * 获取家族中的所有实体序列
     * 
     * 提供延迟计算的实体序列，避免不必要的集合创建
     * 
     * @return 符合条件的实体序列
     */
    val entities: Sequence<Entity> get() = entityBits.map { entityId -> world.entityService[entityId] }
    
    /**
     * 获取家族中实体的数量
     * 
     * @return 实体数量
     */
    val size: Int get() = entityCount.value

    /**
     * 实体插入信号 - 当实体被添加到家族时触发
     * 
     * 允许系统对实体加入做出响应，例如初始化或设置特定状态
     */
    val onEntityInserted: Signal<Entity> = Signal()
    
    /**
     * 实体移除信号 - 当实体被从家族中移除时触发
     * 
     * 允许系统对实体离开做出响应，例如清理资源或停止处理
     */
    val onEntityRemoved: Signal<Entity> = Signal()

    /**
     * 处理实体变更 - 内部方法，由FamilyService调用
     * 
     * 实现逻辑：
     * 1. 检查实体当前状态与家族规则的匹配情况
     * 2. 根据匹配结果和当前成员状态执行相应操作：
     *    - 匹配条件但不在家族中：添加实体到家族
     *    - 不匹配条件但在家族中：从家族移除实体
     *    - 其他情况（已匹配且在家族中，或不匹配且不在家族中）：不执行操作
     * 3. 确保家族成员状态始终与实体组件配置保持同步
     * 
     * 这种设计采用惰性更新策略，只有在实体状态发生变化且影响家族成员资格时才执行更新，
     * 优化了性能并确保数据一致性。
     * 
     * @param entity 发生变化的实体
     */
    internal fun entityChanged(entity: Entity) {
        when {
            // 情况1：实体现在符合家族条件，但尚未在家族中 -> 添加到家族
            entity in this && entity.id !in entityBits -> insertEntity(entity)
            // 情况2：实体不再符合家族条件，但仍在家族中 -> 从家族移除
            entity !in this && entity.id in entityBits -> removeEntity(entity)
            // 其他情况：状态未变化，无需操作
        }
    }

    /**
     * 将实体添加到家族 - 内部方法
     * 
     * @param entity 要添加的实体
     */
    private fun insertEntity(entity: Entity) {
        // 设置位集中对应的位
        entityBits.set(entity.id)
        // 原子增加计数
        entityCount.incrementAndGet()
        // 触发插入信号
        onEntityInserted(entity)
    }

    /**
     * 从家族中移除实体 - 内部方法
     * 
     * @param entity 要移除的实体
     */
    private fun removeEntity(entity: Entity) {
        // 清除位集中对应的位
        entityBits.clear(entity.id)
        // 原子减少计数
        entityCount.decrementAndGet()
        // 触发移除信号
        onEntityRemoved(entity)
    }

    /**
     * 检查实体是否符合家族条件 - 实现contains运算符
     * 
     * @param entity 要检查的实体
     * @return 如果实体符合家族条件返回true
     */
    operator fun contains(entity: Entity): Boolean = familyDefinition.run { checkEntity(entity) }
}