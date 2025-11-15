package cn.jzl.ecs.entity

import cn.jzl.ecs.World
import cn.jzl.ecs.world
import kotlin.test.Test

class RelationServiceTest {

    class TestComponent
    class TestTag

    fun createWorld(): World {
        return world { }
    }

    @Test
    fun testAddRelation() {
        val world = createWorld()
        world.componentService.configure<TestTag> { it.tag() }
        val entity = world.entityService.create { }
        repeat(10) {
            world.entityService.create {
                it.addComponent(TestComponent())
                it.addTag<TestTag>()
            }
        }
        println(world.entityStore.count())
    }

}