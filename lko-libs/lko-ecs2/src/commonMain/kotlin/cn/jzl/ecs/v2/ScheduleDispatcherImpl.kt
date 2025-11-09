package cn.jzl.ecs.v2

import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.time.Duration

/**
 * 调度器分发器实现类，负责管理初始化任务、下一帧任务和延迟任务的执行
 * 
 * 核心职责：
 * - 管理和执行不同类型的任务（初始化任务、下一帧任务、延迟任务）
 * - 构建任务依赖图，确保任务按正确顺序执行
 * - 协调多线程任务执行，优化性能
 * - 实现任务优先级调度和延迟执行机制
 * 
 * 设计特点：
 * - 优先级队列机制：支持不同优先级的任务调度，确保高优先级任务优先执行
 * - 并发安全：使用可重入锁保护任务列表的并发访问
 * - 任务状态管理：通过专用数据结构跟踪不同类型任务的状态
 * - 延迟执行：支持基于时间的延迟任务调度，实现精确计时
 * 
 * 帧任务执行流程：
 * 1. 执行所有初始化任务（mainTasks）
 * 2. 更新延迟任务状态，将到期任务转为工作任务
 * 3. 执行所有下一帧任务（workTasks），包括依赖关系处理和优先级排序
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
 * 实现步骤：
 * 1. 获取锁，确保线程安全
 * 2. 创建新的FrameTask对象，封装调度器描述符、优先级和任务函数
 * 3. 将任务添加到mainTasks列表末尾
 *
 * 设计理念：
 * - 线程安全：使用可重入锁保护任务列表的并发访问
 * - 延迟执行：初始化任务不会立即执行，而是安排在下一帧开始时执行
 * - 优先级支持：虽然主要用于初始化，但仍保留优先级机制以支持复杂场景
 *
 * @param scheduleDescriptor 调度器描述符，用于标识任务来源和依赖信息
 * @param priority 任务优先级，决定任务的执行顺序
 * @param task 要执行的任务函数，接收持续时间参数
 */
    override fun addMainTask(scheduleDescriptor: ScheduleDescriptor, priority: ScheduleTaskPriority, task: (Duration) -> Unit) = lock.withLock {
        mainTasks.insertLast(FrameTask(scheduleDescriptor, priority, task))
    }

    /**
 * 添加下一帧执行的任务
 *
 * 实现步骤：
 * 1. 获取锁，确保线程安全
 * 2. 创建新的FrameTask对象，封装调度器描述符、优先级和任务函数
 * 3. 调用内部方法将任务添加到工作任务列表
 *
 * 设计理念：
 * - 线程安全：使用可重入锁保护任务列表的并发访问
 * - 优先级管理：支持不同优先级的任务调度
 * - 延迟执行：任务会被安排到下一帧执行，避免帧内阻塞
 * - 职责分离：将任务创建和添加逻辑分离，便于扩展
 *
 * @param scheduleDescriptor 调度器描述符，用于标识任务来源和依赖信息
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
 * 
 * 实现步骤：
 * 1. 将FrameTask对象直接添加到workTasks列表末尾
 * 2. 依赖关系的处理会在任务执行阶段进行
 *
 * 设计理念：
 * - 简化实现：将任务添加和依赖处理分离，使代码更清晰
 * - 延迟处理：依赖关系在实际执行阶段才会处理，减少添加阶段的开销
 * - 内部使用：此方法仅在锁保护的上下文中被调用，确保线程安全
 *
 * @param frameTask 要添加的帧任务对象，包含调度器描述符、优先级和任务函数
 */
    private fun addWorkTask(frameTask: FrameTask) {
        workTasks.insertLast(frameTask)
    }
    /**
 * 添加延迟任务
 *
 * 实现步骤：
 * 1. 获取锁，确保线程安全
 * 2. 创建FrameTask对象，封装调度器描述符、优先级和任务函数
 * 3. 创建DelayFrameTask对象，设置初始剩余延迟时间和关联的帧任务
 * 4. 将延迟任务添加到delayTasks列表
 *
 * 设计理念：
 * - 分离关注点：将延迟调度和任务执行逻辑分离，使用组合模式
 * - 精确计时：通过剩余延迟时间计数器实现精确的延迟控制
 * - 自动转换：到期后自动转换为工作任务，简化用户使用
 * - 优先级保持：即使延迟执行，任务优先级也会被保留
 *
 * @param scheduleDescriptor 调度器描述符，用于标识任务来源和依赖信息
 * @param priority 任务优先级，决定任务的执行顺序
 * @param delay 延迟时间，表示任务需要等待多长时间才能执行
 * @param task 要执行的任务函数，接收持续时间参数
 */
    override fun addDelayFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        delay: Duration,
        task: (Duration) -> Unit
    ): Unit = lock.withLock {
        // 创建延迟任务对象，设置初始剩余延迟时间和要执行的帧任务
        delayTasks.insertLast(DelayFrameTask(delay, FrameTask(scheduleDescriptor, priority, task)))
    }

    /**
 * 更新方法 - 在游戏循环的每一帧调用
 *
 * 实现步骤：
 * 1. 执行所有初始化任务（mainTasks）
 * 2. 更新延迟任务状态，将到期任务转为工作任务
 * 3. 执行所有下一帧任务（workTasks）
 *
 * 设计理念：
 * - 有序执行：按照固定顺序处理不同类型任务，确保系统一致性
 * - 时间驱动：基于时间增量更新任务状态和执行时机
 * - 主循环集成：作为游戏主循环的核心接口，协调所有任务执行
 * - 分层处理：将任务处理分为初始化、延迟处理和工作执行三个阶段
 *
 * 执行顺序的重要性：
 * - 先执行初始化任务，确保系统正确启动
 * - 然后处理延迟任务，将到期任务转为工作任务
 * - 最后执行所有工作任务，包括新添加的和从延迟任务转换的
 *
 * @param delta 时间增量，表示自上次更新以来经过的时间
 *              用于任务执行和更新延迟任务的剩余时间
 */
    override fun update(delta: Duration) {
        // 执行初始化任务 - 只在首次或有新初始化任务时执行
        executeMainTasks(delta)
        // 更新延迟任务状态 - 减少剩余时间，处理到期任务
        updateDelayTasks(delta)
        // 执行下一帧任务 - 包括常规任务和从延迟任务转换而来的任务
        executeWorkTasks(delta)
    }

    /**
 * 执行所有初始化任务
 *
 * 实现步骤：
 * 1. 检查是否有初始化任务，无任务则直接返回
 * 2. 将所有初始化任务复制到临时列表currentFrameTasks
 * 3. 清空原任务列表，确保任务只执行一次
 * 4. 按优先级排序任务列表
 * 5. 遍历并执行每个任务
 * 6. 清空临时列表，准备下一帧使用
 *
 * 设计理念：
 * - 单次执行：确保每个初始化任务只执行一次
 * - 优先级执行：即使是初始化任务也支持优先级排序
 * - 安全执行：使用临时缓冲区避免在执行过程中修改原任务列表
 * - 资源清理：执行完成后清空临时列表，避免内存泄漏
 *
 * @param delta 时间增量，传递给任务函数，表示当前帧的时间间隔
 */
    private fun executeMainTasks(delta: Duration) {
        if (mainTasks.isEmpty()) return
        // 将所有初始化任务收集到临时列表
        currentFrameTasks.insertLastAll(mainTasks)
        // 清空原任务列表，确保任务只执行一次
        mainTasks.clear()
        // 按优先级排序任务，确保高优先级任务先执行
        currentFrameTasks.sortedBy { it.priority.priority }
        // 执行所有任务
        currentFrameTasks.forEach { task -> task.task(delta) }
        // 清空临时列表，准备下一帧使用
        currentFrameTasks.clear()
    }

    /**
 * 更新所有延迟任务的状态
 *
 * 实现步骤：
 * 1. 检查是否有延迟任务，无任务则直接返回
 * 2. 使用迭代器遍历延迟任务列表
 * 3. 对每个任务减少剩余延迟时间
 * 4. 检查任务是否到期（剩余时间≤0）
 * 5. 对到期任务：
 *    a. 将其关联的帧任务添加到工作任务队列
 *    b. 从延迟任务列表中移除
 * 6. 对未到期任务，保留在列表中继续等待
 *
 * 设计理念：
 * - 安全遍历：使用迭代器避免并发修改异常
 * - 精确计时：基于时间增量的倒计时机制
 * - 自动转换：到期任务自动转为工作任务，无需手动干预
 * - 资源管理：及时移除已处理的任务，避免内存泄漏
 *
 * 性能考虑：
 * - 只在有延迟任务时执行，避免不必要的处理
 * - 使用迭代器而不是for循环，支持在遍历过程中安全移除元素
 *
 * @param delta 时间增量，用于减少延迟任务的剩余执行时间
 */
    private fun updateDelayTasks(delta: Duration) {
        // 如果没有延迟任务，直接返回
        if (delayTasks.isEmpty()) return
        
        // 使用迭代器安全遍历和修改延迟任务列表
        val iterator = delayTasks.iterator()
        while (iterator.hasNext()) {
            val delayTask = iterator.next()
            
            // 减少剩余延迟时间
            delayTask.remainingDelay -= delta
            
            // 检查任务是否到期
            if (delayTask.remainingDelay <= Duration.Companion.ZERO) {
                // 将到期任务添加到工作任务列表，准备在下一阶段执行
                // 此时任务已完成等待，按照其定义的优先级进入执行队列
                addWorkTask(delayTask.frameTask)
                
                // 从延迟任务列表中移除，避免重复处理
                iterator.remove()
            }
        }
    }

    /**
 * 执行所有下一帧任务
 * 
 * 实现步骤：
 * 1. 清空当前帧任务列表，准备收集新任务
 * 2. 将所有工作任务复制到当前帧任务列表
 * 3. 清空工作任务列表，为接收下一帧的新任务做准备
 * 4. 调用帧任务执行器处理所有当前帧任务，包括依赖管理和并发执行
 * 5. 最后清空当前帧任务列表，释放资源
 *
 * 设计理念：
 * - 分离执行逻辑：将任务收集和任务执行分离，使用专门的执行器处理
 * - 状态隔离：确保当前帧的任务不会与下一帧的任务混合
 * - 依赖处理：委托给帧任务执行器处理复杂的依赖关系
 * - 资源清理：及时清空临时列表，避免内存泄漏
 *
 * 性能优化：
 * - 使用专用执行器优化任务调度和执行
 * - 避免在执行过程中修改原始任务列表
 * - 支持任务的并行执行和依赖排序
 *
 * @param delta 时间增量，传递给每个任务函数，表示当前帧的时间间隔
 */
    private fun executeWorkTasks(delta: Duration) {
        // 清空当前帧任务列表
        currentFrameTasks.clear()
        // 将所有工作任务转移到当前帧任务列表
        currentFrameTasks.addAll(workTasks)
        // 清空工作任务列表，准备接收新任务
        workTasks.clear()
        // 使用帧任务执行器处理所有待执行任务
        // 通过帧任务执行器处理任务依赖和并发执行
        frameTaskExecutor.executePendingTasks(currentFrameTasks, delta)
        // 执行完成后清空当前帧任务列表
        currentFrameTasks.clear()
    }

    /**
 * 帧任务数据类，封装任务的基本信息
 * 
 * 实现特点：
 * - 数据封装：将任务的所有相关信息封装在一个数据类中
 * - 不可变性：主要属性设计为只读，确保任务信息的一致性
 * - 完整性：包含执行任务所需的所有信息
 *
 * 核心组成：
 * - 调度器描述符：标识任务来源，包含依赖信息
 * - 优先级：控制任务在同类型任务中的执行顺序
 * - 任务函数：实际要执行的代码，接收时间增量参数
 * 
 * @property scheduleDescriptor 调度器描述符，包含任务来源和依赖信息
 * @property priority 任务优先级，决定任务的执行顺序
 * @property task 要执行的任务函数，接收持续时间参数（通常是帧时间增量）
 */
    data class FrameTask(
        val scheduleDescriptor: ScheduleDescriptor,
        val priority: ScheduleTaskPriority,
        val task: (Duration) -> Unit
    )

    /**
 * 延迟任务数据类，封装延迟任务的详细信息
 * 
 * 实现特点：
 * - 组合模式：包装FrameTask，添加延迟执行功能
 * - 状态跟踪：维护剩余延迟时间，用于判断任务是否到期
 * - 自动转换：到期后可以转换为普通工作任务
 *
 * 设计理念：
 * - 关注点分离：将延迟调度和任务执行逻辑分离
 * - 倒计时机制：基于剩余时间的精确计时
 * - 自动升级：到期后自动转为普通任务执行
 *
 * 工作原理：
 * 1. 初始化时设置剩余延迟时间
 * 2. 每一帧更新时减少剩余时间
 * 3. 当剩余时间≤0时，任务到期
 * 4. 到期后将内部的帧任务移至工作任务队列
 * 
 * @property remainingDelay 剩余延迟时间，表示任务还需要等待多长时间才能执行
 *                          随着每一帧的update方法调用而减少
 * @property frameTask 延迟结束后要执行的帧任务对象，包含实际的任务逻辑和优先级
 *                     一旦任务到期，此对象将被移至工作任务队列
 */
    private data class DelayFrameTask(
        var remainingDelay: Duration,
        val frameTask: FrameTask
    )
}