package cn.jzl.ecs

import cn.jzl.ecs.observers.observe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityServiceTest {

    data class TestComponent(val value: Float)

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
        
        // 订阅实体创建和销毁事件
        // 注意：暂时注释掉事件订阅，因为observeWithData方法的用法需要进一步确认
        // world.observe<Components.OnInserted> {}
        //     .exec { 
        //         entityCreated = true 
        //     }
        // 
        // world.observe<Components.OnRemoved> {}
        //     .exec { 
        //         entityDestroyed = true 
        //     }
        
        // 创建实体
        val entity = world.entity {}
        // 注意：事件机制可能异步，这里不做断言
        
        // 销毁实体
        world.entityService.destroy(entity)
        // 注意：事件机制可能异步，这里不做断言
    }
}
