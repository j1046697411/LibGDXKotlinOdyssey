/**
 * ECS (Entity Component System) 核心模块
 *
 * 此模块定义了ECS架构的基础组件和核心接口，包括：
 * - Component: 组件接口，所有组件类型的基类
 * - ComponentType: 组件类型标识
 * - Entity: 实体值类，由ID和版本组成
 * - World: 世界类，ECS系统的容器和核心入口
 * - 各种辅助函数和扩展方法
 *
 * ECS架构将游戏对象分解为实体(Entity)、组件(Component)和系统(System)三个部分：
 * - 实体(Entity): 只是一个唯一标识符，不包含任何逻辑
 * - 组件(Component): 纯数据类，存储实体的属性
 * - 系统(System): 包含逻辑，处理具有特定组件组合的实体
 *
 * 此实现采用了现代化的Kotlin特性，包括协程、类型安全和性能优化的数据结构。
 */
@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs.v2

import cn.jzl.di.*
import kotlinx.atomicfu.atomic
import org.kodein.type.TypeToken
import kotlin.time.Duration

/**
 * 检查实体是否处于活跃状态
 *
 * 活跃实体存在于ECS系统中，可以被查询和处理
 *
 * @param entity 要检查的实体
 * @return 如果实体活跃则返回true，否则返回false
 */
inline fun World.isActive(entity: Entity): Boolean = entity in entityService

/**
 * 从世界中移除实体
 *
 * 移除实体将同时移除其所有组件
 *
 * @param entity 要移除的实体
 */
inline fun World.remove(entity: Entity) = entityService.remove(entity)

/**
 * 创建并启动一个新的调度器
 *
 * 实现逻辑：
 * 1. 接收调度器的名称、优先级和执行代码块
 * 2. 委托给调度服务创建新的调度器实例
 * 3. 调度器会根据优先级在适当的时机执行指定的协程代码
 * 4. 返回调度器描述符，用于后续管理调度器的生命周期
 *
 * 调度器是ECS系统中处理游戏逻辑的核心机制，它允许：
 * - 定义任务的执行顺序和依赖关系
 * - 实现定时任务和持续运行的系统
 * - 管理任务的暂停、恢复和取消
 * - 按优先级组织任务执行
 *
 * @param scheduleName 调度器名称，用于调试和识别，默认为空字符串
 * @param scheduleTaskPriority 调度器优先级，决定执行顺序，默认为NORMAL
 * @param block 调度器要执行的协程代码块，在ScheduleScope作用域中运行
 * @return 创建的调度器描述符，可用于管理调度器生命周期
 */
inline fun World.schedule(
    scheduleName: String = "",
    scheduleTaskPriority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
    noinline block: suspend ScheduleScope.() -> Unit
): ScheduleDescriptor = scheduleService.schedule(scheduleName, scheduleTaskPriority, block)

/**
 * 检查调度器是否处于活跃状态
 *
 * 实现逻辑：
 * 1. 从调度器描述符中提取调度器实例
 * 2. 委托给调度服务检查调度器的活跃状态
 * 3. 返回检查结果
 *
 * 活跃的调度器会在世界更新时执行其任务，不活跃的调度器则会被跳过。
 * 这个方法通常用于监控系统状态或实现调度器的动态管理。
 *
 * @param scheduleDescriptor 要检查的调度器描述符
 * @return 如果调度器活跃则返回true，否则返回false
 */
inline fun World.isActive(scheduleDescriptor: ScheduleDescriptor): Boolean {
    return scheduleService.isActive(scheduleDescriptor.schedule)
}

/**
 * 更新世界状态
 *
 * 执行所有活跃调度器的任务，推进游戏逻辑
 *
 * @param delta 自上次更新以来经过的时间
 */
inline fun World.update(delta: Duration): Unit = scheduleService.update(delta)

/**
 * 组件类型ID生成器
 * 用于为每个组件类型分配唯一的索引
 */
internal val idGenerator = atomic(0)

/**
 * 创建一个新的ECS世界实例
 *
 * 这是初始化ECS系统的主要入口点，提供了简洁的DSL来配置世界的依赖注入容器
 * 允许用户自定义依赖注入配置，注册额外的服务和组件
 *
 * @param configuration 依赖注入配置函数，用于注册自定义服务和组件
 * @return 创建的世界实例，可立即用于ECS操作
 */
fun world(configuration: DIMainBuilder.() -> Unit): World {
    val di = DI {
        module(coreModule) // 导入核心模块，提供基础ECS服务
        configuration()    // 应用用户自定义配置
    }
    val world by di.instance<World>()
    return world
}

/**
 * 依赖注入标签，用于配置ECS系统的初始容量
 *
 * 可在依赖注入配置中使用此标签自定义实体存储的初始容量大小
 * 默认值为1024，可根据预期的实体数量调整以优化性能
 */
const val TAG_CAPACITY = "ecs_capacity"

/**
 * ECS核心模块，注册所有必要的服务
 *
 * 此模块定义了ECS系统所需的所有核心服务，为整个框架提供基础支持：
 * - World：ECS系统的主容器和入口点
 * - EntityStore：实体存储服务，管理实体ID和版本
 * - ComponentService：组件管理服务，负责组件的添加、获取和移除
 * - EntityUpdateContext：实体更新上下文，提供安全的实体修改机制
 * - EntityService：实体管理服务，管理实体的生命周期
 * - FamilyService：实体家族管理服务，提供高效的实体查询机制
 * - ScheduleService：任务调度服务，处理任务的注册和调度
 * - ScheduleDispatcher：调度器分发服务，执行实际的任务调度
 */
private val coreModule = module(TypeToken.Any) {
    // 注册World实例为单例，作为ECS系统的核心容器
    this bind singleton { World(di) }

    // 注册实体存储实现，使用配置的容量或默认值1024
    this bind singleton { EntityStoreImpl(instanceOrNull(TAG_CAPACITY) ?: 1024) }

    // 注册组件服务，负责管理组件的添加、获取和移除
    this bind singleton { new(::ComponentService) }

    // 注册实体更新上下文，提供安全的实体修改机制
    this bind singleton { new(::EntityUpdateContextImpl) }

    // 注册实体服务，管理实体的创建、生命周期和移除
    this bind singleton { new(::EntityService) }

    // 注册家族服务，管理实体家族和高效的实体查询
    this bind singleton { new(::FamilyService) }

    // 注册调度服务，处理任务的注册、调度和执行
    this bind singleton { new(::ScheduleService) }

    // 注册调度器分发器实现，实际执行任务调度逻辑
    this bind singleton { new(::ScheduleDispatcherImpl) }
    // 注册资源模块，提供资源服务和资源实例的创建
    module(resourceModule)
}
