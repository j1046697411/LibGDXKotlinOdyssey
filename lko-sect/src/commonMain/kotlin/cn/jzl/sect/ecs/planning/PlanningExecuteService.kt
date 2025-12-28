package cn.jzl.sect.ecs.planning

import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ecs.observers.Observer
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.system.Updatable
import cn.jzl.sect.ecs.logger.Logger
import cn.jzl.sect.ecs.time.Countdown
import cn.jzl.sect.ecs.time.OnCountdownComplete
import cn.jzl.sect.ecs.time.TimeService
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.isActive
import kotlin.coroutines.*
import kotlin.time.Duration

sealed class OnPlanExecutionCompleted

interface TaskDispatcher {
    fun dispatch(task: Runnable)
}

class PlanningExecuteService(world: World) : EntityRelationContext(world), Updatable, TaskDispatcher {

    private val log: Logger by world.di.instance(argProvider = { "PlanningExecuteService" })

    private val waitingTasks = mutableListOf<Runnable>()
    private val activeTasks = mutableListOf<Runnable>()

    override fun dispatch(task: Runnable) {
        waitingTasks.add(task)
    }

    private fun <R> executeTask(agent: Entity, task: suspend EntityTaskContext.() -> R): Entity {
        return world.childOf(agent) {
            val entityPlanContext = EntityPlanContext<R>(world, this@PlanningExecuteService, agent, it)
            val duration = task.createCoroutine(entityPlanContext, entityPlanContext)
            dispatch { duration.resume(Unit) }
        }
    }

    fun executePlan(agent: Entity, plan: Plan): Entity = executeTask(agent) {
        log.debug { "agent $agent, start exec ${plan.goal.name}" }
        plan.actions.forEach { it.task.run { exec() } }
        log.debug { "agent $agent, end exec ${plan.goal.name}" }
    }

    override fun update(delta: Duration) {
        activeTasks.clear()
        activeTasks.addAll(waitingTasks)
        waitingTasks.clear()
        activeTasks.forEach { it.run() }
        activeTasks.clear()
    }

    private class EntityPlanContext<R>(
        world: World,
        private val taskDispatcher: TaskDispatcher,
        agent: Entity,
        dispatcher: Entity
    ) : EntityTaskContext(world, agent, dispatcher), Continuation<R> {

        override val context: CoroutineContext get() = EmptyCoroutineContext

        override suspend fun <R> suspendTask(block: (Continuation<R>) -> Unit): R = suspendCoroutine {
            taskDispatcher.dispatch { block(it) }
        }

        override fun resumeWith(result: Result<R>) {
            world.destroy(dispatcher)
            world.emit<OnPlanExecutionCompleted>(agent)
        }
    }
}

@RestrictsSuspension
abstract class EntityTaskContext(
    world: World,
    val agent: Entity,
    val dispatcher: Entity
) : EntityRelationContext(world) {
    internal val log: Logger by world.di.instance(argProvider = { "EntityTaskContext" })
    internal val timeService by world.di.instance<TimeService>()

    abstract suspend fun <R> suspendTask(block: (Continuation<R>) -> Unit): R
}

suspend fun EntityTaskContext.delay(delay: Duration): Unit = suspendTask { continuation ->
    val currentTime = timeService.getCurrentGameTime()
    log.debug { "start delay $agent startTime = $currentTime, delay = $delay" }
    var observer: Observer? = null
    observer = world.observe<OnCountdownComplete>(dispatcher).exec {
        if (!continuation.context.isActive) return@exec
        continuation.resume(Unit)
        log.debug { "end delay $agent completeTime = ${timeService.getCurrentGameTime()}" }
        observer?.close()
    }
    world.entity(dispatcher) {
        it.addComponent(Countdown(currentTime, delay))
    }
}

fun interface ActionTask {
    suspend fun EntityTaskContext.exec()
}