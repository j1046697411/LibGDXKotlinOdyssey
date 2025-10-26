package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ScheduleService(private val world: World) {
    private val schedules = ObjectFastList<Schedule>(256)
    private val scheduleDescriptors = ObjectFastList<ScheduleDescriptor?>(256)

    private val activeScheduleBits = BitSet(256)
    private val recycledSchedules = ObjectFastList<Int>(128)
    private val waitNextFrameTasks = ObjectFastList<FrameTask>(128)
    private val delayFrameTasks = ObjectFastList<DelayFrameTask>(128)
    private val frameTasks = ObjectFastList<FrameTask>(128)

    private val lock = ReentrantLock()
    private val descriptorLock = ReentrantLock()

    private fun createSchedule(priority: SchedulePriority, scheduleName: String): Schedule {
        val oldSchedule = if (recycledSchedules.isNotEmpty()) {
            recycledSchedules.removeLast().let { schedules[it] }
        } else {
            Schedule(schedules.size, -1, priority.priority).also { schedules.insertLast(it) }
        }
        val schedule = oldSchedule.upgrade(priority.priority)
        activeScheduleBits.set(schedule.id)
        schedules[schedule.id] = schedule
        updateScheduleDescriptor(schedule, scheduleName.ifEmpty { "schedule#${schedule.id}-${schedule.version}" })
        return schedule
    }

    internal val Schedule.descriptor: ScheduleDescriptor get() = scheduleDescriptor(this, "schedule#${id}-${version}")

    fun getSchedule(id: Int): Schedule {
        check(id in activeScheduleBits) { "Schedule $id is not active" }
        return schedules[id]
    }

    private fun updateScheduleDescriptor(schedule: Schedule, scheduleName: String) {
        check(isActive(schedule)) { "Schedule $schedule is not active" }
        val scheduleDescriptor = scheduleDescriptor(schedule, scheduleName)
        scheduleDescriptor.close()
        scheduleDescriptor.scheduleName = scheduleName
        scheduleDescriptor.schedule = schedule
        scheduleDescriptors[schedule.id] = scheduleDescriptor
    }

    private fun scheduleDescriptor(schedule: Schedule, scheduleName: String): ScheduleDescriptor {
        return scheduleDescriptors.getOrNull(schedule.id) ?: descriptorLock.withLock {
            scheduleDescriptors.ensureCapacity(schedule.id + 1, null)
            scheduleDescriptors.getOrNull(schedule.id) ?: ScheduleDescriptor(schedule, scheduleName).also { scheduleDescriptors[schedule.id] = it }
        }
    }

    private fun recycleSchedule(schedule: Schedule) {
        activeScheduleBits.clear(schedule.id)
        recycledSchedules.add(schedule.id)
    }

    private fun Schedule.upgrade(priority: Int = 0): Schedule = Schedule(id, version + 1, priority)
    private fun schedulePriority(schedule: Schedule): SchedulePriority = SchedulePriority.entries.first { it.priority == schedule.priority }

    fun schedule(
        scheduleName: String,
        priority: SchedulePriority = SchedulePriority.NORMAL,
        scheduleTask: suspend ScheduleScore.() -> Unit
    ): Schedule {
        val schedule = createSchedule(priority, scheduleName)
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
            frameTasks.sortedBy { it.schedule.priority }
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
        override val priority: SchedulePriority by lazy { schedulePriority(schedule) }

        override suspend fun <R> suspendScheduleCoroutine(block: (Continuation<R>) -> Unit): R = suspendCoroutine { continuation ->
            val scheduleContinuation = Continuation(continuation.context) { result ->
                addNextFrameTask(schedule) { continuation.resumeWith(result) }
            }
            block(scheduleContinuation)
        }

        override suspend fun waitNextFrame(): Duration = suspendCoroutine { continuation ->
            addNextFrameTask(schedule) { continuation.resume(it) }
        }

        override suspend fun delay(delay: Duration): Unit = suspendCoroutine { continuation ->
            addDelayFrameTask(schedule, delay) { continuation.resume(Unit) }
        }
    }

    data class ScheduleDescriptor(
        var schedule: Schedule,
        var scheduleName: String,
        val dependencies: BitSet = BitSet(), //Schedule 依赖的其他Schedule位
        val readComponentBits: BitSet = BitSet(), //Schedule 依赖的读取的组件位
        val writeComponentBits: BitSet = BitSet() //Schedule 依赖的写入的组件位
    ) : AutoCloseable {

        val scheduleDependencies = BitSet()

        override fun close() {
            dependencies.clear()
            readComponentBits.clear()
            writeComponentBits.clear()
            scheduleDependencies.clear()
        }
    }
}

class ScheduleAssistant(
    private val scheduleService: ScheduleService,
    private val coroutineScope: CoroutineScope,
    private val schedulePriority: SchedulePriority
) : AutoCloseable {

    val scheduleBits: BitSet = BitSet() //Schedule 组内的Schedule位
    private val dirtyData = atomic(false)
    private val pendingSchedules = ObjectFastList<Schedule>()

    fun addSchedule(schedule: Schedule): Unit = scheduleService.run {
        scheduleBits.set(schedule.id)
        dirtyData.value = true
    }

    fun removeSchedule(schedule: Schedule): Unit = scheduleService.run {
        scheduleBits.clear(schedule.id)
        dirtyData.value = true
    }

    fun update(delta: Duration): Unit = scheduleService.run {

    }

    private fun updateDirtyData(): Unit = scheduleService.run {
        if (dirtyData.compareAndSet(expect = true, update = false)) {
            pendingSchedules.clear()
            pendingSchedules.safeInsertLast(scheduleBits.size) {

            }
            pendingSchedules.addAll(scheduleBits.map { getSchedule(it) })
            pendingSchedules.forEach {
                val descriptor = it.descriptor
            }
        }
    }

    override fun close() {
        scheduleBits.clear()
    }
}
