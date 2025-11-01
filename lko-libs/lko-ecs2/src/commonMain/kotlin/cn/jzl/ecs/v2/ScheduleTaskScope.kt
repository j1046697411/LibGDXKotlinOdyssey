package cn.jzl.ecs.v2

import kotlin.coroutines.Continuation
import kotlin.coroutines.RestrictsSuspension
import kotlin.time.Duration

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