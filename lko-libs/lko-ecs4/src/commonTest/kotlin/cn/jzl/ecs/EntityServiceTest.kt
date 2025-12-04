package cn.jzl.ecs

import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.query.query
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EntityServiceTest {

    data class TestComponent(val value: Float)
    data class TestComponent2(val value: Int)

    // 测试实体创建与销毁
    @Test
    fun testEntityCreationAndDestruction() {
        val world = world { }

        // 创建实体
        val entity = world.entity {}
        assertTrue(entity != Entity.ENTITY_INVALID, "Entity should be valid after creation")

        // 销毁实体
        world.entityService.destroy(entity)
        // 注意：实体销毁后，实体对象本身不会改变，需要通过其他方式验证
    }

    // 测试实体版本管理
    @Test
    fun testEntityVersionManagement() {
        val world = world { }

        // 创建并销毁第一个实体
        val entity1 = world.entity {}
        val entity1Id = entity1.id
        val entity1Version = entity1.version
        world.entityService.destroy(entity1)

        // 创建第二个实体，应该重用ID但版本不同
        val entity2 = world.entity {}
        assertEquals(entity1Id, entity2.id, "Entity ID should be reused")
        assertTrue(entity2.version > entity1Version, "Entity version should be incremented")
    }

    // 测试实体批量创建
    @Test
    fun testEntityBatchCreation() {
        val world = world { }

        val entities = mutableListOf<Entity>()

        // 批量创建100个实体
        for (i in 0 until 100) {
            val entity = world.entity {
                it.addComponent(TestComponent(i.toFloat()))
            }
            entities.add(entity)
        }

        assertEquals(100, entities.size, "Should create 100 entities")

        // 验证所有实体都有效
        entities.forEach { entity ->
            assertTrue(entity != Entity.ENTITY_INVALID, "Entity should be valid")
        }
    }

    // 测试实体ID回收
    @Test
    fun testEntityIdRecycling() {
        val world = world { }

        val destroyedEntityIds = mutableSetOf<Int>()

        // 创建并销毁10个实体
        for (i in 0 until 10) {
            val entity = world.entity {}
            destroyedEntityIds.add(entity.id)
            world.destroy(entity)
        }

        val reusedIds = mutableSetOf<Int>()

        // 再创建10个实体，检查ID是否被回收
        for (i in 0 until 10) {
            val entity = world.entity {}
            if (destroyedEntityIds.contains(entity.id)) {
                reusedIds.add(entity.id)
            }
        }

        assertTrue(reusedIds.isNotEmpty(), "Some entity IDs should be recycled")
    }

    // 测试实体生命周期管理
    @Test
    fun testEntityLifecycleManagement() {
        val world = world { }
        var entityCreated = false
        var entityDestroyed = false

//         订阅实体创建和销毁事件
//         注意：暂时注释掉事件订阅，因为observeWithData方法的用法需要进一步确认
        world.observe<Components.OnEntityCreated>().exec {
            entityCreated = true
        }

        world.observe<Components.OnEntityDestroyed>().exec {
            entityDestroyed = true
        }

        // 创建实体
        val entity = world.entity {}
        // 注意：事件机制可能异步，这里不做断言

        // 销毁实体
        world.destroy(entity)
        // 注意：事件机制可能异步，这里不做断言
        assertTrue(entityCreated, "Entity should be marked as created")
        assertTrue(entityDestroyed, "Entity should be marked as destroyed")
    }

    @Test
    fun test() {
        val world = world { }
        var count = 0
        world.observe<Components.OnEntityDestroyed>().exec {
            count++
        }
        val entity = world.entity { }
        world.childOf(entity) {}
        world.destroy(entity)
        assertEquals(2, count, "Entity should be destroyed")
    }

    @Test
    fun testInstanceOf() {
        class Settings(val x: Int)

        val world = world { }

        val entity = world.entity {
            it.addComponent(TestComponent(0f))
            it.addSharedComponent(Settings(0))
        }

        world.instanceOf(entity) {
            it.addComponent(TestComponent2(0))
        }
        world.instanceOf(entity) {
            it.addComponent(TestComponent2(1))
        }

        val query = world.query {
            object : EntityQueryContext(this, false) {
                val testComponent by component<TestComponent>()
                val testComponent2 by component<TestComponent2?>()
                val settings by sharedComponent<Settings>()
            }
        }
        assertEquals(query.size, 2, "Should query 1 entity")
        query.forEach {
            println("$entity testComponent $testComponent, testComponent2 $testComponent2, settings $settings")
            assertEquals(testComponent.value, 0f, "TestComponent value should be 0f")
            assertEquals(settings.x, 0, "Settings value should be 0")
        }
    }

}
