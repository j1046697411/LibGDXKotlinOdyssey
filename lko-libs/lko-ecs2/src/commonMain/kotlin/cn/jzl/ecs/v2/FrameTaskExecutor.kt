/**
 * FrameTaskExecutor.kt 实现了高效的帧任务执行协调器
 * 
 * 帧任务执行器是ECS框架中负责任务调度和协调的核心组件，提供：
 * 1. 高性能的任务队列管理与批处理
 * 2. 复杂依赖关系的自动解析与处理
 * 3. 并发安全的任务分配与执行
 * 4. 循环依赖的自动检测与解决
 * 5. 协程集成的异步任务执行
 * 
 * 核心设计特点：
 * - 使用原子变量管理任务状态，支持高效并发
 * - 实现依赖图处理循环依赖问题
 * - 采用锁保护依赖图结构的线程安全
 * - 任务批处理以提高性能
 * - 支持多线程执行任务
 */
package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.time.Duration

/**
 * 帧任务执行器类
 * 
 * 负责管理、分配和执行一帧内的所有任务，处理任务间的复杂依赖关系
 * 
 * @param initialCapacity 初始容量，用于预分配数据结构
 */
class FrameTaskExecutor(initialCapacity: Int = 256) {
    /**
     * 依赖图，用于管理和解析任务间的依赖关系
     */
    private val dependencyGraph = DependencyGraph()
    
    /**
     * 已完成但尚未归档的任务数量
     * 
     * 原子变量，用于线程安全地跟踪任务完成状态
     */
    private val finishedTaskCount = atomic(0) // 已完成但尚未归档的任务数量

    /**
     * 未完成任务的位掩码
     * 
     * 使用BitSet高效标记哪些任务尚未完成
     */
    private val unfinishedTaskMask = BitSet(initialCapacity)  // 标记哪些任务尚未完成
    
    /**
     * 工作协程作用域
     * 
     * 使用默认调度器和SupervisorJob管理工作线程任务
     */
    private val workCoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * 队列索引说明：
     * [0, completedRangeEnd): 已完成并归档的任务
     * [completedRangeEnd, allocationCursor): 已分配但尚未完成的任务（正在执行中）
     * [allocationCursor, executableRangeEnd): 可执行但尚未分配的任务（入度为0）
     * [executableRangeEnd, size): 因依赖关系不可执行的任务
     */
    
    /**
     * 已完成任务的归档索引
     * 
     * 原子变量，标记已归档任务的边界
     */
    private val completedRangeEnd = atomic(0) // 已完成任务的归档索引
    
    /**
     * 当前批次的起始索引
     * 
     * 原子变量，标记已分配任务的边界
     */
    private val allocationCursor = atomic(0)  // 当前批次的起始索引
    
    /**
     * 可执行任务的结束索引
     * 
     * 原子变量，标记可执行任务的边界
     */
    private val executableRangeEnd = atomic(0)    // 可执行任务的结束索引

    /**
     * 扩展函数：安全地交换列表中的两个元素
     * 
     * 添加了全面的边界检查，防止索引越界错误
     * 
     * @param to 目标索引
     * @param from 源索引
     */
    private fun MutableList<ScheduleDispatcherImpl.FrameTask>.swap(to: Int, from: Int) {
        // 增加边界检查，防止索引越界
        if (to == from || to < 0 || from < 0 || to >= size || from >= size) return
        val temp = this[to]
        this[to] = this[from]
        this[from] = temp
    }

    /**
     * 执行待处理任务 - 在主线程调用
     * 
     * 设计原理：任务分配和依赖检查都在主线程完成，确保依赖关系的一致性
     * 
     * @param frameTasks 待执行的帧任务列表
     * @param delta 帧时间间隔，传递给任务执行
     */
    fun executePendingTasks(frameTasks: ObjectFastList<ScheduleDispatcherImpl.FrameTask>, delta: Duration) {
        // 空检查，避免不必要的执行
        if (frameTasks.isEmpty()) return

        // 更新依赖图，处理任务间的依赖关系
        updateDependencyGraph(frameTasks)
        
        // 初始化任务队列和状态
        prepareExecutionQueue()
        
        // 更新任务依赖度并确定初始可执行任务
        updateTaskDependencyDegrees(frameTasks)
        
        // 协调任务执行的主循环
        coordinateTaskExecution(frameTasks, delta)
    }

    /**
     * 更新依赖图
     * 
     * 为每个帧任务更新其在依赖图中的依赖关系
     * 
     * @param frameTasks 帧任务列表
     */
    private fun updateDependencyGraph(frameTasks: List<ScheduleDispatcherImpl.FrameTask>) {
        frameTasks.forEach { dependencyGraph.updateDependencies(it.scheduleDescriptor) }
    }

    /**
     * 任务执行主循环 - 在FrameTaskContext中运行（主线程）
     * 
     * 负责协调任务执行：归档已完成任务、分配新任务、处理循环依赖
     * 这是整个执行器的核心控制逻辑
     * 
     * @param frameTasks 帧任务列表
     * @param delta 帧时间间隔
     */
    private fun coordinateTaskExecution(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>, delta: Duration) {
        // 只要还有剩余任务就继续执行
        while (hasRemainingTasks(frameTasks)) {
            // ✅ 每轮只检查已经完成状态的任务
            processFinishedTasks(frameTasks)
            
            // 计算可执行任务数和正在运行的任务数
            val readyTaskCount = executableRangeEnd.value - allocationCursor.value
            val runningTaskCount = allocationCursor.value - completedRangeEnd.value
            
            // 单个任务的特殊优化：直接在主线程执行，减少协程开销
            if (readyTaskCount == 1) {
                runTaskImmediately(frameTasks[allocationCursor.getAndIncrement()], delta)
                continue
            }
            
            // 分配批量任务给工作线程，限制并发数量为16
            dispatchTaskBatch(frameTasks, min(16 - runningTaskCount, readyTaskCount), delta)
            
            // 没有可执行任务且没有运行中任务，说明可能存在循环依赖
            if (readyTaskCount == 0 && runningTaskCount == 0) {
                resolveDependencyCycles(frameTasks)
            }
            
            // 没有完成任务但有可执行任务，直接执行一个任务
            if (finishedTaskCount.value == 0 && executableRangeEnd.value - allocationCursor.value > 0) {
                runTaskImmediately(frameTasks[allocationCursor.getAndIncrement()], delta)
            }
            
            // 如果没有任务完成，让出线程，给其他任务执行机会
            if (finishedTaskCount.value == 0) {
                threadYield() // 让出线程，给其他任务执行机会
            }
        }
    }

    /**
     * 执行批量任务 - 在FrameTaskContext中调用（主线程）
     * 
     * 设计：在主线程分配任务，启动的协程可能在工作线程或主线程执行
     * 批处理可以减少任务分配的开销，提高执行效率
     * 
     * @param frameTasks 帧任务列表
     * @param size 要分配的批处理大小
     * @param delta 帧时间间隔
     */
    private fun dispatchTaskBatch(frameTasks: List<ScheduleDispatcherImpl.FrameTask>, size: Int, delta: Duration) {
        // 计算实际的批处理大小，确保不超过可执行任务数量
        val batchSize = min(size, executableRangeEnd.value - allocationCursor.value)
        if (batchSize <= 0) return
        
        // 原子地获取并更新分配游标
        val startIndex = allocationCursor.getAndAdd(batchSize)
        
        // 为批处理中的每个任务启动一个协程
        for (index in startIndex until batchSize + startIndex) {
            val frameTask = frameTasks[index]
            workCoroutineScope.launch { runTaskImmediately(frameTask, delta) }
        }
    }

    /**
     * 处理已完成的任务 - 在主线程执行
     * 
     * ✅ 设计：只检查completedRangeEnd到allocationCursor之间的任务
     * ✅ 设计：把已完成任务交换到队列最前方（归档）
     * 这种设计可以保持任务队列的组织结构，同时高效地归档完成的任务
     * 
     * @param frameTasks 帧任务列表
     */
    private fun processFinishedTasks(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>) {
        // 快速路径：没有完成任务，直接返回
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
                // 从未完成任务掩码中清除
                unfinishedTaskMask.clear(scheduleId)
                // 减少已完成任务计数
                finishedTaskCount.decrementAndGet()
            }
        }
        // 更新可执行任务范围：检查之前不可执行的任务是否现在可以执行
        for (index in executableRangeEnd.value until frameTasks.size) {
            val frameTask = frameTasks[index]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            // 如果任务没有依赖或者所有依赖都已完成
            if (scheduleNode == null || !scheduleNode.dependencies.intersects(unfinishedTaskMask)) {
                // 将任务移到可执行区域
                frameTasks.swap(index, executableRangeEnd.getAndIncrement())
                // 更新任务状态为就绪
                scheduleNode?.updateState(DependencyGraph.ScheduleNode.STATE_READY)
            }
        }
    }

    /**
     * 检查是否还有剩余任务需要执行
     * 
     * 队列索引说明：
     * [0, completedRangeEnd): 已完成并归档的任务
     * [completedRangeEnd, allocationCursor): 已分配但尚未完成的任务（正在执行中）
     * [allocationCursor, executableRangeEnd): 可执行但尚未分配的任务（入度为0）
     * [executableRangeEnd, size): 因依赖关系不可执行的任务
     * 
     * @param frameTasks 帧任务列表
     * @return 如果还有未完成任务返回true
     */
    private fun hasRemainingTasks(frameTasks: List<ScheduleDispatcherImpl.FrameTask>): Boolean {
        // 已完成任务数 + 已归档任务数 < 总任务数，表示还有任务未完成
        return finishedTaskCount.value + completedRangeEnd.value < frameTasks.size
    }

    /**
     * 处理循环依赖 - 在主线程执行
     * 
     * ✅ 必须同步执行来打破依赖环
     * 当存在循环依赖时，系统需要主动打破死锁，确保任务能够继续执行
     * 
     * @param frameTasks 帧任务列表
     */
    private fun resolveDependencyCycles(frameTasks: MutableList<ScheduleDispatcherImpl.FrameTask>) {
        // 第一阶段：查找没有强依赖循环的任务
        for (index in executableRangeEnd.value until frameTasks.size) {
            val frameTask = frameTasks[index]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            // 找到可以安全执行的任务（没有阻塞性强依赖）
            if (scheduleNode != null && !hasBlockingDependencyCycle(scheduleNode)) {
                // 移动到可执行区域
                frameTasks.swap(index, executableRangeEnd.getAndIncrement())
                // 更新状态为就绪
                scheduleNode.updateState(DependencyGraph.ScheduleNode.STATE_READY)
                return
            }
        }

        // 后备方案：如果无法找到安全的任务，强制执行第一个任务来打破死锁
        if (executableRangeEnd.value < frameTasks.size) {
            val frameTask = frameTasks[executableRangeEnd.getAndIncrement()]
            val scheduleNode = dependencyGraph.getScheduleNode(frameTask.scheduleDescriptor.schedule.id)
            // 标记为就绪，强制执行以打破循环依赖
            scheduleNode?.updateState(DependencyGraph.ScheduleNode.STATE_READY)
        }
    }

    /**
     * 检查是否存在阻塞性依赖循环
     * 
     * 如果任务的强依赖与未完成任务集合有交集，则存在阻塞性循环依赖
     * 
     * @param scheduleNode 调度节点
     * @return 如果存在阻塞性依赖循环返回true
     */
    private fun hasBlockingDependencyCycle(scheduleNode: DependencyGraph.ScheduleNode): Boolean {
        // 检查强依赖是否与未完成任务有交集
        return scheduleNode.hardDependencies.intersects(unfinishedTaskMask)
    }

    /**
     * 执行单个任务 - 可能在主线程或工作线程执行
     * 
     * ✅ 状态修改需要保证原子性
     * 使用CAS操作确保任务状态的原子转换，防止并发问题
     * 
     * @param frameTask 要执行的帧任务
     * @param delta 帧时间间隔
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

