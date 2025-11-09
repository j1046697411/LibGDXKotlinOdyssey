package cn.jzl.ecs.v2

import cn.jzl.di.instance
import kotlin.coroutines.*
import kotlin.time.Duration

/**
 * ScheduleScope.kt 定义了调度器作用域的核心接口
 * 
 * 调度器作用域是ECS系统中调度器上下文的核心接口，提供了：
 * 1. 组件读写访问的权限控制机制
 * 2. 协程挂起与恢复的底层支持
 * 3. 实体家族创建的能力
 * 4. 调度任务优先级与分发控制
 * 
 * 此接口通过@RestrictsSuspension注解限制了挂起函数的调用范围，确保只能在调度器上下文中使用，
 * 同时使用@ScheduleDsl注解提供了流畅的DSL体验。
 * 
 * 在ECS架构中，ScheduleScope作为连接系统、实体和组件的桥梁，使得开发者能够在调度环境中安全地
 * 访问和操作ECS世界中的各种元素，同时维持系统的并发安全性和性能。
 */
@RestrictsSuspension
@ScheduleDsl
interface ScheduleScope {

    /**
     * 当前调度器实例
     * 
     * 提供对当前正在执行的调度器的直接访问，可用于获取调度器的状态和配置信息
     */
    val schedule: Schedule

    /**
     * 获取对指定组件类型的写访问权限
     *
     * 此属性提供了类型安全的组件写访问机制，允许在调度器上下文中修改实体组件
     *
     * @param C 组件类型参数
     * @return 写访问权限实例，用于修改组件数据
     */
    val <C> ComponentType<C>.write: ComponentWriteAccesses<C>

    /**
     * 获取对指定组件类型的只读访问权限
     *
     * 此属性提供了类型安全的组件只读访问机制，适合只需读取组件数据的场景
     *
     * @param C 组件类型参数
     * @return 只读访问权限实例，用于安全地读取组件数据
     */
    val <C> ComponentType<C>.read: ComponentReadAccesses<C>

    /**
     * 挂起当前调度器协程，等待指定条件满足
     *
     * 这是调度器中最基础的协程挂起机制，允许任务在满足特定条件前暂停执行
     * 并在条件满足后恢复，常用于帧同步、事件响应等场景
     *
     * @param dispatcherType 指定使用的调度器类型，默认为Work类型
     * @param priority 指定任务优先级，默认为普通优先级
     * @param block 用于检查条件的lambda表达式，在World上下文中执行
     * @return 当条件满足时返回的结果
     */
    suspend fun <R> suspendScheduleCoroutine(
        dispatcherType: DispatcherType = DispatcherType.Work,
        priority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
        block: World.(ScheduleContinuation<R>) -> Unit
    ): R

    /**
     * 定义并创建一个实体家族
     *
     * 通过DSL方式配置实体过滤条件，创建符合条件的实体集合视图
     * 家族是ECS系统中高效查询实体的重要机制
     *
     * @param configuration 家族配置的DSL函数，用于定义组件过滤条件
     * @return 创建的家族实例，可以用于访问符合条件的实体
     */
    fun family(configuration: FamilyDefinition.() -> Unit): Family

    /**
     * 调度器类型枚举
     *
     * 用于区分不同的调度器实例类型，控制任务的执行环境
     * - Main: 主调度器，通常用于UI相关任务
     * - Work: 工作调度器，用于后台处理和计算密集型任务
     */
    enum class DispatcherType { Main, Work }
}

suspend inline fun ScheduleScope.withTask(
    priority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
    noinline block: suspend ScheduleTaskScope.() -> Unit
): Unit = suspendScheduleCoroutine(ScheduleScope.DispatcherType.Main, priority) { scheduleContinuation ->
    val scheduleTaskScore = ScheduleTaskScopeImpl(this, scheduleContinuation.scheduleDescriptor, priority)
    scheduleContinuation.scheduleDispatcher.addWorkTask(scheduleContinuation.scheduleDescriptor, priority) {
        block.startCoroutine(scheduleTaskScore, Continuation(EmptyCoroutineContext) {
            scheduleContinuation.resume(Unit)
        })
    }
}

/**
 * 在调度器中执行循环任务
 *
 * @param priority 循环任务的优先级
 * @param block 循环执行的代码块，包含循环控制器参数
 * @receiver 调度器评分实例
 */
suspend inline fun ScheduleScope.withLoop(
    priority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
    noinline block: suspend ScheduleTaskScope.(looper: ScheduleTaskLooper) -> Unit
): Unit = withTask(priority) {
    var loop = true
    val looper = ScheduleTaskLooper { loop = false }
    while (loop) {
        block(looper)
        waitNextFrame()
    }
}

suspend inline fun ScheduleScope.create(noinline configuration: EntityCreateContext.(Entity) -> Unit): Entity = suspendScheduleCoroutine { continuation ->
    continuation.resume(entityService.create(configuration))
}

suspend inline fun ScheduleScope.create(entityId: Int, noinline configuration: EntityCreateContext.(Entity) -> Unit): Entity = suspendScheduleCoroutine { continuation ->
    continuation.resume(entityService.create(entityId, configuration))
}

suspend inline fun ScheduleScope.configure(entity: Entity, noinline configuration: EntityUpdateContext.(Entity) -> Unit): Unit = suspendScheduleCoroutine { continuation ->
    entityService.configure(entity, configuration)
    continuation.resume(Unit)
}

suspend inline fun ScheduleScope.batchConfigure(entities: Sequence<Entity>, noinline configuration: EntityUpdateContext.(Entity) -> Unit): Unit = suspendScheduleCoroutine { continuation ->
    entities.forEach { entityService.configure(it, configuration) }
    continuation.resume(Unit)
}

suspend inline fun ScheduleScope.batchConfigure(entities: Iterable<Entity>, noinline configuration: EntityUpdateContext.(Entity) -> Unit): Unit = suspendScheduleCoroutine { continuation ->
    entities.forEach { entityService.configure(it, configuration) }
    continuation.resume(Unit)
}

suspend inline fun ScheduleScope.batchConfigure(family: Family, noinline configuration: EntityUpdateContext.(Entity) -> Unit) {
    batchConfigure(family.entities, configuration)
}

suspend inline fun <T> ScheduleScope.batch(data: Sequence<T>, noinline configuration: EntityService.(T) -> Entity): List<Entity> = suspendScheduleCoroutine { continuation ->
    continuation.resume(sequence { data.forEach { yield(entityService.configuration(it)) } }.toList())
}

suspend inline fun <T> ScheduleScope.batch(data: Iterable<T>, noinline configuration: EntityService.(T) -> Entity): List<Entity> = suspendScheduleCoroutine { continuation ->
    continuation.resume(sequence { data.forEach { yield(entityService.configuration(it)) } }.toList())
}

suspend inline fun <R> ScheduleScope.batch(noinline configuration: EntityService.() -> R): R = suspendScheduleCoroutine { continuation ->
    continuation.resume(entityService.configuration())
}

suspend inline fun ScheduleScope.batchCreation(noinline configuration: suspend SequenceScope<Entity>.(EntityService) -> Unit): List<Entity> = suspendScheduleCoroutine { continuation ->
    continuation.resume(sequence { configuration(entityService) }.toList())
}

interface ScheduleContinuation<R> : Continuation<R> {
    val scheduleDispatcher: ScheduleDispatcher
    val scheduleDescriptor: ScheduleDescriptor
}

/**
 * 调度任务评分接口的内部实现类
 *
 * @property priority 任务优先级
 */
@PublishedApi
internal class ScheduleTaskScopeImpl(
    override val world: World,
    private val scheduleDescriptor: ScheduleDescriptor,
    private val priority: ScheduleTaskPriority
) : ScheduleTaskScope, EntityComponentContext by world.entityUpdateContext {
    val scheduleDispatcher by world.instance<ScheduleDispatcher>()

    /**
     * 等待下一帧执行
     *
     * @return 等待的持续时间
     */
    override suspend fun waitNextFrame(): Duration = suspendCoroutine { continuation ->
        scheduleDispatcher.addWorkTask(scheduleDescriptor, priority) {
            continuation.resume(it)
        }
    }

    /**
     * 延迟指定时间后执行
     *
     * @param delay 要延迟的时间
     */
    override suspend fun delay(delay: Duration): Unit = suspendCoroutine { continuation ->
        scheduleDispatcher.addDelayFrameTask(scheduleDescriptor, priority, delay) {
            continuation.resume(Unit)
        }
    }

    /**
     * 在任务上下文中挂起协程
     *
     * @param block 要在世界上下文中执行的代码块
     * @return 代码块的执行结果
     * @param R 返回类型参数
     */
    override suspend fun <R> suspendScheduleCoroutine(block: World.(Continuation<R>) -> Unit): R = suspendCoroutine { continuation ->
        val scheduleContinuation = Continuation(continuation.context) { result ->
            scheduleDispatcher.addWorkTask(scheduleDescriptor, priority) {
                continuation.resumeWith(result)
            }
        }
        world.block(scheduleContinuation)
    }
}