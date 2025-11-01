package cn.jzl.ecs.v2

/**
 * 调度任务循环控制器接口
 *
 * 用于控制循环任务的执行
 */
fun interface ScheduleTaskLooper {
    /**
     * 停止循环执行
     */
    fun stop()
}