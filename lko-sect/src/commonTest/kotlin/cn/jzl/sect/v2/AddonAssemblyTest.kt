package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.planning.PlanningService
import cn.jzl.sect.ecs.planning.planningAddon
import kotlin.test.Test
import kotlin.test.assertNotNull

class AddonAssemblyTest {
    @Test
    fun testPlanningAddonInstallsAndProvidesPlanningService() {
        val world = world {
            install(planningAddon)
        }

        // PlanningService is registered via planningAddon injects block
        val service = world.di.instance<PlanningService>()
        assertNotNull(service)
    }
}
