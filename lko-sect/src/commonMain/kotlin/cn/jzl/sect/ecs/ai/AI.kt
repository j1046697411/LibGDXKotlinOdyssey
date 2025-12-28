package cn.jzl.sect.ecs.ai

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.observers.exec
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.*
import cn.jzl.ecs.system.Updatable
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.logger.Logger
import cn.jzl.sect.ecs.planning.OnPlanExecutionCompleted
import cn.jzl.sect.ecs.planning.PlanningService
import cn.jzl.sect.ecs.planning.planningAddon
import kotlin.time.Duration

enum class AiAgent { Idle, Running }

val aiAddon = createAddon("ai") {
    install(planningAddon)
    injects {
        this bind singleton { new(::AIService) }
    }
    components {
        world.componentId<AiAgent>()
    }
}

class AIService(world: World) : EntityRelationContext(world), Updatable {

    private val planningService by world.di.instance<PlanningService>()
    private val aiAgents = world.query { AiAgentContext(this) }
    private val logger: Logger by world.di.instance(argProvider = { "AIService" })

    init {
        world.observe<OnPlanExecutionCompleted>().exec(aiAgents) {
            logger.debug { "OnPlanExecutionCompleted exec $entity ${it.agent}" }
            it.agent = AiAgent.Idle
        }
    }

    fun ai(context: EntityCreateContext, entity: Entity) = context.run {
        entity.addComponent(AiAgent.Idle)
    }

    override fun update(delta: Duration) {
        aiAgents.filter { agent == AiAgent.Idle }.take(5).forEach { planning() }
    }

    private fun AiAgentContext.planning() {
        val plan = planningService.planBestGoal(entity) ?: return run {
            logger.debug { "${named?.name} No executable tasks" }
        }
        planningService.execPlan(entity, plan)
        agent = AiAgent.Running
    }

    class AiAgentContext(world: World) : EntityQueryContext(world) {
        val named by component<Named?>()
        var agent by component<AiAgent>()
    }
}