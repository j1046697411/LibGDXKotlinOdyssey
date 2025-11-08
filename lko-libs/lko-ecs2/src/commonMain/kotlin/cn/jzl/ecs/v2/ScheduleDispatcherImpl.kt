package cn.jzl.ecs.v2

import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.time.Duration

/**
 * 调度器分发器的实现类，负责管理初始化任务、下一帧任务和延迟任务的执行
 *
 * 此类实现了优先级队列机制，支持不同优先级的任务调度，确保高优先级任务优先执行
 * 使用对象池技术优化任务对象的创建和销毁，提高性能
 * 实现了基于依赖关系的任务并行执行策略
 *
 * @constructor 创建新的调度器分发器实例
 */
class ScheduleDispatcherImpl : ScheduleDispatcher {

    /**
     * 初始化任务列表，存储所有待执行的初始化任务
     * 初始化任务会在下一帧开始时执行，使用优先队列管理优先级
     */
    private val mainTasks = ObjectFastList<FrameTask>(256)
    
    /**
     * 下一帧任务列表，存储所有待在下一帧执行的任务
     * 包含需要在下一帧更新时处理的常规任务
     */
    private val workTasks = ObjectFastList<FrameTask>(256)
    
    /**
     * 延迟任务列表，存储所有需要延迟执行的任务
     * 每个任务都有剩余延迟时间，到期后会被移至下一帧任务列表
     */
    private val delayTasks = ObjectFastList<DelayFrameTask>(64)

    /**
     * 当前帧正在处理的任务列表
     * 在执行任务时临时使用，避免在执行过程中修改原任务列表
     */
    private val currentFrameTasks = ObjectFastList<FrameTask>(256)

    /**
     * 用于保护任务列表并发访问的可重入锁
     * 确保在多线程环境下安全地添加和处理任务
     */
    private val lock = ReentrantLock()

    private val frameTaskExecutor = FrameTaskExecutor(256)

    /**
     * 添加初始化任务
     *
     * 初始化任务会在下一帧开始时立即执行，没有优先级区分
     * 主要用于调度器的初始化和协程的启动
     *
     * @param scheduleDescriptor 调度器描述符，用于标识任务来源
     * @param task 要执行的任务函数，接收持续时间参数
     */
    override fun addMainTask(scheduleDescriptor: ScheduleDescriptor, priority: ScheduleTaskPriority, task: (Duration) -> Unit) = lock.withLock {
        mainTasks.insertLast(FrameTask(scheduleDescriptor, priority, task))
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
    override fun addWorkTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        task: (Duration) -> Unit
    ): Unit = lock.withLock { addWorkTask(FrameTask(scheduleDescriptor, priority, task)) }

    /**
     * 添加帧任务到下一帧任务列表
     * 此方法是内部实现，用于将FrameTask对象添加到工作任务列表并更新依赖关系
     * 
     * @param frameTask 要添加的帧任务对象
     */
    private fun addWorkTask(frameTask: FrameTask) {
        workTasks.insertLast(frameTask)
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
    ): Unit = lock.withLock {
        delayTasks.insertLast(DelayFrameTask(delay, FrameTask(scheduleDescriptor, priority, task)))
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
        executeMainTasks(delta)
        // 更新延迟任务
        updateDelayTasks(delta)
        // 执行下一帧任务
        executeWorkTasks(delta)
    }

    /**
     * 执行所有初始化任务
     *
     * 初始化任务按添加顺序执行，执行完成后清空任务列表
     *
     * @param delta 时间增量，传递给任务函数
     */
    private fun executeMainTasks(delta: Duration) {
        if (mainTasks.isEmpty()) return
        currentFrameTasks.insertLastAll(mainTasks)
        mainTasks.clear()
        currentFrameTasks.sortedBy { it.priority.priority }
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
    private fun updateDelayTasks(delta: Duration) {
        if (delayTasks.isEmpty()) return
        val iterator = delayTasks.iterator()
        while (iterator.hasNext()) {
            val delayTask = iterator.next()
            delayTask.remainingDelay -= delta
            if (delayTask.remainingDelay <= Duration.Companion.ZERO) {
                addWorkTask(delayTask.frameTask)
                iterator.remove()
            }
        }
    }

    /**
     * 执行所有下一帧任务
     * 
     * 此方法负责协调整个任务执行过程，包含以下关键步骤：
     * 1. 首先调用 updateFrameTaskDependencies() 更新所有任务的依赖关系
     * 2. 启动一个带有 FrameTaskContext 的协程来处理任务的拓扑排序执行
     * 3. 使用 frameCoroutineScheduler.processAllTasks() 确保所有任务都能被处理
     * 
     * 这种设计允许：
     * - 任务可以并行执行，提高处理效率
     * - 任务执行过程中可以让出线程，避免阻塞主线程
     * - 在工作任务积累过多时，可以利用当前线程协助执行任务
     * 
     * @param delta 时间增量，传递给每个任务函数
     */
    private fun executeWorkTasks(delta: Duration) {
        currentFrameTasks.clear()
        currentFrameTasks.addAll(workTasks)
        workTasks.clear()
        frameTaskExecutor.executePendingTasks(currentFrameTasks, delta)
        currentFrameTasks.clear()
    }

    /**
     * 帧任务数据类，封装任务的基本信息
     * 
     * @property scheduleDescriptor 调度器描述符，包含任务来源和依赖信息
     * @property priority 任务优先级，决定任务的执行顺序
     * @property task 要执行的任务函数，接收持续时间参数
     */
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