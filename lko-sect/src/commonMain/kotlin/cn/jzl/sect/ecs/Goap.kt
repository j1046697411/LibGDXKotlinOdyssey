package cn.jzl.sect.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.WorldOwner
import cn.jzl.ecs.addon.AddonSetup
import cn.jzl.ecs.addon.WorldSetup
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.system.Phase
import java.util.PriorityQueue

interface WorldState : GOAPStateProvider {
    val keys: Sequence<StateKey<*>>
    fun copy(): WorldState
    fun merge(other: WorldState): WorldState
    fun satisfies(conditions: WorldState): Boolean
}

interface StateKey<T>

interface StateHandler<T> {
    fun EntityRelationContext.getWorldState(agent: Entity): T
    fun EntityRelationContext.merge(agent: Entity, current: T?, effect: T): T
    fun EntityRelationContext.satisfies(agent: Entity, current: T?, condition: T): Boolean
}

interface GOAPStateProvider {
    fun <T> getValue(agent: Entity, key: StateKey<T>): T
}

interface GOAPActionProvider {
    fun getActions(stateProvider: GOAPStateProvider, agent: Entity): Sequence<GOAPAction>
}

interface GOAPGoalProvider {
    fun getGoals(stateProvider: GOAPStateProvider, agent: Entity): Sequence<GOAPGoal>
}

interface GOAPRegistry : WorldOwner {
    fun <T> register(key: StateKey<T>, stateHandler: StateHandler<T>)

    fun register(actionProvider: GOAPActionProvider)

    fun register(goalProvider: GOAPGoalProvider)
}

@ECSDsl
fun WorldSetup.goap(block: GOAPRegistry.() -> Unit) = install(goapAddon) { config(block) }

@ECSDsl
fun AddonSetup<*>.goap(block: GOAPRegistry.() -> Unit) = install(goapAddon) { config(block) }

interface GOAPGoal {
    val name: String
    val priority: Double
    fun isSatisfied(worldState: WorldState): Boolean
    fun calculateDesirability(worldState: WorldState, agent: Entity): Double
}

interface GOAPAction {
    val name: String
    val preconditions: WorldState
    val effects: WorldState
    val cost: Double
    val task: suspend World.(Entity) -> Unit
}

interface GOAPPlanner {
    fun plan(agent: Entity, goal: GOAPGoal): GOAPPlan?
}

data class GOAPPlan(
    val goal: GOAPGoal,
    val actions: List<GOAPAction>,
    val cost: Double
)

class GOAPBuilder {

    private val configs = mutableListOf<GOAPRegistry.() -> Unit>()

    fun config(block: GOAPRegistry.() -> Unit) {
        configs.add(block)
    }

    internal fun apply(service: GOAPService) {
        configs.forEach { config -> service.config() }
    }
}


val goapAddon = createAddon("goap", { GOAPBuilder() }) {
    injects { this bind singleton { new(::GOAPService) } }
    on(Phase.ADDONS_CONFIGURED) {
        val service by world.di.instance<GOAPService>()
        configuration.apply(service)
    }
}

class AgentWorldState(
    private val goapService: GOAPService,
    private val agent: Entity,
    private val states: MutableMap<StateKey<*>, Any?> = mutableMapOf()
) : WorldState, EntityRelationContext(goapService.world) {

    override val keys: Sequence<StateKey<*>> = states.keys.asSequence()

    override fun copy(): WorldState = AgentWorldState(goapService, agent, states.toMutableMap())

    @Suppress("UNCHECKED_CAST")
    override fun merge(other: WorldState): WorldState {
        val newWorldState = AgentWorldState(goapService, agent, states.toMutableMap())
        for (key in other.keys) {
            key as StateKey<Any?>
            val stateHandler = goapService.getStateHandler(key)
            val current = newWorldState.states[key]
            val effect = other.getValue(agent, key)
            val mergedValue = stateHandler.run { merge(agent, current, effect) }
            newWorldState.states[key] = mergedValue
        }
        return newWorldState
    }

    @Suppress("UNCHECKED_CAST")
    override fun satisfies(conditions: WorldState): Boolean = conditions.keys.all { key ->
        key as StateKey<Any?>
        val stateHandler = goapService.getStateHandler(key)
        val current = states[key]
        val condition = conditions.getValue(agent, key)
        stateHandler.run { satisfies(agent, current, condition) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getValue(agent: Entity, key: StateKey<T>): T {
        return states.getOrPut(key) { goapService.getValue(agent, key) } as T
    }
}

class GOAPService(world: World) : EntityRelationContext(world), GOAPStateProvider, GOAPRegistry {

    private val stateHandlers = mutableMapOf<StateKey<*>, StateHandler<*>>()
    private val actionProviders = mutableSetOf<GOAPActionProvider>()
    private val goalProviders = mutableSetOf<GOAPGoalProvider>()
    private val planner: GOAPPlanner = AStarGOAPPlanner(this)

    override fun <T> register(key: StateKey<T>, stateHandler: StateHandler<T>) {
        stateHandlers[key] = stateHandler
    }

    override fun register(actionProvider: GOAPActionProvider) {
        this.actionProviders.add(actionProvider)
    }

    override fun register(goalProvider: GOAPGoalProvider) {
        this.goalProviders.add(goalProvider)
    }

    fun createWorldState(agent: Entity): WorldState = AgentWorldState(this, agent)

    override fun <T> getValue(agent: Entity, key: StateKey<T>): T {
        return getStateHandler(key).run { getWorldState(agent) }
    }

    fun getAllActions(agent: Entity): Sequence<GOAPAction> = actionProviders.asSequence().flatMap { it.getActions(this, agent) }

    private fun getAllGoals(agent: Entity): Sequence<GOAPGoal> = goalProviders.asSequence().flatMap { it.getGoals(this, agent) }

    fun plan(agent: Entity, goal: GOAPGoal): GOAPPlan? = planner.plan(agent, goal)

    fun planBestGoal(agent: Entity): GOAPPlan? {
        val worldState = createWorldState(agent)
        return getAllGoals(agent)
            .map { goal -> goal to goal.calculateDesirability(worldState, agent) }
            .sortedByDescending { (goal, desirability) -> goal.priority * desirability }
            .mapNotNull { planner.plan(agent, it.first) }.firstOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T> getStateHandler(key: StateKey<T>): StateHandler<T> {
        return stateHandlers.getValue(key) as StateHandler<T>
    }
}


class AStarGOAPPlanner(private val goapService: GOAPService, private val maxSearchDepth: Int = 50) : GOAPPlanner {

    override fun plan(agent: Entity, goal: GOAPGoal): GOAPPlan? {
        val startState = goapService.createWorldState(agent)
        val allActions = goapService.getAllActions(agent)

        // 如果目标已经满足，返回空计划
        if (goal.isSatisfied(startState)) {
            return GOAPPlan(goal, emptyList(), 0.0)
        }

        // 使用优先队列管理开放列表
        val openSet = PriorityQueue<AStarNode>()
        val closedSet = mutableSetOf<String>()

        // 初始节点
        val startNode = AStarNode(
            worldState = startState,
            actions = emptyList(),
            cost = 0.0,
            heuristic = calculateHeuristic(startState, goal)
        )
        openSet.add(startNode)

        // A*搜索主循环
        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            // 检查是否达到目标
            if (goal.isSatisfied(current.worldState)) {
                return GOAPPlan(goal, current.actions, current.cost)
            }

            // 检查搜索深度限制
            if (current.actions.size >= maxSearchDepth) {
                continue
            }

            // 生成状态哈希用于去重
            val stateHash = getStateHash(current.worldState, agent)
            if (stateHash in closedSet) {
                continue
            }
            closedSet.add(stateHash)

            // 扩展节点：尝试所有可用动作
            for (action in allActions) {
                // 检查前置条件是否满足
                if (!current.worldState.satisfies(action.preconditions)) continue
                // 应用动作效果，创建新状态
                val newState = current.worldState.merge(action.effects)
                val newCost = current.cost + action.cost
                val newHeuristic = calculateHeuristic(newState, goal)
                val newActions = current.actions + action

                val newNode = AStarNode(
                    worldState = newState,
                    actions = newActions,
                    cost = newCost,
                    heuristic = newHeuristic
                )

                // 检查新状态是否已经在关闭列表中
                val newStateHash = getStateHash(newState, agent)
                if (newStateHash !in closedSet) {
                    openSet.add(newNode)
                }
            }
        }
        return null
    }

    /**
     * 计算启发式值：估计从当前状态到目标状态的成本
     * 这里使用一个简单的启发式：如果目标不满足，返回1.0，否则返回0.0
     * 更复杂的实现可以计算需要改变的状态数量等
     */
    private fun calculateHeuristic(state: WorldState, goal: GOAPGoal): Double {
        return if (goal.isSatisfied(state)) {
            0.0
        } else {
            // 简单的启发式：计算不满足的条件数量
            var unsatisfiedCount = 0
            // 这里可以改进为更精确的启发式计算
            // 例如：计算需要改变的状态键数量
            1.0
        }
    }

    /**
     * 生成状态哈希用于去重
     * 基于状态的所有键值对生成唯一标识
     */
    private fun getStateHash(state: WorldState, agent: Entity): String {
        // 使用状态的键和值生成哈希
        val keys = state.keys.sortedBy { it.hashCode() }
        return keys.joinToString("|") { key ->
            // 获取键对应的值并包含在哈希中
            try {
                @Suppress("UNCHECKED_CAST")
                val value = state.getValue(agent, key as StateKey<Any?>)
                "$key=$value"
            } catch (e: Exception) {
                "$key=null"
            }
        }
    }


    /**
     * A*搜索节点，表示搜索过程中的一个状态
     */
    private data class AStarNode(
        val worldState: WorldState,
        val actions: List<GOAPAction>,
        val cost: Double,
        val heuristic: Double
    ) : Comparable<AStarNode> {
        val fCost: Double get() = cost + heuristic

        override fun compareTo(other: AStarNode): Int {
            return fCost.compareTo(other.fCost)
        }
    }
}