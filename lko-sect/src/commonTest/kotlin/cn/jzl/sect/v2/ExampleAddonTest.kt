package cn.jzl.sect.v2

import cn.jzl.di.instance
import cn.jzl.ecs.system.update
import cn.jzl.ecs.world
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ExampleAddonTest {

    @Test
    fun testExampleAddonWhenEnabledRegistersSystemThatTicksService() {
        val world = world {
            install(exampleAddon) { enabled = true }
        }

        val service by world.di.instance<ExampleService>()

        // Before any update, the service shouldn't have been ticked yet.
        assertEquals(0, service.tickCount())

        world.update(16.milliseconds)

        assertTrue(service.tickCount() >= 1)
    }

    @Test
    fun testExampleAddonWhenDisabledDoesNotTickService() {
        val world = world {
            install(exampleAddon) { enabled = false }
        }

        val service by world.di.instance<ExampleService>()

        assertEquals(0, service.tickCount())

        world.update(16.milliseconds)

        assertEquals(0, service.tickCount())
    }
}
