package cn.jzl.ecs

import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.forEach
import kotlin.test.Test
import kotlin.test.assertEquals

class RelationServiceTest {

    data class TestComponent(val value: Float)
    data class AnotherComponent(val name: String)

    // 测试关系添加和查询功能
    @Test
    fun testAddAndGetRelation() {
        val world = world { }
        world.observe<Components.OnInserted>().exec {
            println("OnInserted: $entity $involvedRelation")
        }
        world.observe<Components.OnRemoved>().exec {
            println("OnRemoved: $entity $involvedRelation")
        }
        world.observe<Components.OnUpdated>().exec {
            println("OnUpdated: $entity $involvedRelation")
        }
        val parent1 = world.entity {
            it.addComponent(TestComponent(1.0f))
            it.addComponent(AnotherComponent("Test"))

            it.addComponent(AnotherComponent("Test2"))
        }

        val parent2 = world.entity {
        }
        val child1 = world.childOf(parent1) {
        }
        val child2 = world.childOf(parent2) {
        }
        world.entity(parent1) {
            var count = 0
            it.children.forEach { count++ }
            assertEquals(1, count, message = "Parent1 should have 1 child")
        }
        world.entity(parent2) {
            var count = 0
            it.children.forEach { count++ }
            assertEquals(1, count, message = "Parent2 should have 1 child")
        }
        world.entity(child2) {
            it.parentOf(parent1)
        }

        world.entity(parent1) {
            var count = 0
            it.children.forEach { count++ }
            assertEquals(2, count, message = "Child2 should have parent1")
        }

        world.entity(parent2) {
            var count = 0
            it.children.forEach { count++ }
            assertEquals(0, count, message = "Parent2 should have no parent")
        }
    }

}
