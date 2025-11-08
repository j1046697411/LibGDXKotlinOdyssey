package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Duration

class FrameTaskExecutor(initialCapacity: Int = 256) {
    private val dependencyGraph = DependencyGraph()
    private val finishedTaskCount = atomic(0) // 已完成但尚未归档的任务数量

    // 任务队列管理
    private val unfinishedTaskMask = BitSet(initialCapacity)  // 标记哪些任务尚未完成
    private val workCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // 队列索引说明：
    // [0, completedRangeEnd): 已完成并归档的任务
    // [completedRangeEnd, allocationCursor): 已分配但尚未完成的任务（正在执行中）
    // [allocationCursor, executableRangeEnd): 可执行但尚未分配的任务（入度为0）
    // [executableRangeEnd, size): 因依赖关系不可执行的任务
    private val completedRangeEnd = atomic(0) // 已完成任务的归档索引
    private val allocationCursor = atomic(0)  // 当前批次的起始索引
    private val executableRangeEnd = atomic(0)    // 可执行任务的结束索引

    private fun MutableList<ScheduleDispatcherImpl.FrameTask>.swap(to: Int, from: Int) {
        // 增加边界检查，防止索引越界
        if (to == from || to < 0 || from < 0 || to >= size || from >= size) return
        val temp = this[to]
        this[to] = this[from]
        this[from] = temp
    }

    /**
     * 执行待处理任务 - 在主线程调用
     * 设计原理：任务分配和依赖检查都在主线程完成
     */
    fun executePendingTasks(frameTasks: ObjectFastList<ScheduleDispatcherImpl.FrameTask>, delta: Duration) {
        if (frameTasks.isEmpty()) return
        println("executePendingTasks ${frameTasks.size}")
        updateDependencyGraph(frameTasks)
        // 初始化任务队列和状态
        prepareExecutionQueue()
        updateTaskDependencyDegrees(frameTasks)
        coordinateTaskExecution(frameTasks, delta)
    }

    private fun updateDependencyGraph(frameTasks: List<ScheduleDispatcherImpl.FrameTask>) {
        frameTasks.forEach { dependencyGraph.updateDependencies(it.scheduleDescriptor) }
    }

    /**
     * 任务执行主循环 - 在FrameTaskContext中运行（主线程）
     * 负责协调任务执行：归档已完成任务、分配新任务、处理循环依赖
     */
    private fun coordinateTaskExecution(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>, delta: Duration) {
        while (hasRemainingTasks(frameTasks)) {
            // ✅ 每轮只检查已经完成状态的任务
            processFinishedTasks(frameTasks)
            val readyTaskCount = executableRangeEnd.value - allocationCursor.value
            val runningTaskCount = allocationCursor.value - completedRangeEnd.value
            if (readyTaskCount == 1) {
                runTaskImmediately(frameTasks[allocationCursor.getAndIncrement()], delta)
                continue
            }
            // 分配批量任务给工作线程
            dispatchTaskBatch(frameTasks, min(16 - runningTaskCount, readyTaskCount), delta)
            if (readyTaskCount == 0 && runningTaskCount == 0) {
                resolveDependencyCycles(frameTasks)
            }
            if (finishedTaskCount.value == 0 && executableRangeEnd.value - allocationCursor.value > 0) {
                runTaskImmediately(frameTasks[allocationCursor.getAndIncrement()], delta)
            }
            if (finishedTaskCount.value == 0) {
                threadYield() // 让出线程，给其他任务执行机会
            }
        }
    }

    /**
     * 执行批量任务 - 在FrameTaskContext中调用（主线程）
     * 设计：在主线程分配任务，启动的协程可能在工作线程或主线程执行
     */
    private fun dispatchTaskBatch(frameTasks: List<ScheduleDispatcherImpl.FrameTask>, size: Int, delta: Duration) {
        val batchSize = min(size, executableRangeEnd.value - allocationCursor.value)
        if (batchSize <= 0) return
        val startIndex = allocationCursor.getAndAdd(batchSize)
        for (index in startIndex until batchSize + startIndex) {
            val frameTask = frameTasks[index]
            workCoroutineScope.launch { runTaskImmediately(frameTask, delta) }
        }
    }

    /**
     * 处理已完成的任务 - 在主线程执行
     * ✅ 设计：只检查completedRangeEnd到allocationCursor之间的任务
     * ✅ 设计：把已完成任务交换到队列最前方（归档）
     */
    private fun processFinishedTasks(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>) {
        if (finishedTaskCount.value == 0) return
        // 正确：只检查已分配但尚未完成的任务区域
        for (index in completedRangeEnd.value until allocationCursor.value) {
            // 安全检查：确保索引有效
            if (index >= frameTasks.size) break

            val frameTask = frameTasks[index]
            val scheduleId = frameTask.scheduleDescriptor.schedule.id
            val scheduleNode = dependencyGraph.getScheduleNode(scheduleId)

            // 正确：只处理状态已经完成的任务
            // 状态正在修改的任务会在下一轮检查中处理
            if (scheduleNode == null || scheduleNode.executionState.value == DependencyGraph.ScheduleNode.STATE_FINISHED) {
                // 正确：把已完成任务交换到队列最前方归档
                frameTasks.swap(index, completedRangeEnd.getAndIncrement())
                unfinishedTaskMask.clear(scheduleId)
                finishedTaskCount.decrementAndGet()
            }
        }
        for (index in executableRangeEnd.value until frameTasks.size) {
            val frameTask = frameTasks[index]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            if (scheduleNode == null || !scheduleNode.dependencies.intersects(unfinishedTaskMask)) {
                frameTasks.swap(index, executableRangeEnd.getAndIncrement())
                scheduleNode?.updateState(DependencyGraph.ScheduleNode.STATE_READY)
            }
        }
    }

    // 队列索引说明：
    // [0, completedRangeEnd): 已完成并归档的任务
    // [completedRangeEnd, allocationCursor): 已分配但尚未完成的任务（正在执行中）
    // [allocationCursor, executableRangeEnd): 可执行但尚未分配的任务（入度为0）
    // [executableRangeEnd, size): 因依赖关系不可执行的任务
    private fun hasRemainingTasks(frameTasks: List<ScheduleDispatcherImpl.FrameTask>): Boolean {
        return finishedTaskCount.value + completedRangeEnd.value < frameTasks.size
    }

    /**
     * 处理循环依赖 - 在主线程执行
     * ✅ 必须同步执行来打破依赖环
     */
    private fun resolveDependencyCycles(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>) {
        // 查找没有强依赖循环的任务
        for (index in executableRangeEnd.value until frameTasks.size) {
            val frameTask = frameTasks[index]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            if (scheduleNode != null && !hasBlockingDependencyCycle(scheduleNode)) {
                frameTasks.swap(index, executableRangeEnd.getAndIncrement())
                scheduleNode.updateState(DependencyGraph.ScheduleNode.STATE_READY)
                return
            }
        }

        // 后备方案：强制执行一个任务来打破死锁
        if (executableRangeEnd.value < frameTasks.size) {
            val frameTask = frameTasks[executableRangeEnd.getAndIncrement()]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            scheduleNode?.updateState(DependencyGraph.ScheduleNode.STATE_READY)
        }
    }

    private fun hasBlockingDependencyCycle(scheduleNode: DependencyGraph.ScheduleNode): Boolean {
        return scheduleNode.hardDependencies.intersects(unfinishedTaskMask)
    }

    /**
     * 执行单个任务 - 可能在主线程或工作线程执行
     * ✅ 状态修改需要保证原子性
     */
    private fun runTaskImmediately(frameTask: ScheduleDispatcherImpl.FrameTask, delta: Duration) {
        val scheduleDescriptor = frameTask.scheduleDescriptor
        val scheduleId = scheduleDescriptor.schedule.id
        val scheduleNode = dependencyGraph.getScheduleNode(scheduleId) ?: return
        val currentState = scheduleNode.executionState
        // 确保节点状态为READY才执行任务
        if (currentState.compareAndSet(DependencyGraph.ScheduleNode.STATE_READY, DependencyGraph.ScheduleNode.STATE_RUNNING)) {
            runCatching {
                frameTask.task(delta)
            }.onFailure { error ->
                println("Task execution failed for schedule $scheduleId: ${error.message}")
                // 打印完整的异常堆栈信息，便于调试
                error.printStackTrace()
            }
            // 使用原子操作确保状态一致性
            currentState.value = DependencyGraph.ScheduleNode.STATE_FINISHED
            finishedTaskCount.incrementAndGet()
        }
    }

    /**
     * 准备任务队列
     */
    private fun prepareExecutionQueue() {
        allocationCursor.value = 0
        executableRangeEnd.value = 0
        completedRangeEnd.value = 0
        finishedTaskCount.value = 0
    }

    /**
     * 更新任务节点的入度
     */
    private fun updateTaskDependencyDegrees(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>) {
        unfinishedTaskMask.clear()

        // 第一阶段：重置所有节点的入度并标记剩余任务
        for (frameTask in frameTasks) {
            val scheduleId = frameTask.scheduleDescriptor.schedule.id
            val scheduleNode = dependencyGraph.getScheduleNode(scheduleId) ?: continue
            unfinishedTaskMask.set(scheduleId)
            scheduleNode.executionState.value = DependencyGraph.ScheduleNode.STATE_PENDING
        }

        for (index in 0 until frameTasks.size) {
            val frameTask = frameTasks[index]
            val scheduleId = frameTask.scheduleDescriptor.schedule.id
            val scheduleNode = dependencyGraph.getScheduleNode(scheduleId)
            if (scheduleNode == null || !scheduleNode.dependencies.intersects(unfinishedTaskMask)) {
                scheduleNode?.updateState(DependencyGraph.ScheduleNode.STATE_READY)
                frameTasks.swap(index, executableRangeEnd.getAndIncrement())
            }
        }
    }
}

class DependencyGraph {

    private val nodeRegistry = ObjectFastList<ScheduleNode?>(256)
    private val structureLock = ReentrantLock()

    private fun getOrCreateNode(scheduleDescriptor: ScheduleDescriptor): ScheduleNode {
        val schedule = scheduleDescriptor.schedule
        val scheduleId = schedule.id

        // 快速路径：直接获取已存在的节点
        val existingNode = nodeRegistry.getOrNull(scheduleId)
        if (existingNode != null) return existingNode

        // 慢速路径：创建新节点
        return structureLock.withLock {
            nodeRegistry.getOrNull(scheduleId) ?: run {
                nodeRegistry.ensureCapacity(scheduleId + 1, null)
                val node = ScheduleNode(schedule, scheduleDescriptor)
                nodeRegistry[scheduleId] = node
                node
            }
        }
    }

    fun updateDependencies(scheduleDescriptor: ScheduleDescriptor) {
        val scheduleNode = getOrCreateNode(scheduleDescriptor)
        if (scheduleNode.requiresUpdate(scheduleDescriptor)) {
            structureLock.withLock {
                resetNodeDependencies(scheduleNode, scheduleDescriptor)
                updateNodeDependencies(scheduleNode)
            }
        }
    }

    fun getScheduleNode(scheduleId: Int): ScheduleNode? = nodeRegistry.getOrNull(scheduleId)

    private fun updateNodeDependencies(scheduleNode: ScheduleNode) {
        val scheduleDescriptor = scheduleNode.scheduleDescriptor
        for (i in 0 until nodeRegistry.size) {
            val otherNode = nodeRegistry.getOrNull(i) ?: continue
            if (otherNode === scheduleNode) continue

            val otherScheduleDescriptor = otherNode.scheduleDescriptor

            // 检查双向依赖关系
            if (scheduleDescriptor.isDependency(otherScheduleDescriptor)) {
                scheduleNode.addDependency(otherNode)
            }

            if (otherScheduleDescriptor.isDependency(scheduleDescriptor)) {
                otherNode.addDependency(scheduleNode)
            }
        }
    }

    private fun resetNodeDependencies(scheduleNode: ScheduleNode, scheduleDescriptor: ScheduleDescriptor) {
        // 清除其他节点对此节点的依赖
        scheduleNode.dependentNodes.forEach { nodeId ->
            val node = nodeRegistry.getOrNull(nodeId) ?: return@forEach
            node.dependencies.clear(scheduleNode.schedule.id)
            node.hardDependencies.clear(scheduleNode.schedule.id)
        }

        // 重置此节点的依赖关系
        scheduleNode.resetDependencies(scheduleDescriptor)
    }

    data class ScheduleNode(
        var schedule: Schedule,
        var scheduleDescriptor: ScheduleDescriptor,
        val hardDependencies: BitSet = BitSet(256),
        val dependencies: BitSet = BitSet(256),
        val dependentNodes: BitSet = BitSet(256),
        var descriptorVersion: Int = -1
    ) {
        val executionState = atomic(STATE_PENDING)

        fun updateState(state: Int) {
            executionState.value = state
        }

        fun requiresUpdate(newScheduleDescriptor: ScheduleDescriptor): Boolean {
            return schedule != newScheduleDescriptor.schedule || descriptorVersion != newScheduleDescriptor.version
        }

        fun addDependency(otherNode: ScheduleNode) {
            val otherScheduleId = otherNode.schedule.id

            dependencies.set(otherScheduleId)
            if (scheduleDescriptor.isStrongDependency(otherNode.scheduleDescriptor)) {
                hardDependencies.set(otherScheduleId)
            }

            otherNode.dependentNodes.set(schedule.id)
        }

        fun resetDependencies(newScheduleDescriptor: ScheduleDescriptor) {
            this.schedule = newScheduleDescriptor.schedule
            this.scheduleDescriptor = newScheduleDescriptor
            hardDependencies.clear()
            dependencies.clear()
            dependentNodes.clear()
            descriptorVersion = newScheduleDescriptor.version
        }

        companion object {
            const val STATE_PENDING = 0
            const val STATE_READY = 1
            const val STATE_RUNNING = 2
            const val STATE_FINISHED = 3
        }
    }
}