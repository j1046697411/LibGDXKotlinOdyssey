package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.IntFastList
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.di.instance
import kotlin.time.Duration

/**
 * 调度服务类，负责管理ECS系统中的调度器生命周期和任务执行
 *
 * 此类提供了调度器的创建、管理和更新功能，支持初始化任务、下一帧任务和延迟任务的调度
 * 使用对象池技术优化调度器的创建和销毁，提高性能
 *
 * @property world 世界实例，用于获取调度器分发器和实体组件上下文
 */
class ScheduleService(private val world: World) {

    /**
     * 调度器分发器实例，负责实际的任务调度执行
     */
    private val scheduleDispatcher by world.instance<ScheduleDispatcher>()

    /**
     * 调度器对象池，存储所有已创建的调度器实例
     * 初始容量为1024，支持动态扩容
     */
    private val schedules = ObjectFastList<Schedule>(1024)

    /**
     * 可回收的调度器ID列表，用于对象池优化
     * 当调度器被销毁时，其ID会被添加到该列表以供重用
     */
    private val recycleScheduleIds = IntFastList(128)

    /**
     * 活跃调度器位图，用于快速判断调度器是否处于活跃状态
     * 每个位对应一个调度器ID，设置为1表示活跃，0表示非活跃
     */
    private val activeSchedules = BitSet(1024)

    /**
     * 创建新的调度器实例
     *
     * 优先从回收池中获取可重用的调度器ID，如果回收池为空则创建新的调度器
     * 新创建的调度器会被标记为活跃状态并添加到调度器列表中
     *
     * @return 新创建的调度器实例
     */
    private fun createSchedule(): Schedule {
        val schedule = if (recycleScheduleIds.isNotEmpty()) {
            val id = recycleScheduleIds.removeLast()
            schedules[id].upgrade()
        } else {
            Schedule(schedules.size, 0).also { schedules.insertLast(it) }
        }
        schedules[schedule.id] = schedule
        activeSchedules.set(schedule.id)
        return schedule
    }

    private fun releaseSchedule(schedule: Schedule) {
        recycleScheduleIds.add(schedule.id)
        activeSchedules.clear(schedule.id)
    }

    fun isActive(schedule: Schedule): Boolean = schedule.id in activeSchedules && schedules.getOrNull(schedule.id) == schedule

    /**
     * 升级调度器版本
     *
     * 当重用调度器ID时，需要升级其版本号以确保唯一性
     * 版本号递增，避免版本冲突
     *
     * @receiver 要升级的调度器实例
     * @return 升级后的新调度器实例
     */
    private fun Schedule.upgrade(): Schedule = Schedule(id, version + 1)

    /**
     * 创建并启动一个新的调度器
     *
     * 创建调度器实例，设置调度器作用域，并添加初始化任务
     * 调度器会在下一帧开始执行指定的代码块
     *
     * @param scheduleName 调度器名称，用于标识和调试
     * @param block 要在调度器中执行的协程代码块
     * @return 新创建的调度器实例
     */
    fun schedule(
        scheduleName: String,
        scheduleTaskPriority: ScheduleTaskPriority = ScheduleTaskPriority.NORMAL,
        block: suspend ScheduleScope.() -> Unit
    ): ScheduleDescriptor {
        val schedule = createSchedule()
        val scheduleScope = ScheduleScopeImpl(world, schedule, scheduleName.ifEmpty { "schedule-${schedule.id}-${schedule.version}" }, scheduleDispatcher)
        return scheduleScope.startCoroutine(scheduleTaskPriority, block) { releaseSchedule(schedule) }
    }

    /**
     * 更新所有调度器的状态
     *
     * 调用调度器分发器的更新方法，处理所有待执行的任务
     * 包括初始化任务、下一帧任务和延迟任务的执行
     *
     * @param delta 时间增量，表示自上次更新以来经过的时间
     */
    fun update(delta: Duration): Unit = scheduleDispatcher.update(delta)
}

expect fun threadYield(): Unit