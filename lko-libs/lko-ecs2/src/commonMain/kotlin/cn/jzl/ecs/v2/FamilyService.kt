package cn.jzl.ecs.v2

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * FamilyService - 实体家族服务
 *
 * 核心职责：
 * - 管理和维护实体家族(Family)的创建与缓存
 * - 监听实体变化并自动更新相关家族的成员
 * - 提供声明式API创建实体过滤条件
 * - 实现Sequence接口，支持迭代所有已创建的家族
 *
 * 实体家族是ECS框架中用于过滤和查询具有特定组件组合的实体的机制，
 * 通过FamilyService，可以高效地管理这些过滤规则并自动维护符合条件的实体集合。
 */
class FamilyService(private val world: World) : Sequence<Family> {

    /**
     * 家族映射表 - 缓存已创建的家族实例
     *
     * 使用FamilyDefinition作为键，Family作为值，确保相同定义的家族只创建一次
     * 这样可以避免重复的过滤逻辑和实体集合维护
     */
    private val families = mutableMapOf<FamilyDefinition, Family>()

    private val lock = ReentrantLock()

    /**
     * 初始化块 - 注册实体变更监听器
     *
     * 监听三种实体事件：
     * - 创建：新实体被创建时
     * - 更新：实体组件被添加/移除时
     * - 销毁：实体被销毁时
     *
     * 当这些事件发生时，会触发家族成员的重新评估
     */
    init {
        // 监听实体创建事件
        world.entityService.onEntityCreate.add { entityChanges(it) }
        // 监听实体更新事件（组件添加/移除）
        world.entityService.onEntityUpdate.add { entityChanges(it) }
        // 监听实体销毁事件
        world.entityService.onEntityDestroy.add { entityChanges(it) }
    }

    /**
     * 处理实体变更 - 内部方法，由事件监听器调用
     *
     * 当实体发生变化时，通知所有家族重新评估该实体是否符合条件
     *
     * @param entity 发生变化的实体
     */
    internal fun entityChanges(entity: Entity) {
        // 快速路径：如果没有家族，直接返回
        if (families.isEmpty()) return
        // 通知所有家族实体发生变化
        families.values.forEach { family -> family.entityChanged(entity) }
    }

    /**
     * 创建或获取实体家族 - 主要API入口
     *
     * 使用DSL风格创建家族定义，支持链式调用配置过滤条件
     *
     * @param configuration 家族定义配置函数，使用DSL方式设置过滤条件
     * @return 创建或缓存的家族实例
     */
    fun family(configuration: FamilyDefinition.() -> Unit): Family {
        // 创建并配置家族定义
        val familyDefinition = FamilyDefinition().apply(configuration)
        // 查找缓存或创建新家族
        return families[familyDefinition] ?: lock.withLock {
            families.getOrPut(familyDefinition) { createFamily(familyDefinition) }
        }
    }

    /**
     * 创建新的家族实例 - 内部方法
     *
     * 实现逻辑：
     * 1. 根据家族定义创建新的Family实例
     * 2. 对世界中所有现有实体执行初始筛选，评估每个实体是否符合家族条件
     * 3. 将符合条件的实体加入到家族的实体集合中
     * 4. 返回完全初始化的家族实例
     *
     * 这一步骤确保新创建的家族能够立即反映当前世界中所有符合条件的实体，
     * 为后续的实体变化监听奠定基础。
     *
     * @param familyDefinition 家族定义，包含所有筛选条件
     * @return 新创建并完成初始填充的家族实例
     */
    private fun createFamily(familyDefinition: FamilyDefinition): Family {
        // 创建家族实例
        val family = Family(world, familyDefinition)
        // 初始筛选：对所有现有实体进行评估，将符合条件的实体加入家族
        world.entityService.entities.forEach { entity -> family.entityChanged(entity) }
        return family
    }

    /**
     * 获取所有已创建家族的迭代器 - 实现Sequence接口
     *
     * @return 家族迭代器
     */
    override fun iterator(): Iterator<Family> = families.values.iterator()
}