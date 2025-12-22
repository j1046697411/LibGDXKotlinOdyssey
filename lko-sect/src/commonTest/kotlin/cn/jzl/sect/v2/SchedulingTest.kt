package cn.jzl.sect.v2

import cn.jzl.ecs.WorldOwner
import cn.jzl.ecs.system.Phase
import kotlin.test.Test
import kotlin.test.assertEquals

class SchedulingTest {

    @Test
    fun testPipelineRunsStartupTasksInPhaseOrder() {
        val calls = mutableListOf<String>()

        cn.jzl.ecs.world {
            on(Phase.INIT_COMPONENTS) { calls += "init_components" }
            on(Phase.INIT_SYSTEMS) { calls += "init_systems" }
            on(Phase.INIT_ENTITIES) { calls += "init_entities" }
            on(Phase.ENABLE) { calls += "enable" }
        }

        // Startup tasks are executed as part of world construction. Verify deterministic order.
        assertEquals(
            listOf("init_components", "init_systems", "init_entities", "enable"),
            calls
        )
    }
}

private fun cn.jzl.ecs.addon.WorldSetup.on(phase: Phase, block: WorldOwner.() -> Unit) {
    phaseTaskRegistry("scheduling-test", phase, block)
}
