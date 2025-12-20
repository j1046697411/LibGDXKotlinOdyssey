package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.AddonSetup
import cn.jzl.ecs.addon.WorldSetup
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.system.Phase
import cn.jzl.ecs.system.system
import kotlin.time.Duration

/**
 * Minimal v2 example addon + system.
 *
 * Purpose:
 * - Acts as a reference implementation for the Constitution requirements (ECS-first + Addon + DI).
 * - Supports enable/disable via configuration.
 * - Demonstrates DI-managed system wiring (the addon doesn't `new` core services).
 */
data class ExampleConfig(
    var enabled: Boolean = true,
)

class ExampleService(@Suppress("UNUSED_PARAMETER") private val world: World) {
    private var ticks: Int = 0

    fun tick(@Suppress("UNUSED_PARAMETER") delta: Duration) {
        // Keep logic simple and deterministic. This is used by tests.
        ticks += 1
    }

    fun tickCount(): Int = ticks
}

private class EmptyContext(world: World) : cn.jzl.ecs.query.EntityQueryContext(world, true)

val exampleAddon = createAddon("v2-example", { ExampleConfig() }) {
    injects {
        // Service lifecycle managed by DI.
        this bind singleton { new(::ExampleService) }
    }

    systems {
        if (!configuration.enabled) return@systems

        val service by world.di.instance<ExampleService>()

        // ECS-first: behavior lives in a System.
        // We intentionally use execOnAll with an empty query context to avoid entity requirements.
        system(EmptyContext(world), name = "exampleSystem")
            .execOnAll { delta ->
                service.tick(delta)
            }
    }

    // Ensure system registration happens after addons are configured.
    on(Phase.ADDONS_CONFIGURED) {
        // no-op; documented phase hook
    }
}

/** Install helper for WorldSetup. */
@Suppress("unused")
fun WorldSetup.example(configure: ExampleConfig.() -> Unit = {}) = install(exampleAddon) { configure() }

/** Install helper for AddonSetup. */
@Suppress("unused")
fun AddonSetup<*>.example(configure: ExampleConfig.() -> Unit = {}) = install(exampleAddon) { configure() }
