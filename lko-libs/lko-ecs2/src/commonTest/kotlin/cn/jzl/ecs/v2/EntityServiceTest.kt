package cn.jzl.ecs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityServiceTest {

    //用于创建测试用的World
    fun createWorld(): World = world { }

    @Test
    fun `test create entity with configuration`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity += Test1Tag
        }
        
        assertTrue(entity in entityService)
        assertEquals(1, entityService.size)
        
        // 验证组件和标签已正确设置（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(entity.active)
            assertTrue(Test1Component in entity)
            assertTrue(Test1Tag in entity)
        }
    }

    @Test
    fun `test create entity with specific ID`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create(entityId = 5) { entity ->
            entity[Test1Component] = Test1Component()
        }
        
        assertEquals(5, entity.id)
        assertTrue(entity in entityService)
        assertEquals(1, entityService.size)
    }

    @Test
    fun `test create multiple entities`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity1 = entityService.create { }
        val entity2 = entityService.create { }
        val entity3 = entityService.create { }
        
        assertEquals(3, entityService.size)
        assertTrue(entity1 in entityService)
        assertTrue(entity2 in entityService)
        assertTrue(entity3 in entityService)
        
        // 验证实体ID是递增的
        assertEquals(0, entity1.id)
        assertEquals(1, entity2.id)
        assertEquals(2, entity3.id)
    }

    @Test
    fun `test configure existing entity`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { }
        
        // 配置现有实体并验证配置已应用
        entityService.configure(entity) { entity ->
            entity[Test1Component] = Test1Component()
            entity += Test1Tag
            
            // 验证配置已应用（在configure方法内部）
            assertTrue(entity.active)
            assertTrue(Test1Component in entity)
            assertTrue(Test1Tag in entity)
        }
    }

    @Test
    fun `test configure non-existent entity should throw exception`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val nonExistentEntity = Entity(999, 0)
        assertFailsWith<IllegalArgumentException> {
            entityService.configure(nonExistentEntity) { entity ->
                entity[Test1Component] = Test1Component()
            }
        }
    }

    @Test
    fun `test remove entity`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity += Test1Tag
        }
        
        assertTrue(entity in entityService)
        assertEquals(1, entityService.size)
        
        // 移除实体
        val result = entityService.remove(entity)
        
        assertTrue(result)
        assertFalse(entity in entityService)
        assertEquals(0, entityService.size)
    }

    @Test
    fun `test remove non-existent entity`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val nonExistentEntity = Entity(999, 0)
        
        val result = entityService.remove(nonExistentEntity)
        
        assertFalse(result)
        assertEquals(0, entityService.size)
    }

    @Test
    fun `test remove entity with components and tags`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity[Test2Component] = Test2Component()
            entity += Test1Tag
            entity += Test2Tag
        }
        
        // 验证实体有组件和标签（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(Test1Component in entity)
            assertTrue(Test2Component in entity)
            assertTrue(Test1Tag in entity)
            assertTrue(Test2Tag in entity)
        }
        
        // 移除实体
        entityService.remove(entity)
        
        // 验证实体已被移除
        assertFalse(entity in entityService)
        assertEquals(0, entityService.size)
        
        // 验证实体被移除后不能再调用configure
        assertFailsWith<IllegalArgumentException> {
            entityService.configure(entity) { entity ->
            }
        }
    }

    @Test
    fun `test entities sequence`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity1 = entityService.create { }
        val entity2 = entityService.create { }
        val entity3 = entityService.create { }
        
        val entities = entityService.entities.toList()
        
        assertEquals(3, entities.size)
        assertTrue(entities.contains(entity1))
        assertTrue(entities.contains(entity2))
        assertTrue(entities.contains(entity3))
    }

    @Test
    fun `test entities sequence after removal`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity1 = entityService.create { }
        val entity2 = entityService.create { }
        val entity3 = entityService.create { }
        
        // 移除中间实体
        entityService.remove(entity2)
        
        val entities = entityService.entities.toList()
        
        assertEquals(2, entities.size)
        assertTrue(entities.contains(entity1))
        assertFalse(entities.contains(entity2))
        assertTrue(entities.contains(entity3))
    }

    @Test
    fun `test entity with multiple components`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity[Test2Component] = Test2Component()
            entity[Test3Component] = Test3Component()
        }
        entityService.configure(entity) {
            // 验证所有组件都已设置
            assertTrue(Test1Component in entity)
            assertTrue(Test2Component in entity)
            assertTrue(Test3Component in entity)

            // 验证可以获取组件实例
            entity[Test1Component]
            entity[Test2Component]
            entity[Test3Component]
        }
    }

    @Test
    fun `test entity with multiple tags`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity += Test1Tag
            entity += Test2Tag
            entity += Test3Tag
        }
        
        // 验证所有标签都已设置（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(Test1Tag in entity)
            assertTrue(Test2Tag in entity)
            assertTrue(Test3Tag in entity)
        }
    }

    @Test
    fun `test entity component removal`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity += Test1Tag
        }
        
        // 验证组件和标签已设置（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(Test1Component in entity)
            assertTrue(Test1Tag in entity)
        }
        
        // 移除组件并验证已被移除（在configure方法内部）
        entityService.configure(entity) { entity ->
            entity -= Test1Component
            entity -= Test1Tag
            
            // 验证组件和标签已被移除
            assertFalse(Test1Component in entity)
            assertFalse(Test1Tag in entity)
        }
    }

    @Test
    fun `test getOrNull for non-existent component`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { }
        
        // 获取不存在的组件应该返回null（在configure方法内部）
        entityService.configure(entity) { entity ->
            val component = entity.getOrNull(Test1Component)
            assertEquals(null, component)
        }
    }

    @Test
    fun `test getOrPut for component`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { }
        
        // 使用getOrPut获取或创建组件（在configure方法内部）
        entityService.configure(entity) { entity ->
            val component = entity.getOrPut(Test1Component) { Test1Component() }
            assertTrue(Test1Component in entity)
            
            // 再次调用应该返回相同的实例
            val sameComponent = entity.getOrPut(Test1Component) { Test1Component() }
            assertEquals(component, sameComponent)
        }
    }

    @Test
    fun `test entity active status`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { }
        
        // 验证实体是活跃的（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(entity.active)
        }
        
        // 移除实体
        entityService.remove(entity)
        
        // 验证实体已被移除
        assertFalse(entity in entityService)
        assertEquals(0, entityService.size)
        
        // 验证实体被移除后不能再调用configure
        assertFailsWith<IllegalArgumentException> {
            entityService.configure(entity) { entity ->
            }
        }
    }

    @Test
    fun `test clear all entities`() {
        val world = createWorld()
        val entityService = world.entityService
        
        // 创建多个实体
        entityService.create { entity ->
            entity[Test1Component] = Test1Component()
            entity += Test1Tag
        }
        entityService.create { entity ->
            entity[Test2Component] = Test2Component()
            entity += Test2Tag
        }
        entityService.create { entity ->
            entity[Test3Component] = Test3Component()
            entity += Test3Tag
        }
        
        assertEquals(3, entityService.size)
        
        // 清空所有实体（通过移除每个实体）
        entityService.entities.toList().forEach { entityService.remove(it) }
        
        assertEquals(0, entityService.size)
        assertEquals(0, entityService.entities.count())
    }

    @Test
    fun `test entity creation with complex configuration`() {
        val world = createWorld()
        val entityService = world.entityService
        
        val entity = entityService.create { entity ->
            // 设置多个组件
            entity[Test1Component] = Test1Component()
            entity[Test2Component] = Test2Component()
            entity[Test3Component] = Test3Component()
            
            // 设置多个标签
            entity += Test1Tag
            entity += Test2Tag
            entity += Test3Tag
            
            // 使用tags方法设置标签
            entity.tags(Test1Tag, Test2Tag, Test3Tag)
        }
        
        assertEquals(1, entityService.size)
        assertTrue(entity in entityService)
        
        // 验证所有配置都已正确应用（在configure方法内部）
        entityService.configure(entity) { entity ->
            assertTrue(Test1Component in entity)
            assertTrue(Test2Component in entity)
            assertTrue(Test3Component in entity)
            assertTrue(Test1Tag in entity)
            assertTrue(Test2Tag in entity)
            assertTrue(Test3Tag in entity)
        }
    }
}