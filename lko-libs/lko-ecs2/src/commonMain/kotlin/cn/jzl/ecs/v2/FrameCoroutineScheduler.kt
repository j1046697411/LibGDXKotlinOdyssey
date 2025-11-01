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
 * 优化的协程任务调度器，用于在游戏循环帧内高效执行任务。
 * 通过区分帧内关键任务和后台任务，结合并发控制和任务队列管理，
 * 确保游戏帧的流畅性同时最大化系统资源利用。
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
     * @param context 协程上下文，用于识别任务类型
     * @param block 待执行的任务块
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
     * @param condition 控制循环继续执行的条件函数
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
     */
    private fun hasPendingTasks(): Boolean {
        return backgroundTasks.isNotEmpty() || frameCriticalTasks.isNotEmpty() || activeWorkerCount.value > 0
    }

    /**
     * 判断是否应该优先处理帧关键任务
     */
    private fun shouldPrioritizeFrameTasks(): Boolean {
        return backgroundTasks.size <= activeWorkerCount.value
    }

    /**
     * 处理单个后台任务
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
     * 用于标识需要在帧内优先执行的关键任务
     */
    data object FrameTaskContext : CoroutineContext.Element, CoroutineContext.Key<FrameTaskContext> {
        override val key: CoroutineContext.Key<*> get() = this
    }
}