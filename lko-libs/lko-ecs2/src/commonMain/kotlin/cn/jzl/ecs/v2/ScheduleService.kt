package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScheduleService(private val world: World) {
    private val schedules = ObjectFastList<Schedule>(256)
    private val activeScheduleBits = BitSet.Companion(256)
    private val recycledSchedules = ObjectFastList<Int>(128)
    private val waitNextFrameTasks = ObjectFastList<FrameTask>(128)
    private val delayFrameTasks = ObjectFastList<DelayFrameTask>(128)
    private val frameTasks = ObjectFastList<FrameTask>(128)
    private val lock = ReentrantLock()

    private fun createSchedule(priority: Int): Schedule {
        val oldSchedule = if (recycledSchedules.isNotEmpty()) {
            recycledSchedules.removeLast().let { schedules[it] }
        } else {
            Schedule(schedules.size, -1, priority).also { schedules.insertLast(it) }
        }
        val schedule = oldSchedule.upgrade(priority)
        activeScheduleBits.set(schedule.id)
        schedules[schedule.id] = schedule
        return schedule
    }

    private fun recycleSchedule(schedule: Schedule) {
        activeScheduleBits.clear(schedule.id)
        recycledSchedules.add(schedule.id)
    }

    private fun Schedule.upgrade(priority: Int = 0): Schedule = Schedule(id, version + 1, priority)

    fun schedule(
        priority: Int = 0,
        scheduleTask: suspend ScheduleScore.() -> Unit
    ): Schedule {
        val schedule = createSchedule(priority)
        val scheduleScore = ScheduleScoreImpl(world, schedule)
        addNextFrameTask(schedule) {
            scheduleTask.startCoroutine(scheduleScore, Continuation(EmptyCoroutineContext) {
                recycleSchedule(schedule)
            })
        }
        return schedule
    }

    private fun addNextFrameTask(schedule: Schedule, scheduleTask: (Duration) -> Unit) {
        waitNextFrameTasks.add(FrameTask(schedule, scheduleTask))
    }

    private fun addDelayFrameTask(schedule: Schedule, delay: Duration, scheduleTask: (Duration) -> Unit) {
        delayFrameTasks.add(DelayFrameTask(delay, 0.seconds, FrameTask(schedule, scheduleTask)))
    }

    fun update(delta: Duration) {
        lock.withLock {
            if (delayFrameTasks.isNotEmpty()) {
                val iterator = delayFrameTasks.iterator()
                while (iterator.hasNext()) {
                    val delayFrameTask = iterator.next()
                    delayFrameTask.waitTime += delta
                    if (delayFrameTask.waitTime >= delayFrameTask.delay) {
                        frameTasks.insertLast(delayFrameTask.frameTask)
                        iterator.remove()
                    }
                }
            }
            if (waitNextFrameTasks.isNotEmpty()) {
                frameTasks.addAll(waitNextFrameTasks)
                waitNextFrameTasks.clear()
            }
        }
        if (frameTasks.isNotEmpty()) {
            frameTasks.sortByDescending { it.schedule.priority }
            frameTasks.forEach { (schedule, task) -> if (isActive(schedule)) task(delta) }
            frameTasks.clear()
        }
    }

    private fun isActive(schedule: Schedule): Boolean = schedule.id in activeScheduleBits && schedule == schedules[schedule.id]

    private data class FrameTask(val schedule: Schedule, val scheduleTask: (Duration) -> Unit)
    private data class DelayFrameTask(val delay: Duration, var waitTime: Duration, val frameTask: FrameTask)

    private inner class ScheduleScoreImpl(
        override val world: World,
        override val schedule: Schedule,
    ) : ScheduleScore, EntityComponentContext by world.entityUpdateContext {

        override val active: Boolean get() = isActive(schedule)

        override suspend fun waitNextFrame(): Duration = suspendCoroutine { continuation ->
            addNextFrameTask(schedule) { continuation.resume(it) }
        }

        override suspend fun delay(delay: Duration): Unit = suspendCoroutine { continuation ->
            addDelayFrameTask(schedule, delay) { continuation.resume(Unit) }
        }
    }
}