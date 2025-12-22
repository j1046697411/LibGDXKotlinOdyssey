package cn.jzl.sect.v2

import cn.jzl.di.DI
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.AStarPlanner
import cn.jzl.sect.ecs.Action
import cn.jzl.sect.ecs.ActionEffect
import cn.jzl.sect.ecs.ActionProvider
import cn.jzl.sect.ecs.GOAPGoal
import cn.jzl.sect.ecs.PlanningService
import cn.jzl.sect.ecs.Precondition
import cn.jzl.sect.ecs.StateKey
import cn.jzl.sect.ecs.WorldStateReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlanningServiceTest {

    @Test
    fun plan_returnsEmptyPlan_whenGoalAlreadySatisfied() {
        val world = World(di = DI {})
        val service = PlanningService(world)
        service.register(actionProvider = object : ActionProvider {
            override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> = emptySequence()
        })

        val planner = AStarPlanner(service)
        val agent: Entity = Entity(id = 1, version = 0)

        val goal = object : GOAPGoal {
            override val name: String = "already-satisfied"
            override val priority: Double = 1.0
            override fun isSatisfied(worldState: WorldStateReader, agent: Entity): Boolean = true
            override fun calculateDesirability(worldState: WorldStateReader, agent: Entity): Double = 1.0
            override fun calculateHeuristic(worldState: WorldStateReader, agent: Entity): Double = 0.0
        }

        val plan = planner.plan(agent, goal)

        assertNotNull(plan)
        assertEquals(0.0, plan.cost)
        assertEquals(0, plan.actions.size)
    }

    @Test
    fun plan_findsSingleActionPlan_whenOneActionSatisfiesGoal() {
        val world = World(di = DI {})
        val service = PlanningService(world)

        val doneKey = TestBoolKey("done")
        service.register(actionProvider = object : ActionProvider {
            override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> =
                sequenceOf(
                    object : Action {
                        override val name: String = "do-it"
                        override val preconditions: Sequence<Precondition> = emptySequence()
                        override val effects: Sequence<ActionEffect> = sequenceOf(
                            ActionEffect { stateWriter, _ -> stateWriter.setValue(doneKey, true) }
                        )
                        override val cost: Double = 1.0
                        override val task: suspend World.(Entity) -> Unit = { }
                    }
                )
        })

        val planner = AStarPlanner(service)
        val agent: Entity = Entity(id = 1, version = 0)

        val goal = object : GOAPGoal {
            override val name: String = "reach-done"
            override val priority: Double = 1.0
            override fun isSatisfied(worldState: WorldStateReader, agent: Entity): Boolean =
                worldState.getValue(agent, doneKey) == true

            override fun calculateDesirability(worldState: WorldStateReader, agent: Entity): Double = 1.0
            override fun calculateHeuristic(worldState: WorldStateReader, agent: Entity): Double =
                if (isSatisfied(worldState, agent)) 0.0 else 1.0
        }

        val plan = planner.plan(agent, goal)

        assertNotNull(plan)
        assertEquals(listOf("do-it"), plan.actions.map { it.name })
    }

    private data class TestBoolKey(val id: String) : StateKey<Boolean>
}
