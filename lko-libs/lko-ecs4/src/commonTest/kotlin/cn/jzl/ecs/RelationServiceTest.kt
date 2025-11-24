package cn.jzl.ecs

import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.query
import kotlin.test.Test

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

        val entity = world.entity {
            it.addComponent(TestComponent(1.0f))
            it.addComponent(AnotherComponent("Test"))

            it.addComponent(AnotherComponent("Test2"))
        }

        world.entity(entity) {
            it.addComponent(TestComponent(2.0f))
        }

        world.query {
            object : QueriedEntity(this) {
                val test by component<TestComponent>()
                val another by component<AnotherComponent>()
            }
        }.forEach {
            println("test: ${it.test}, another: ${it.another}")
        }

        world.entity(entity) {
            it.removeComponent<AnotherComponent>()
        }
    }

}
