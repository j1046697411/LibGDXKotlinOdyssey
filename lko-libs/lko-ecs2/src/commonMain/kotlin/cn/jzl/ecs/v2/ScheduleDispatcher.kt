package cn.jzl.ecs.v2

import kotlin.time.Duration

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
    fun addMainTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: ScheduleTaskPriority,
        task: (Duration) -> Unit
    )

    /**
     * 添加下一帧执行的任务
     *
     * @param scheduleDescriptor 调度器描述符
     * @param priority 任务优先级
     * @param task 要执行的任务函数，接收持续时间参数
     */
    fun addWorkTask(
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