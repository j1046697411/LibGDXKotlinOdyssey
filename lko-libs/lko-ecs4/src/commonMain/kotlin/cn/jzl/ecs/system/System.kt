package cn.jzl.ecs.system

import cn.jzl.ecs.World
import cn.jzl.ecs.WorldOwner
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.forEach
import kotlin.time.Duration

enum class Phase {
    ADDONS_CONFIGURED,
    INIT_COMPONENTS,
    INIT_SYSTEMS,
    INIT_ENTITIES,
    ENABLE
}

interface Pipeline {
    fun runOnOrAfter(phase: Phase, block: WorldOwner.() -> Unit)

    fun onSystemAdd(run: (System<*>) -> Unit)

    fun runStartupTasks()

    fun <C : EntityQueryContext> addSystem(system: System<C>): TrackedSystem<C>

    fun getRepeatingInExecutionOrder(): Sequence<TrackedSystem<*>>
}

class PipelineImpl(override val world: World) : Pipeline, WorldOwner {

    private val onSystemAdd = mutableListOf<(System<*>) -> Unit>()
    private val trackedSystems = mutableListOf<TrackedSystem<*>>()
    private val scheduled = Array(Phase.entries.size) {
        mutableListOf<WorldOwner.() -> Unit>()
    }
    private var currentPhase = Phase.entries.first()

    override fun runOnOrAfter(phase: Phase, block: WorldOwner.() -> Unit) {
        if (currentPhase >= phase) {
            block()
            return
        }
        scheduled[phase.ordinal].add(block)
    }

    override fun onSystemAdd(run: (System<*>) -> Unit) {
        onSystemAdd.add(run)
    }

    override fun runStartupTasks() {
        Phase.entries.forEach { phase ->
            currentPhase = phase
            val systems = scheduled[phase.ordinal]
            if (systems.isEmpty()) return@forEach
            systems.forEach { it() }
        }
    }

    override fun <C : EntityQueryContext> addSystem(system: System<C>): TrackedSystem<C> {
        onSystemAdd.forEach { it(system) }
        val query = world.queryService.query { system.context }
        val trackedSystem = TrackedSystem(system, query)
        trackedSystems.add(trackedSystem)
        return trackedSystem
    }

    override fun getRepeatingInExecutionOrder(): Sequence<TrackedSystem<*>> = trackedSystems.asSequence()
}

data class System<C : EntityQueryContext>(
    val name: String,
    val context: C,
    val onTick: Query<C>.() -> Unit,
    val interval: Duration? = null
)

data class TrackedSystem<C : EntityQueryContext>(val system: System<C>, val query: Query<C>) {
    fun tick(): Unit = system.run { query.onTick() }
}

data class SystemBuilder<C : EntityQueryContext>(
    val pipeline: Pipeline,
    val context: C,
    val name: String,
    val interval: Duration? = null,
) {

    fun named(name: String): SystemBuilder<C> = copy(name = name)

    fun every(interval: Duration): SystemBuilder<C> = copy(interval = interval)

    inline fun exec(crossinline onTick: C.() -> Unit): TrackedSystem<C> = pipeline.addSystem(System(name, context, { forEach(onTick) }, interval))

    fun execOnAll(onTick: Query<C>.() -> Unit): TrackedSystem<C> = pipeline.addSystem(System(name, context, onTick, interval))
}