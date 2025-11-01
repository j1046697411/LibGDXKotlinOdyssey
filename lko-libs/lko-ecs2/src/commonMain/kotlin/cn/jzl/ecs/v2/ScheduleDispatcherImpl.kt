package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
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

    /**
     * 帧协程调度器，负责管理任务的执行线程
     * 提供基于帧的任务调度机制，确保任务在正确的帧周期内执行
     */
    private val frameCoroutineScheduler = FrameCoroutineScheduler()
    
    /**
     * 帧协程作用域，使用帧协程调度器和监督作业
     * 用于启动和管理任务执行的协程，确保任务可以安全地取消和异常传播
     */
    private val frameCoroutineScope = CoroutineScope(frameCoroutineScheduler + SupervisorJob())

    /**
     * 剩余帧任务位掩码，用于跟踪未完成的任务
     * 使用位运算高效地管理任务依赖关系
     */
    private val remainingFrameTasks = BitSet()
    
    /**
     * 保护剩余帧任务位掩码的可重入锁
     * 确保在多线程环境下安全地更新任务完成状态
     */
    private val remainingLock = ReentrantLock()


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
        updateFrameTaskDependencies(frameTask)
    }

    /**
     * 更新指定帧任务的依赖关系
     * 当添加新任务时，检查其依赖配置是否需要更新
     * 
     * @param frameTask 需要更新依赖关系的帧任务
     */
    private fun updateFrameTaskDependencies(frameTask: FrameTask) {
        val scheduleDescriptor = frameTask.scheduleDescriptor
        val frameTaskDependency = frameTaskDependencies.getOrNull(scheduleDescriptor.schedule.id)
        if (scheduleDescriptor.version != frameTaskDependency?.version) {
            dirtyData.value = true
            val newFrameTaskDependency = frameTaskDependency ?: FrameTaskDependency(scheduleDescriptor).also {
                frameTaskDependencies.ensureCapacity(scheduleDescriptor.schedule.id + 1, null)
            }
            newFrameTaskDependency.resetDependencies()
            newFrameTaskDependency.version = scheduleDescriptor.version
            frameTaskDependencies[scheduleDescriptor.schedule.id] = newFrameTaskDependency
        }
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
        updateFrameTaskDependencies()
        if (currentFrameTasks.isEmpty()) return
        // 启动协程处理任务，包含FrameTaskContext的任务说明需要在processAllTasks循环中执行。不需要考虑锁的问题。
        frameCoroutineScope.launch(FrameCoroutineScheduler.FrameTaskContext) {
            processTasksInTopologicalOrder(delta)
        }

        // 等待所有任务处理完成，并且工作任务积累过多时，使用当前线程执行工作任务。
        frameCoroutineScheduler.processAllTasks { currentFrameTasks.isNotEmpty() }
    }

    /**
     * 按拓扑顺序处理任务
     * 
     * 此方法实现了基于依赖关系的任务调度算法，使用拓扑排序确保任务按照正确的依赖顺序执行：
     * 1. 优先执行无依赖或依赖已完成的任务
     * 2. 对于存在循环依赖的情况，尝试执行不涉及强依赖的任务
     * 3. 在无法解析依赖的情况下，强制执行队列中的第一个任务以避免死锁
     * 
     * @param delta 时间增量，表示自上次更新以来经过的时间，传递给每个任务函数
     */
    private suspend fun processTasksInTopologicalOrder(delta: Duration) {
        val count = atomic(0)
        while (currentFrameTasks.isNotEmpty()) {
            val iterator = currentFrameTasks.iterator()
            // 找出所有可执行的任务（没有未完成的依赖）
            while (iterator.hasNext()) {
                val task = iterator.next()
                val dependency = getOrCreateFrameTaskDependency(task)
                if (!dependency.dependencies.intersects(remainingFrameTasks)) {
                    // 启动任务协程执行，根据不同的调度，有可能在当前线程执行，也有可能在其他任务线程执行。
                    frameCoroutineScope.launch { task.task(delta) }.invokeOnCompletion {
                        count.decrementAndGet()
                        remainingLock.withLock { remainingFrameTasks.clear(task.scheduleDescriptor.schedule.id) }
                    }
                    iterator.remove()
                    count.incrementAndGet()
                }
            }
            // 如果没有可执行中的任务但是列表不为空，说明存在循环依赖，取不存在强依赖的任务执行
            if (count.value == 0 && currentFrameTasks.isNotEmpty()) {
                val iterator = currentFrameTasks.iterator()
                val size = currentFrameTasks.size
                while (iterator.hasNext()) {
                    val task = iterator.next()
                    val dependency = getOrCreateFrameTaskDependency(task)
                    if (!dependency.strongDependencies.intersects(remainingFrameTasks)) {
                        // 启动任务协程执行，根据不同的调度，有可能在当前线程执行，也有可能在其他任务线程执行。
                        task.task(delta)
                        remainingFrameTasks.clear(task.scheduleDescriptor.schedule.id)
                        iterator.remove()
                        break
                    }
                }
                // 全部存在强依赖，取第一个任务执行
                if (size == currentFrameTasks.size) {
                    val task = currentFrameTasks.removeFirst()
                    task.task(delta)
                    remainingFrameTasks.clear(task.scheduleDescriptor.schedule.id)
                }
            }
            // 把当前线程的协程让出去，有机会当作工作线程处理其他任务
            yield()
        }
    }

    /**
     * 更新所有任务的依赖关系
     * 此方法在任务执行前调用，重新计算所有任务之间的依赖关系
     * 使用dirtyData标志避免不必要的重复计算
     */
    private fun updateFrameTaskDependencies() {
        // 更新任务依赖关系
        if (dirtyData.compareAndSet(expect = true, update = false)) {
            // 优化依赖计算：只计算实际存在的任务之间的依赖
            for (i in 0 until frameTaskDependencies.size) {
                val dependency1 = frameTaskDependencies.getOrNull(i) ?: continue
                for (j in i + 1 until frameTaskDependencies.size) {
                    val dependency2 = frameTaskDependencies.getOrNull(j) ?: continue
                    // 计算双向依赖关系
                    if (dependency1.scheduleDescriptor.isDependency(dependency2.scheduleDescriptor)) {
                        dependency1.dependencies.set(dependency2.scheduleDescriptor.schedule.id)
                        // 检查是否为强依赖
                        if (dependency1.scheduleDescriptor.isStrongDependency(dependency2.scheduleDescriptor)) {
                            dependency1.strongDependencies.set(dependency2.scheduleDescriptor.schedule.id)
                        }
                        continue
                    }
                    if (dependency2.scheduleDescriptor.isDependency(dependency1.scheduleDescriptor)) {
                        dependency2.dependencies.set(dependency1.scheduleDescriptor.schedule.id)
                        // 检查是否为强依赖
                        if (dependency2.scheduleDescriptor.isStrongDependency(dependency1.scheduleDescriptor)) {
                            dependency2.strongDependencies.set(dependency1.scheduleDescriptor.schedule.id)
                        }
                        continue
                    }
                }
            }
        }

        // 处理下一帧任务
        if (workTasks.isNotEmpty()) {
            // 清空当前任务列表并转移下一帧任务
            currentFrameTasks.clear()
            currentFrameTasks.insertLastAll(workTasks)
            workTasks.clear()

            // 重置剩余任务标记
            remainingFrameTasks.clear()
            currentFrameTasks.forEach { task ->
                remainingFrameTasks.set(task.scheduleDescriptor.schedule.id)
            }
        }
    }

    /**
     * 任务依赖关系列表，存储每个调度器的依赖信息
     * 使用索引对应调度器ID，实现快速查找
     */
    private val frameTaskDependencies = ObjectFastList<FrameTaskDependency?>()
    
    /**
     * 脏数据标志，用于标识依赖关系是否需要重新计算
     * 使用原子变量确保线程安全
     */
    private val dirtyData = atomic(false)

    /**
     * 获取或创建帧任务的依赖关系对象
     * 此方法首先尝试获取已存在的依赖对象，如果不存在则创建新的
     * 
     * @param task 需要获取依赖关系的帧任务
     * @return 对应的帧任务依赖关系对象
     */
    private fun getOrCreateFrameTaskDependency(task: FrameTask): FrameTaskDependency {
        val scheduleDescriptor = task.scheduleDescriptor
        val id = scheduleDescriptor.schedule.id

        // 快速路径：直接获取已存在的依赖
        val existingDependency = frameTaskDependencies.getOrNull(id)
        if (existingDependency != null) {
            // 检查版本是否需要更新
            if (existingDependency.version != scheduleDescriptor.version) {
                existingDependency.version = scheduleDescriptor.version
                dirtyData.value = true // 标记依赖需要重新计算
            }
            return existingDependency
        }

        // 创建新的依赖对象
        frameTaskDependencies.ensureCapacity(id + 1, null)
        val dependency = FrameTaskDependency(scheduleDescriptor)
        frameTaskDependencies[id] = dependency
        dirtyData.value = true // 新依赖需要计算关系
        return dependency
    }

    /**
     * 帧任务数据类，封装任务的基本信息
     * 
     * @property scheduleDescriptor 调度器描述符，包含任务来源和依赖信息
     * @property priority 任务优先级，决定任务的执行顺序
     * @property task 要执行的任务函数，接收持续时间参数
     */
    private data class FrameTask(
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

    /**
     * 帧任务依赖数据类，管理调度器之间的依赖关系
     * 
     * @property scheduleDescriptor 调度器描述符，包含依赖配置信息
     */
    private data class FrameTaskDependency(val scheduleDescriptor: ScheduleDescriptor) {
        /**
         * 强依赖位掩码，表示必须严格按照顺序执行的依赖关系
         * 在循环依赖情况下，强依赖任务不会被强制执行
         */
        val strongDependencies: BitSet = BitSet()
        
        /**
         * 普通依赖位掩码，表示一般的依赖关系
         * 用于确定任务的执行顺序，但在必要时可以绕过
         */
        val dependencies: BitSet = BitSet()
        
        /**
         * 版本号，用于检测依赖关系是否需要更新
         * 与调度描述符的版本保持同步
         */
        var version: Int = -1

        /**
         * 重置所有依赖关系
         * 当调度描述符版本变更时调用，清除所有现有依赖信息
         */
        fun resetDependencies() {
            dependencies.clear()
            strongDependencies.clear()
            version = -1
        }
    }
}