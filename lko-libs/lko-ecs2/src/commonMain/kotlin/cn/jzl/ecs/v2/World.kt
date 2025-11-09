package cn.jzl.ecs.v2

import cn.jzl.di.DI
import cn.jzl.di.DIAware
import cn.jzl.di.instance

/**
 * World 类，ECS系统的核心容器和入口点
 *
 * World 是整个ECS系统的中心，管理所有实体、组件和调度器
 * 它提供了创建实体、管理组件和调度任务的功能
 * 采用依赖注入模式组织各个服务
 *
 * @property di 依赖注入容器，用于获取各种服务实例
 */
class World internal constructor(override val di: DI) : DIAware by di {

    /**
     * 实体更新上下文，用于在更新阶段安全地修改实体和组件
     * 提供了延迟执行实体操作的机制，确保在合适的时机进行实体修改
     */
    @PublishedApi
    internal val entityUpdateContext by instance<EntityUpdateContext>()

    /**
     * 组件服务，负责管理组件类型和组件持有器
     * 提供组件的添加、获取、移除等核心功能
     */
    @PublishedApi
    internal val componentService by instance<ComponentService>()

    /**
     * 实体服务，负责创建、管理和销毁实体
     * 维护实体的生命周期和事件分发
     */
    @PublishedApi
    internal val entityService by instance<EntityService>()

    /**
     * 家族服务，负责管理实体家族（具有特定组件组合的实体集合）
     * 提供高效的实体查询机制
     */
    @PublishedApi
    internal val familyService by instance<FamilyService>()

    /**
     * 调度服务，负责创建和管理任务调度器
     * 处理任务的注册、调度和执行
     */
    @PublishedApi
    internal val scheduleService: ScheduleService by instance()

    /**
     * 资源服务，负责管理系统资源（如配置、状态等）
     * 提供资源的添加、获取、更新等功能
     */
    @PublishedApi
    internal val resourceService by instance<ResourceService>()
}