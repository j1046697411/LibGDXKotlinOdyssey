package cn.jzl.ecs.v2

import cn.jzl.di.instance
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration

/**
 * 调度任务评分接口的内部实现类
 *
 * @property priority 任务优先级
 */
@PublishedApi
internal class ScheduleTaskScopeImpl(
    override val world: World,
    private val scheduleDescriptor: ScheduleDescriptor,
    private val priority: SchedulePriority
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