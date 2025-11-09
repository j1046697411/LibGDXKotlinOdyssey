package cn.jzl.ecs.v2

import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

/**
 * 帧协同任务调度器
 *
 * 设计特点：
 * - 任务分类：区分帧内关键任务和后台任务，确保游戏渲染流畅
 * - 优先级机制：帧关键任务优先执行，保证游戏主循环的响应性
 * - 工作线程池：使用有限并行度的工作线程处理后台任务
 * - 负载均衡：根据活跃工作线程数量动态调整任务执行策略
 * - 异常安全：完善的错误处理确保线程计数的准确性
 * - 资源优化：使用高效的集合类型减少内存分配和GC压力
 *
 * 核心组件：
 * - 帧关键任务队列：用于存储必须在当前帧内完成的任务
 * - 后台任务队列：用于存储可以异步执行的任务
 * - 工作线程池：管理并发任务的执行
 * - 原子计数器：安全跟踪活跃工作线程数量
 *
 * 此实现是ECS框架中异步任务处理的核心，通过协程机制实现高效调度
 */
internal class FrameCoroutineScheduler : CoroutineDispatcher() {
    // 常量定义
    private companion object {
        const val MAX_WORKER_COUNT = 12
        const val DEFAULT_TASK_CAPACITY = 128
        const val DEFAULT_FRAME_TASK_CAPACITY = 64
    }

    // 任务队列
    private val backgroundTasks = ObjectFastList<Pair<CoroutineContext, Runnable>>(DEFAULT_TASK_CAPACITY)
    private val frameCriticalTasks = ObjectFastList<Runnable>(DEFAULT_FRAME_TASK_CAPACITY)

    // 工作线程池和计数器
    private val workerDispatcher = Dispatchers.Default.limitedParallelism(MAX_WORKER_COUNT)
    private val activeWorkerCount = atomic(0)

    /**
 * 分发任务到适当的执行队列
 *
 * 实现步骤：
 * 1. 检查协程上下文是否包含FrameTaskContext标记
 * 2. 如果是帧关键任务，直接添加到帧关键任务队列
 * 3. 如果是后台任务，检查当前活跃工作线程数量
 * 4. 如果工作线程未满，立即提交给工作线程执行
 * 5. 如果工作线程饱和，将任务加入后台任务队列等待执行
 *
 * 设计理念：
 * - 任务分类：基于上下文自动识别任务类型并分配到不同队列
 * - 动态调度：根据系统负载情况自适应调整任务处理策略
 * - 优先级保障：确保帧关键任务优先被处理，不依赖线程池
 *
 * @param context 协程上下文，用于识别任务类型和获取调度信息
 * @param block 待执行的任务块，包含实际的执行逻辑
 */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        // 帧内关键任务优先处理
        if (context[FrameTaskContext] != null) {
            frameCriticalTasks.insertLast(block)
            return
        }

        // 后台任务根据工作线程数量决定执行方式
        if (activeWorkerCount.value < MAX_WORKER_COUNT) {
            submitToWorker(context, block)
        } else {
            // 工作线程饱和时，将任务加入队列等待
            backgroundTasks.insertLast(context to block)
        }
    }

    /**
     * 将任务提交给工作线程执行
     *
     * 实现步骤：
     * 1. 原子递增活跃工作线程计数器，确保线程安全
     * 2. 使用try-catch包装整个调度过程，确保异常不会影响计数
     * 3. 调用workerDispatcher的dispatchYield方法提交任务
     * 4. 在任务执行完成后（finally块中）原子递减计数器
     * 5. 如果调度失败，在onFailure回调中确保计数器正确递减
     *
     * 设计理念：
     * - 原子操作：使用原子计数器确保线程安全
     * - 资源释放保证：无论成功或失败都确保计数器正确更新
     * - 异常隔离：任务执行异常不会影响调度器的稳定性
     * - 公平调度：使用dispatchYield确保任务公平分配给工作线程
     *
     * @param context 协程上下文，传递给工作线程的上下文信息
     * @param task 待执行的任务，包含实际的工作逻辑
     */
    @OptIn(InternalCoroutinesApi::class)
    private fun submitToWorker(context: CoroutineContext, task: Runnable) {
        activeWorkerCount.incrementAndGet()

        // 确保计数正确性的异常处理
        runCatching {
            workerDispatcher.dispatchYield(context + Dispatchers.Default) {
                try {
                    task.run()
                } finally {
                    activeWorkerCount.decrementAndGet()
                }
            }
        }.onFailure {
            // 确保在调度失败时也减少计数器
            activeWorkerCount.decrementAndGet()
        }
    }

    /**
     * 执行所有待处理的任务
     *
     * 实现步骤：
     * 1. 当有待处理任务或条件函数返回true时继续循环
     * 2. 优先处理帧关键任务（当满足优先条件且有帧任务时）
     * 3. 处理后台任务（如果存在）
     * 4. 处理剩余的帧关键任务（如果前面两个队列都为空）
     * 5. 如果所有队列都为空且条件仍为true，让出CPU时间片
     *
     * 设计理念：
     * - 优先级调度：优先处理帧关键任务，保证游戏主循环流畅
     * - 资源利用最大化：在满足优先条件的情况下充分利用CPU
     * - 自适应处理：根据队列状态动态调整任务处理顺序
     * - 防阻塞设计：当无任务可执行时让出CPU，避免空循环消耗资源
     *
     * @param condition 控制循环继续执行的条件函数，通常用于限制执行时间或帧数
     */
    fun processAllTasks(condition: () -> Boolean) {
        while (hasPendingTasks() || condition()) {
            // 优先处理帧关键任务
            if (shouldPrioritizeFrameTasks() && frameCriticalTasks.isNotEmpty()) {
                frameCriticalTasks.removeFirst().run()
                continue
            }

            // 处理后台任务
            if (backgroundTasks.isNotEmpty()) {
                processBackgroundTask()
                continue
            }

            // 处理剩余的帧关键任务
            if (frameCriticalTasks.isNotEmpty()) {
                frameCriticalTasks.removeLast().run()
                continue
            }

            // 让出CPU时间片
            threadYield()
        }
    }

    /**
     * 检查是否有待处理的任务
     *
     * 实现逻辑：
     * - 检查后台任务队列是否非空
     * - 检查帧关键任务队列是否非空
     * - 检查是否有正在执行的工作线程
     * - 只要满足任一条件，即认为存在待处理任务
     *
     * 设计理念：
     * - 全面检查：不仅考虑等待队列，还考虑正在执行的任务
     * - 快速判断：使用简单的逻辑运算提高判断效率
     *
     * @return 如果存在待处理任务（包括队列中的任务和执行中的任务）返回true，否则返回false
     */
    private fun hasPendingTasks(): Boolean {
        return backgroundTasks.isNotEmpty() || frameCriticalTasks.isNotEmpty() || activeWorkerCount.value > 0
    }

    /**
     * 判断是否应该优先处理帧关键任务
     *
     * 实现逻辑：
     * - 比较后台任务队列大小与活跃工作线程数量
     * - 当后台任务数量小于等于活跃工作线程数量时，优先处理帧关键任务
     * - 这表示后台任务负载较轻，有足够的工作线程处理
     *
     * 设计理念：
     * - 负载感知：基于当前系统负载情况动态调整优先级策略
     * - 资源平衡：确保帧关键任务和后台任务都能得到合理的处理资源
     * - 避免饥饿：防止后台任务过多导致帧关键任务长时间等待
     *
     * @return 如果应该优先处理帧关键任务返回true，否则返回false
     */
    private fun shouldPrioritizeFrameTasks(): Boolean {
        return backgroundTasks.size <= activeWorkerCount.value
    }

    /**
     * 处理单个后台任务
     *
     * 实现步骤：
     * 1. 从后台任务队列中移除并获取第一个任务
     * 2. 检查当前活跃工作线程数量和剩余后台任务数量
     * 3. 如果工作线程已饱和或没有更多后台任务，则直接在当前线程执行
     * 4. 否则，将任务提交给工作线程异步执行
     *
     * 设计理念：
     * - 自适应执行：根据系统状态动态调整执行策略
     * - 资源优化：避免创建过多工作线程导致的资源浪费
     * - 负载均衡：合理分配任务以充分利用系统资源
     * - 避免过度调度：当只有少量任务时直接执行，减少调度开销
     *
     * 优化考量：
     * - 当后台任务队列即将为空时，直接在当前线程执行可以减少线程切换开销
     * - 当工作线程已满时，直接执行避免任务继续积压
     */
    private fun processBackgroundTask() {
        val taskPair = backgroundTasks.removeFirst()

        // 根据工作线程状态决定执行方式
        if (activeWorkerCount.value >= MAX_WORKER_COUNT || backgroundTasks.isEmpty()) {
            taskPair.second.run() // 直接执行
        } else {
            submitToWorker(taskPair.first, taskPair.second) // 提交给工作线程
        }
    }

    /**
     * 帧任务上下文标记
     *
     * 实现特点：
     * - 单例模式：使用object关键字确保全局唯一实例
     * - 协程上下文元素：实现CoroutineContext.Element接口
     * - 自引用键：同时实现Key接口并返回自身作为key
     *
     * 设计理念：
     * - 轻量级标记：作为协程上下文的标记，不包含额外数据
     * - 类型安全：通过类型系统确保上下文标记的正确使用
     * - 零开销：使用object单例减少内存分配
     *
     * 使用场景：
     * - 当任务需要在帧内优先执行时，将此上下文添加到协程上下文
     * - 调度器通过检查此上下文决定任务的处理优先级
     *
     * 此实现遵循Kotlin协程上下文设计模式，提供高效的任务分类机制
     */
    data object FrameTaskContext : CoroutineContext.Element, CoroutineContext.Key<FrameTaskContext> {
        override val key: CoroutineContext.Key<*> get() = this
    }
}