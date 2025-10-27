package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import cn.jzl.di.instance
import kotlin.coroutines.*
import kotlin.jvm.JvmInline
import kotlin.time.Duration

/**
 * 表示一个调度器，用于管理ECS系统中的任务调度
 *
 * @property data 内部存储的64位数据，包含调度器的ID和版本信息
 */
@JvmInline
value class Schedule(private val data: Long) {
    /**
     * 调度器的唯一标识符
     */
    val id: Int get() = data.low

    /**
     * 调度器的版本号，用于版本控制
     */
    val version: Int get() = data.high
}

/**
 * 表示对组件类型的只读访问权限
 *
 * @property type 组件类型
 * @param C 组件类型参数
 */
@JvmInline
value class ReadAccesses<C> internal constructor(val type: ComponentType<C>)

/**
 * 表示对组件类型的写访问权限
 *
 * @property type 组件类型
 * @param C 组件类型参数
 */
@JvmInline
value class WriteAccesses<C> internal constructor(val type: ComponentType<C>)

/**
 * DSL标记注解，用于调度器DSL的上下文限制
 */
@DslMarker
annotation class ScheduleDsl

/**
 * 调度器评分接口，用于定义调度器的访问权限和任务配置
 *
 * 此接口限制了挂起函数的调用范围，确保只能在调度器上下文中使用
 */
@RestrictsSuspension
@ScheduleDsl
interface ScheduleScope {

    /**
     * 当前调度器实例
     */
    val schedule: Schedule

    /**
     * 获取对指定组件类型的写访问权限
     *
     * @param C 组件类型参数
     * @return 写访问权限实例
     */
    val <C> ComponentType<C>.write: WriteAccesses<C>

    /**
     * 获取对指定组件类型的只读访问权限
     *
     * @param C 组件类型参数
     * @return 只读访问权限实例
     */
    val <C> ComponentType<C>.read: ReadAccesses<C>

    /**
     * 定义并创建一个实体家族
     *
     * @param configuration 家族配置的DSL函数
     * @return 创建的家族实例
     */
    fun family(configuration: FamilyDefinition.() -> Unit): Family

    /**
     * 在调度器上下文中挂起协程
     *
     * @param block 要在世界上下文中执行的代码块
     * @return 代码块的执行结果
     * @param R 返回类型参数
     */
    suspend fun <R> suspendScheduleCoroutine(block: World.(Continuation<R>) -> Unit): R

    /**
     * 在指定优先级下执行任务
     *
     * @param priority 任务优先级，默认为普通优先级
     * @param block 要执行的任务代码块
     * @return 任务的执行结果
     * @param R 返回类型参数
     */
    suspend fun <R> withTask(priority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL, block: suspend ScheduleTaskScope.() -> R): R
}

/**
 * 在调度器中执行循环任务
 *
 * @param priority 循环任务的优先级
 * @param block 循环执行的代码块，包含循环控制器参数
 * @receiver 调度器评分实例
 */
suspend fun ScheduleScope.withLoop(
    priority: ScheduleTaskPriority,
    block: suspend ScheduleTaskScope.(looper: ScheduleTaskLooper) -> Unit
): Unit = withTask(priority) {
    var loop = true
    val looper = ScheduleTaskLooper { loop = false }
    while (loop) {
        block(looper)
        waitNextFrame()
    }
}

/**
 * 调度任务评分接口，提供任务级别的操作和控制
 *
 * 此接口扩展了实体组件上下文，允许在任务中访问和修改实体组件
 */
@RestrictsSuspension
@ScheduleDsl
interface ScheduleTaskScope : EntityComponentContext {
    /**
     * 等待下一帧执行
     *
     * @return 等待的持续时间
     */
    suspend fun waitNextFrame(): Duration

    /**
     * 延迟指定时间后执行
     *
     * @param delay 要延迟的时间
     */
    suspend fun delay(delay: Duration)

    /**
     * 在任务上下文中挂起协程
     *
     * @param block 要在世界上下文中执行的代码块
     * @return 代码块的执行结果
     * @param R 返回类型参数
     */
    suspend fun <R> suspendScheduleCoroutine(block: World.(Continuation<R>) -> Unit): R
}

/**
 * 调度任务循环控制器接口
 *
 * 用于控制循环任务的执行
 */
fun interface ScheduleTaskLooper {
    /**
     * 停止循环执行
     */
    fun stop()
}

/**
 * 调度任务优先级枚举
 *
 * @property priority 优先级数值，数值越小优先级越高
 */
enum class ScheduleTaskPriority(val priority: Int) {
    HIGHEST(0),    // 最高优先级，用于紧急任务
    HIGH(1),       // 高优先级，用于重要任务
    NORMAL(2),     // 普通优先级，默认优先级
    LOW(3),        // 低优先级，用于后台任务
    LOWEST(4);     // 最低优先级，用于非关键任务,
}

/**
 * 调度器描述符，包含调度器的完整配置信息
 *
 * @property schedule 调度器实例
 * @property scheduleName 调度器名称
 * @property readAccesses 只读访问权限集合
 * @property writeAccesses 写访问权限集合
 * @property familyDefinitions 家族定义集合
 */
data class ScheduleDescriptor(
    val schedule: Schedule,
    val scheduleName: String,
    val readAccesses: MutableSet<ReadAccesses<*>> = mutableSetOf(),
    val writeAccesses: MutableSet<WriteAccesses<*>> = mutableSetOf(),
    val familyDefinitions: MutableSet<FamilyDefinition> = mutableSetOf()
)

/**
 * 调度器分发器接口，负责调度任务的执行管理
 */
interface ScheduleDispatcher {

    /**
     * 添加初始化任务
     *
     * @param scheduleDescriptor 调度器描述符
     * @param task 要执行的任务函数，接收持续时间参数
     */
    fun addInitializeTask(
        scheduleDescriptor: ScheduleDescriptor,
        task: (Duration) -> Unit
    )

    /**
     * 添加下一帧执行的任务
     *
     * @param scheduleDescriptor 调度器描述符
     * @param priority 任务优先级
     * @param task 要执行的任务函数，接收持续时间参数
     */
    fun addNextFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        task: (Duration) -> Unit
    )

    /**
     * 添加延迟指定帧数后执行的任务
     *
     * @param scheduleDescriptor 调度器描述符
     * @param priority 任务优先级
     * @param delay 延迟时间
     * @param task 要执行的任务函数，接收持续时间参数
     */
    fun addDelayFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        delay: Duration,
        task: (Duration) -> Unit
    )

    fun update(delta: Duration)
}


/**
 * 调度器评分接口的实现类
 *
 * @property world 世界实例
 * @property schedule 调度器实例
 * @property scheduleName 调度器名称
 * @property scheduleDispatcher 调度器分发器
 */
class ScheduleScopeImpl(
    private val world: World,
    schedule: Schedule,
    scheduleName: String,
    private val scheduleDispatcher: ScheduleDispatcher
) : ScheduleScope {
    internal val scheduleDescriptor = ScheduleDescriptor(schedule, scheduleName)

    /**
     * 当前调度器实例
     */
    override val schedule: Schedule get() = scheduleDescriptor.schedule

    /**
     * 获取对指定组件类型的写访问权限
     *
     * @param C 组件类型参数
     * @return 写访问权限实例
     */
    override val <C> ComponentType<C>.write: WriteAccesses<C>
        get() = WriteAccesses(this).also { scheduleDescriptor.writeAccesses.add(it) }

    /**
     * 获取对指定组件类型的只读访问权限
     *
     * @param C 组件类型参数
     * @return 只读访问权限实例
     */
    override val <C> ComponentType<C>.read: ReadAccesses<C>
        get() = ReadAccesses(this).also { scheduleDescriptor.readAccesses.add(it) }

    /**
     * 定义并创建一个实体家族
     *
     * @param configuration 家族配置的DSL函数
     * @return 创建的家族实例
     */
    override fun family(configuration: FamilyDefinition.() -> Unit): Family {
        val family = world.family(configuration)
        scheduleDescriptor.familyDefinitions.add(family.familyDefinition)
        return family
    }

    /**
     * 在调度器上下文中挂起协程
     *
     * @param block 要在世界上下文中执行的代码块
     * @return 代码块的执行结果
     * @param R 返回类型参数
     */
    override suspend fun <R> suspendScheduleCoroutine(
        block: World.(Continuation<R>) -> Unit
    ): R = suspendCoroutine { continuation ->
        world.block(Continuation(continuation.context) { result ->
            scheduleDispatcher.addInitializeTask(scheduleDescriptor) { continuation.resumeWith(result) }
        })
    }

    /**
     * 在指定优先级下执行任务
     *
     * @param priority 任务优先级
     * @param block 要执行的任务代码块
     * @return 任务的执行结果
     * @param R 返回类型参数
     */
    override suspend fun <R> withTask(
        priority: ScheduleTaskPriority,
        block: suspend ScheduleTaskScope.() -> R
    ): R = suspendScheduleCoroutine { continuation ->
        val scheduleTaskScore = ScheduleTaskScopeImpl(priority)
        scheduleDispatcher.addNextFrameTask(scheduleDescriptor, priority) {
            block.startCoroutine(scheduleTaskScore, continuation)
        }
    }

    /**
     * 调度任务评分接口的内部实现类
     *
     * @property priority 任务优先级
     */
    private inner class ScheduleTaskScopeImpl(
        private val priority: ScheduleTaskPriority
    ) : ScheduleTaskScope, EntityComponentContext by world.entityUpdateContext {
        val scheduleDispatcher get() = this@ScheduleScopeImpl.scheduleDispatcher
        val scheduleDescriptor get() = this@ScheduleScopeImpl.scheduleDescriptor

        /**
         * 等待下一帧执行
         *
         * @return 等待的持续时间
         */
        override suspend fun waitNextFrame(): Duration = suspendCoroutine { continuation ->
            scheduleDispatcher.addNextFrameTask(scheduleDescriptor, priority) {
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
                scheduleDispatcher.addNextFrameTask(scheduleDescriptor, priority) {
                    continuation.resumeWith(result)
                }
            }
            world.block(scheduleContinuation)
        }
    }
}

class ScheduleService(private val world: World) {

    private val scheduleDispatcher by world.instance<ScheduleDispatcher>()

    private fun createSchedule(): Schedule {
        return Schedule(0)
    }

    fun schedule(scheduleName: String, block: suspend ScheduleScope.() -> Unit): Schedule {
        val schedule = createSchedule()
        val scheduleScope = ScheduleScopeImpl(world, schedule, scheduleName, scheduleDispatcher)
        scheduleDispatcher.addInitializeTask(scheduleScope.scheduleDescriptor) {
            block.startCoroutine(scheduleScope, Continuation(EmptyCoroutineContext) {})
        }
        return schedule
    }

    fun update(delta: Duration): Unit = scheduleDispatcher.update(delta)
}