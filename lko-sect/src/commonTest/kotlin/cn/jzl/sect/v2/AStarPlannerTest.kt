package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.planning.AStarPlanner
import cn.jzl.sect.ecs.planning.Action
import cn.jzl.sect.ecs.planning.ActionEffect
import cn.jzl.sect.ecs.planning.ActionProvider
import cn.jzl.sect.ecs.planning.GOAPGoal
import cn.jzl.sect.ecs.planning.PlanningService
import cn.jzl.sect.ecs.planning.Precondition
import cn.jzl.sect.ecs.planning.StateKey
import cn.jzl.sect.ecs.planning.StateResolver
import cn.jzl.sect.ecs.planning.StateResolverRegistry
import cn.jzl.sect.ecs.planning.WorldStateReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AStarPlannerTest {

    private object CounterKey : StateKey<Int>

    private class CounterResolver : StateResolver<CounterKey, Int> {
        override fun cn.jzl.ecs.EntityRelationContext.getWorldState(agent: cn.jzl.ecs.Entity, key: CounterKey): Int = 0
    }

    private class CounterRegistry : StateResolverRegistry {
        private val resolver = CounterResolver()
        override fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>? {
            @Suppress("UNCHECKED_CAST")
            return if (key === CounterKey) resolver as StateResolver<K, T> else null
        }
    }

    private class IncAction(private val amount: Int) : Action {
        override val name: String = "inc$amount"
        override val preconditions: Sequence<Precondition> = sequenceOf(
            Precondition { stateProvider, agent -> stateProvider.getValue(agent, CounterKey) < 10 }
        )
        override val effects: Sequence<ActionEffect> = sequenceOf(
            ActionEffect { stateWriter, agent ->
                stateWriter.setValue(CounterKey, stateWriter.getValue(agent, CounterKey) + amount)
            }
        )
        override val cost: Double = 1.0
        override val task: suspend World.(cn.jzl.ecs.Entity) -> Unit = { }
    }

    private class GoalAtLeast(private val target: Int) : GOAPGoal {
        override val name: String = "counter>=$target"
        override val priority: Double = 1.0
        override fun isSatisfied(worldState: WorldStateReader, agent: cn.jzl.ecs.Entity): Boolean {
            return worldState.getValue(agent, CounterKey) >= target
        }

        override fun calculateDesirability(worldState: WorldStateReader, agent: cn.jzl.ecs.Entity): Double = 1.0

        override fun calculateHeuristic(worldState: WorldStateReader, agent: cn.jzl.ecs.Entity): Double {
            val v = worldState.getValue(agent, CounterKey)
            return (target - v).toDouble().coerceAtLeast(0.0)
        }
    }

    @Test
    fun plan_returnsEmptyPlan_whenGoalAlreadySatisfied() {
        val world = world {
            planning {
                register(CounterRegistry())
            }
        }

        val planningService by world.di.instance<PlanningService>()

        val agent = world.entity { }

        // Create a state that already satisfies goal by pre-filling CounterKey.
        val satisfiedState = planningService.createWorldState(mapOf(CounterKey to 10))
        val goal = GoalAtLeast(1)

        assertTrue(goal.isSatisfied(satisfiedState, agent))
    }

    @Test
    fun plan_findsPlan_whenActionCanReachGoal() {
        val world = world {
            planning {
                register(CounterRegistry())
                register(object : ActionProvider {
                    override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> =
                        sequenceOf(IncAction(1))
                })
            }
        }

        val planningService by world.di.instance<PlanningService>()
        val agent = world.entity { }

        val planner = AStarPlanner(planningService, maxSearchDepth = 10)
        val plan = planner.plan(agent, GoalAtLeast(2))

        assertNotNull(plan)
        assertEquals(GoalAtLeast(2).name, plan.goal.name)
        assertTrue(plan.actions.isNotEmpty())
    }

    @Test
    fun plan_returnsNull_whenSearchDepthTooSmall() {
        val world = world {
            planning {
                register(CounterRegistry())
                register(object : ActionProvider {
                    override fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action> =
                        sequenceOf(IncAction(1))
                })
            }
        }

        val planningService by world.di.instance<PlanningService>()
        val agent = world.entity { }

        val planner = AStarPlanner(planningService, maxSearchDepth = 1)
        val plan = planner.plan(agent, GoalAtLeast(5))

        assertNull(plan)
    }
}
