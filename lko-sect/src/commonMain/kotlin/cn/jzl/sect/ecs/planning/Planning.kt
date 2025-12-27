package cn.jzl.sect.ecs.planning

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
import kotlin.collections.plus
import kotlin.getValue

/**
 * 规划系统包，包含GOAP（面向目标的动作规划）系统的核心组件
 *
 * 主要功能：
 * 1. 定义世界状态和智能体状态
 * 2. 提供动作、目标和规划器接口
 * 3. 实现A*搜索算法进行规划
 * 4. 支持状态解析和注册机制
 * 5. 提供规划服务和addon配置
 */

/**
 * 世界状态接口
 * 表示世界的当前状态
 */
interface WorldState : WorldStateReader {
    /**
     * 当前状态中的所有键
     */
    val stateKeys: Sequence<StateKey<*>>
}

/**
 * 智能体状态接口
 * 表示智能体的当前状态，继承自世界状态
 */
interface AgentState : WorldState {
    /**
     * 创建状态副本
     *
     * @return 状态副本
     */
    fun copy(): AgentState

    /**
     * 合并动作效果到当前状态
     *
     * @param effects 动作效果序列
     * @return 合并后的新状态
     */
    fun mergeEffects(effects: Sequence<ActionEffect>): AgentState

    /**
     * 检查当前状态是否满足条件序列
     *
     * @param conditions 条件序列
     * @return 是否满足所有条件
     */
    fun satisfiesConditions(conditions: Sequence<Precondition>): Boolean
}

/**
 * 世界状态实现类
 *
 * @param map 状态键值对映射
 */
@JvmInline
value class WorldStateImpl(private val map: Map<StateKey<*>, Any?>) : WorldState {
    override val stateKeys: Sequence<StateKey<*>> get() = map.keys.asSequence()

    @Suppress("UNCHECKED_CAST")
    override fun <K : StateKey<T>, T> getValue(agent: Entity, key: K): T {
        return map.getValue(key) as T
    }
}

/**
 * 状态键接口
 * 用于标识状态值的类型
 *
 * @param T 状态值类型
 */
interface StateKey<T>

/**
 * 状态解析器接口
 * 用于从世界中获取特定键的状态值
 *
 * @param K 状态键类型
 * @param T 状态值类型
 */
interface StateResolver<K : StateKey<T>, T> {
    /**
     * 从世界中获取特定智能体的状态值
     *
     * @param agent 智能体实体
     * @param key 状态键
     * @return 状态值
     */
    fun EntityRelationContext.getWorldState(agent: Entity, key: K): T
}

/**
 * 状态解析器注册表接口
 * 用于注册和获取状态解析器
 */
interface StateResolverRegistry {
    /**
     * 获取特定状态键的解析器
     *
     * @param key 状态键
     * @return 状态解析器，可为空
     */
    fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T>?
}

/**
 * 世界状态读取器接口
 * 用于读取世界状态
 */
interface WorldStateReader {
    /**
     * 获取特定智能体的状态值
     *
     * @param agent 智能体实体
     * @param key 状态键
     * @return 状态值
     */
    fun <K : StateKey<T>, T> getValue(agent: Entity, key: K): T
}

/**
 * 世界状态写入器接口
 * 用于写入世界状态
 */
interface WorldStateWriter : WorldStateReader {
    /**
     * 设置状态值
     *
     * @param key 状态键
     * @param value 状态值
     */
    fun <K : StateKey<T>, T> setValue(key: K, value: T)
}

fun WorldStateWriter.increase(agent: Entity, key: StateKey<Int>, value: Int) {
    setValue(key, getValue(agent, key) + value)
}

fun WorldStateWriter.decrease(agent: Entity, key: StateKey<Int>, value: Int) {
    setValue(key, getValue(agent, key) - value)
}

fun WorldStateWriter.increase(agent: Entity, key: StateKey<Long>, value: Long) {
    setValue(key, getValue(agent, key) + value)
}

fun WorldStateWriter.decrease(agent: Entity, key: StateKey<Long>, value: Long) {
    setValue(key, getValue(agent, key) - value)
}

interface ActionProvider {
    fun getActions(stateProvider: WorldStateReader, agent: Entity): Sequence<Action>
}

fun interface Precondition {
    fun satisfiesCondition(stateProvider: WorldStateReader, agent: Entity): Boolean
}

fun interface ActionEffect {
    fun apply(stateWriter: WorldStateWriter, agent: Entity)
}

interface GoalProvider {
    fun getGoals(stateProvider: WorldStateReader, agent: Entity): Sequence<GOAPGoal>
}

interface PlanningRegistry : WorldOwner {
    fun register(stateHandlerProvider: StateResolverRegistry)

    fun register(actionProvider: ActionProvider)

    fun register(goalProvider: GoalProvider)
}

@ECSDsl
fun WorldSetup.planning(block: PlanningRegistry.() -> Unit) = install(planningAddon) { config(block) }

@ECSDsl
fun AddonSetup<*>.planning(block: PlanningRegistry.() -> Unit) = install(planningAddon) { config(block) }

interface GOAPGoal {
    val name: String
    val priority: Double
    fun isSatisfied(worldState: WorldStateReader, agent: Entity): Boolean
    fun calculateDesirability(worldState: WorldStateReader, agent: Entity): Double
    fun calculateHeuristic(worldState: WorldStateReader, agent: Entity): Double
}

interface Action {
    val name: String
    val preconditions: Sequence<Precondition>
    val effects: Sequence<ActionEffect>
    val cost: Double
    val task: suspend World.(Entity) -> Unit
}

interface Planner {
    fun plan(agent: Entity, goal: GOAPGoal): Plan?
}

data class Plan(
    val goal: GOAPGoal,
    val actions: List<Action>,
    val cost: Double
)

class GOAPBuilder {

    private val configs = mutableListOf<PlanningRegistry.() -> Unit>()

    fun config(block: PlanningRegistry.() -> Unit) {
        configs.add(block)
    }

    internal fun apply(service: PlanningService) {
        configs.forEach { config -> service.config() }
    }
}

val planningAddon = createAddon("planning", { GOAPBuilder() }) {
    injects { this bind singleton { new(::PlanningService) } }
    on(Phase.ADDONS_CONFIGURED) {
        val service by world.di.instance<PlanningService>()
        configuration.apply(service)
    }
}

class AgentWorldState(
    private val planningService: PlanningService,
    private val agent: Entity,
    private val states: MutableMap<StateKey<*>, Any?> = mutableMapOf()
) : AgentState, EntityRelationContext(planningService.world), WorldStateWriter {

    override val stateKeys: Sequence<StateKey<*>> = states.keys.asSequence()

    override fun copy(): AgentState = AgentWorldState(planningService, agent, states.toMutableMap())

    override fun mergeEffects(effects: Sequence<ActionEffect>): AgentState {
        val newWorldState = AgentWorldState(planningService, agent, states.toMutableMap())
        effects.forEach { it.apply(newWorldState, agent) }
        return newWorldState
    }

    override fun satisfiesConditions(
        conditions: Sequence<Precondition>
    ): Boolean = conditions.all { it.satisfiesCondition(this, agent) }

    @Suppress("UNCHECKED_CAST")
    override fun <K : StateKey<T>, T> getValue(agent: Entity, key: K): T {
        return states.getOrPut(key) { planningService.getValue(agent, key) } as T
    }

    override fun <K : StateKey<T>, T> setValue(key: K, value: T) {
        states[key] = value
    }
}

class PlanningService(world: World) : EntityRelationContext(world), WorldStateReader, PlanningRegistry {

    private val actionProviders = mutableSetOf<ActionProvider>()
    private val goalProviders = mutableSetOf<GoalProvider>()
    private val stateHandlerProviders = mutableListOf<StateResolverRegistry>()
    private val planner: Planner = AStarPlanner(this)

    override fun register(stateHandlerProvider: StateResolverRegistry) {
        this.stateHandlerProviders.add(stateHandlerProvider)
    }

    override fun register(actionProvider: ActionProvider) {
        this.actionProviders.add(actionProvider)
    }

    override fun register(goalProvider: GoalProvider) {
        this.goalProviders.add(goalProvider)
    }

    fun createAgentState(agent: Entity): AgentState = AgentWorldState(this, agent)
    fun createWorldState(map: Map<StateKey<*>, Any?>): WorldState = WorldStateImpl(map)

    override fun <K : StateKey<T>, T> getValue(agent: Entity, key: K): T {
        return getStateHandler(key).run { getWorldState(agent, key) }
    }

    fun getAllActions(agent: Entity): Sequence<Action> = actionProviders.asSequence().flatMap { it.getActions(this, agent) }

    private fun getAllGoals(agent: Entity): Sequence<GOAPGoal> = goalProviders.asSequence().flatMap { it.getGoals(this, agent) }

    fun plan(agent: Entity, goal: GOAPGoal): Plan? = planner.plan(agent, goal)

    fun planBestGoal(agent: Entity): Plan? {
        val worldState = createAgentState(agent)
        return getAllGoals(agent)
            .map { goal -> goal to goal.calculateDesirability(worldState, agent) }
            .sortedByDescending { (goal, desirability) -> goal.priority * desirability }
            .mapNotNull { planner.plan(agent, it.first) }.firstOrNull()
    }

    fun <K : StateKey<T>, T> getStateHandler(key: K): StateResolver<K, T> {
        return stateHandlerProviders.asSequence().mapNotNull { it.getStateHandler(key) }.first()
    }
}

class AStarPlanner(private val goapService: PlanningService, private val maxSearchDepth: Int = 50) : Planner {

    override fun plan(agent: Entity, goal: GOAPGoal): Plan? {
        val startState = goapService.createAgentState(agent)
        // Materialize once; `Sequence` may be one-shot and can't be safely iterated repeatedly.
        val allActions: List<Action> = goapService.getAllActions(agent).toList()

        // 如果目标已经满足，返回空计划
        if (goal.isSatisfied(startState, agent)) {
            return Plan(goal, emptyList(), 0.0)
        }

        // 使用优先队列管理开放列表
        val openSet = PriorityQueue<AStarNode>()
        val closedSet = mutableSetOf<String>()

        // 初始节点
        val startNode = AStarNode(
            worldState = startState,
            actions = emptyList(),
            cost = 0.0,
            heuristic = calculateHeuristic(startState, goal, agent)
        )
        openSet.add(startNode)

        // A*搜索主循环
        while (openSet.isNotEmpty()) {
            val current = openSet.poll()

            // 检查是否达到目标
            if (goal.isSatisfied(current.worldState, agent)) {
                return Plan(goal, current.actions, current.cost)
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
                if (!current.worldState.satisfiesConditions(action.preconditions)) continue
                // 应用动作效果，创建新状态
                val newState = current.worldState.mergeEffects(action.effects)
                val newCost = current.cost + action.cost
                val newHeuristic = calculateHeuristic(newState, goal, agent)
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

    private fun calculateHeuristic(state: WorldState, goal: GOAPGoal, agent: Entity): Double {
        if (goal.isSatisfied(state, agent)) return 0.0
        return goal.calculateHeuristic(state, agent)
    }

    private fun getStateHash(state: WorldState, agent: Entity): String {
        // 使用状态的键和值生成哈希
        val keys = state.stateKeys.sortedBy { it.hashCode() }
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
        val worldState: AgentState,
        val actions: List<Action>,
        val cost: Double,
        val heuristic: Double
    ) : Comparable<AStarNode> {
        val fCost: Double get() = cost + heuristic

        override fun compareTo(other: AStarNode): Int {
            return fCost.compareTo(other.fCost)
        }
    }
}