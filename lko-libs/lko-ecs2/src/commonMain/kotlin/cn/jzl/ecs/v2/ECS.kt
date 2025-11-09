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
 * 组件接口，所有组件类型的基类
 * 
 * 组件是ECS架构中的数据容器，只包含状态而不包含逻辑
 * 每个组件必须指定其类型，并可以选择性地实现附加和分离回调
 * 
 * @param C 组件的具体类型，用于自引用，确保类型安全
 */
interface Component<C : Component<C>> {
    /**
     * 组件的类型标识，用于在系统中唯一标识此组件类型
     */
    val type: ComponentType<C>
    
    /**
     * 当组件被附加到实体时调用的回调
     * 
     * 可用于执行初始化逻辑或注册监听器
     * 
     * @param entity 组件被附加到的实体
     */
    fun World.onAttach(entity: Entity): Unit = Unit
    
    /**
     * 当组件从实体中分离时调用的回调
     * 
     * 可用于执行清理逻辑或移除监听器
     * 
     * @param entity 组件被从其分离的实体
     */
    fun World.onDetach(entity: Entity): Unit = Unit
}

/**
 * World 类，ECS系统的核心容器和入口点
 * 
 * World 是整个ECS系统的中心，管理所有实体、组件和调度器
 * 它提供了创建实体、管理组件和调度任务的功能
 * 采用依赖注入模式组织各个服务
 * 
 * @property di 依赖注入容器，用于获取各种服务实例
 */
class World(override val di: DI) : DIAware by di {
    
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
    internal val scheduleService by instance<ScheduleService>()
}

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
 * ComponentType - 组件类型标识抽象类
 * 
 * 实现逻辑：
 * 1. 作为组件类型的唯一标识基类，为每个组件类型分配一个唯一索引
 * 2. 使用原子计数器确保索引分配的线程安全性
 * 3. 在类实例化时自动生成并分配索引，无需手动管理
 * 4. 索引用于组件的快速查找、位运算和存储优化
 * 
 * 这个类是ECS系统类型安全和高性能的关键部分，它：
 * - 实现了组件类型的运行时识别
 * - 支持组件类型到索引的映射，用于位运算优化
 * - 确保类型安全性，防止不同组件类型的混淆
 * - 提供高效的组件查找机制，支持快速的实体过滤
 * 
 * @param T 组件类型的实际数据类型
 */
abstract class ComponentType<T> {
    /**
     * 组件类型的唯一索引
     * 
     * 索引特性：
     * - 在类实例化时自动分配，从0开始递增
     * - 使用原子操作保证线程安全
     * - 索引在应用运行期间保持稳定
     * - 用于BitSet和数组索引等性能优化场景
     */
    val index: Int = idGenerator.getAndIncrement()
}

/**
 * EntityTag - 实体标签基类
 * 
 * 实现逻辑：
 * 1. 继承自ComponentType，使用Boolean类型作为组件数据
 * 2. 作为标记组件的特殊实现，用于表示实体的二进制状态
 * 3. 提供轻量级的实体分类和过滤机制
 * 4. 与ComponentType共享索引分配机制，但有特殊的语义
 * 
 * 实体标签是一种轻量级的组件形式，它：
 * - 不存储复杂数据，只表示存在与否
 * - 适用于标记实体状态、类别或特征
 * - 可用于快速过滤和识别特定实体
 * - 比存储完整组件数据更节省资源
 * 
 * 典型应用场景包括：玩家实体标记、可交互物体标记、危险实体标记等。
 */
abstract class EntityTag : ComponentType<Boolean>()

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
}



