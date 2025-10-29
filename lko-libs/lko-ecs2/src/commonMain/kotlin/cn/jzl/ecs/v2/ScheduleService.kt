package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.datastructure.math.fromLowHigh
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
value class Schedule internal constructor(private val data: Long) {
    /**
     * 调度器的唯一标识符
     */
    val id: Int get() = data.low

    /**
     * 调度器的版本号，用于版本控制
     */
    val version: Int get() = data.high

    companion object {
        operator fun invoke(id: Int, version: Int = 0): Schedule = Schedule(Long.fromLowHigh(id, version))
    }
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
 * 调度任务作用域接口，提供任务级别的操作和控制
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
 * 调度器作用域接口的实现类
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

/**
 * 调度服务类，负责管理ECS系统中的调度器生命周期和任务执行
 *
 * 此类提供了调度器的创建、管理和更新功能，支持初始化任务、下一帧任务和延迟任务的调度
 * 使用对象池技术优化调度器的创建和销毁，提高性能
 *
 * @property world 世界实例，用于获取调度器分发器和实体组件上下文
 */
class ScheduleService(private val world: World) {

    /**
     * 调度器分发器实例，负责实际的任务调度执行
     */
    private val scheduleDispatcher by world.instance<ScheduleDispatcher>()
    
    /**
     * 调度器对象池，存储所有已创建的调度器实例
     * 初始容量为1024，支持动态扩容
     */
    private val schedules = ObjectFastList<Schedule>(1024)
    
    /**
     * 可回收的调度器ID列表，用于对象池优化
     * 当调度器被销毁时，其ID会被添加到该列表以供重用
     */
    private val recycleScheduleIds = IntFastList(128)
    
    /**
     * 活跃调度器位图，用于快速判断调度器是否处于活跃状态
     * 每个位对应一个调度器ID，设置为1表示活跃，0表示非活跃
     */
    private val activeSchedules = BitSet(1024)

    /**
     * 创建新的调度器实例
     * 
     * 优先从回收池中获取可重用的调度器ID，如果回收池为空则创建新的调度器
     * 新创建的调度器会被标记为活跃状态并添加到调度器列表中
     *
     * @return 新创建的调度器实例
     */
    private fun createSchedule(): Schedule {
        val schedule = if (recycleScheduleIds.isNotEmpty()) {
            val id = recycleScheduleIds.removeLast()
            schedules[id].upgrade()
        } else {
            Schedule(schedules.size, 0).also { schedules.insertLast(it) }
        }
        schedules[schedule.id] = schedule
        activeSchedules.set(schedule.id)
        return schedule
    }

    /**
     * 升级调度器版本
     * 
     * 当重用调度器ID时，需要升级其版本号以确保唯一性
     * 版本号递增，避免版本冲突
     *
     * @receiver 要升级的调度器实例
     * @return 升级后的新调度器实例
     */
    private fun Schedule.upgrade(): Schedule = Schedule(id, version + 1)

    /**
     * 创建并启动一个新的调度器
     * 
     * 创建调度器实例，设置调度器作用域，并添加初始化任务
     * 调度器会在下一帧开始执行指定的代码块
     *
     * @param scheduleName 调度器名称，用于标识和调试
     * @param block 要在调度器中执行的协程代码块
     * @return 新创建的调度器实例
     */
    fun schedule(scheduleName: String, block: suspend ScheduleScope.() -> Unit): Schedule {
        val schedule = createSchedule()
        val scheduleScope = ScheduleScopeImpl(world, schedule, scheduleName, scheduleDispatcher)
        scheduleDispatcher.addInitializeTask(scheduleScope.scheduleDescriptor) {
            block.startCoroutine(scheduleScope, Continuation(EmptyCoroutineContext) {})
        }
        return schedule
    }

    /**
     * 更新所有调度器的状态
     * 
     * 调用调度器分发器的更新方法，处理所有待执行的任务
     * 包括初始化任务、下一帧任务和延迟任务的执行
     *
     * @param delta 时间增量，表示自上次更新以来经过的时间
     */
    fun update(delta: Duration): Unit = scheduleDispatcher.update(delta)
}

/**
 * 调度器分发器的实现类，负责管理初始化任务、下一帧任务和延迟任务的执行
 * 
 * 此类实现了优先级队列机制，支持不同优先级的任务调度，确保高优先级任务优先执行
 * 使用对象池技术优化任务对象的创建和销毁，提高性能
 * 
 * @constructor 创建新的调度器分发器实例
 */
class ScheduleDispatcherImpl : ScheduleDispatcher {
    
    private val initializeTasks = ObjectFastList<FrameTask>()
    private val nextFrameTasks = ObjectFastList<FrameTask>()
    private val delayFrameTasks = ObjectFastList<DelayFrameTask>()

    private val currentFrameTasks = ObjectFastList<FrameTask>(1024)
    

    /**
     * 添加初始化任务
     * 
     * 初始化任务会在下一帧开始时立即执行，没有优先级区分
     * 主要用于调度器的初始化和协程的启动
     * 
     * @param scheduleDescriptor 调度器描述符，用于标识任务来源
     * @param task 要执行的任务函数，接收持续时间参数
     */
    override fun addInitializeTask(scheduleDescriptor: ScheduleDescriptor, task: (Duration) -> Unit) {
        initializeTasks.insertLast(FrameTask(scheduleDescriptor, ScheduleTaskPriority.NORMAL, task))
    }
    
    /**
     * 添加下一帧执行的任务
     * 
     * 下一帧任务会在下一帧更新时执行，支持优先级控制
     * 高优先级任务会优先执行，相同优先级的任务按添加顺序执行
     * 
     * @param scheduleDescriptor 调度器描述符，用于标识任务来源
     * @param priority 任务优先级，决定任务的执行顺序
     * @param task 要执行的任务函数，接收持续时间参数
     */
    override fun addNextFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        task: (Duration) -> Unit
    ) {
        nextFrameTasks.insertLast(FrameTask(scheduleDescriptor, priority, task))
    }
    
    /**
     * 添加延迟指定帧数后执行的任务
     * 
     * 延迟任务会在指定的延迟时间过后执行，支持优先级控制
     * 延迟时间以帧为单位计算，实际执行时间取决于帧率
     * 
     * @param scheduleDescriptor 调度器描述符，用于标识任务来源
     * @param priority 任务优先级，决定任务的执行顺序
     * @param delay 延迟时间，表示需要等待的时间
     * @param task 要执行的任务函数，接收持续时间参数
     */
    override fun addDelayFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        delay: Duration,
        task: (Duration) -> Unit
    ) {
        delayFrameTasks.insertLast(DelayFrameTask(delay, FrameTask(scheduleDescriptor, priority, task)))
    }
    
    /**
     * 更新所有任务的状态
     * 
     * 此方法会按顺序执行以下操作：
     * 1. 执行所有初始化任务
     * 2. 按优先级执行下一帧任务
     * 3. 更新延迟任务的剩余时间，执行到期的延迟任务
     * 
     * @param delta 时间增量，表示自上次更新以来经过的时间
     */
    override fun update(delta: Duration) {
        // 执行初始化任务
        executeInitializeTasks(delta)
        
        // 执行下一帧任务
        executeNextFrameTasks(delta)
        
        // 更新延迟任务
        updateDelayFrameTasks(delta)
    }
    
    /**
     * 执行所有初始化任务
     * 
     * 初始化任务按添加顺序执行，执行完成后清空任务列表
     * 
     * @param delta 时间增量，传递给任务函数
     */
    private fun executeInitializeTasks(delta: Duration) {
        if (initializeTasks.isEmpty()) return
        currentFrameTasks.insertLastAll(initializeTasks)
        initializeTasks.clear()
        currentFrameTasks.forEach { task -> task.task(delta) }
        currentFrameTasks.clear()
    }
    
    /**
     * 执行所有下一帧任务
     * 
     * 按优先级从高到低的顺序执行任务，相同优先级的任务按添加顺序执行
     * 执行完成后清空所有下一帧任务列表
     * 
     * @param delta 时间增量，传递给任务函数
     */
    private fun executeNextFrameTasks(delta: Duration) {
        if (nextFrameTasks.isNotEmpty()) {
            currentFrameTasks.insertLastAll(nextFrameTasks)
            nextFrameTasks.clear()
        }
        if (currentFrameTasks.isEmpty()) return
        currentFrameTasks.sortByDescending { it.priority }
        currentFrameTasks.forEach { task -> task.task(delta) }
        currentFrameTasks.clear()
    }
    
    /**
     * 更新所有延迟任务的状态
     * 
     * 减少延迟任务的剩余时间，执行到期的延迟任务
     * 未到期的延迟任务保留在列表中继续等待
     * 
     * @param delta 时间增量，用于减少延迟任务的剩余时间
     */
    private fun updateDelayFrameTasks(delta: Duration) {
        if (delayFrameTasks.isEmpty()) return
        val iterator = delayFrameTasks.iterator()
        while (iterator.hasNext()) {
            val delayTask = iterator.next()
            delayTask.remainingDelay -= delta
            if (delayTask.remainingDelay <= Duration.ZERO) {
                currentFrameTasks.insertLast(delayTask.frameTask)
                iterator.remove()
            }
        }
    }

    data class FrameTask(
        val scheduleDescriptor: ScheduleDescriptor,
        val priority: ScheduleTaskPriority,
        val task: (Duration) -> Unit
    )

    /**
     * 延迟任务数据类，封装延迟任务的详细信息
     *
     * @property remainingDelay 剩余延迟时间
     * @property frameTask 要执行的任务函数
     */
    private data class DelayFrameTask(
        var remainingDelay: Duration,
        val frameTask: FrameTask
    )
}