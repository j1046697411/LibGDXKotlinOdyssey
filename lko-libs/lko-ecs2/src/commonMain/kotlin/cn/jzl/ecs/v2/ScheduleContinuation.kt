package cn.jzl.ecs.v2

import kotlin.coroutines.Continuation

interface ScheduleContinuation<R> : Continuation<R> {
    val scheduleDispatcher: ScheduleDispatcher
}