package cn.jzl.ecs.v2

import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class EntityServiceTest {

    //用于创建测试用的World
    fun createWorld(): World = world { }

    @Test
    fun `test create entity with configuration`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val testTag = Test1Tag.write
            val entity = create {
                it[test1Component] = Test1Component()
                it += testTag
            }
            configure(entity) {
                assertTrue(it.active, "entity is not active")
                assertTrue(test1Component in it, "component should be present")
                assertTrue(testTag in it, "tag should be present")
            }
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test create entity with specific id`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            // 使用特定ID创建实体
            val entity = create(123) {
                it[test1Component] = Test1Component()
            }
            configure(entity) {
                assertEquals(123, entity.id, "entity id should be 123")
                assertTrue(it.active, "entity should be active")
            }
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test get entity by id`() {
        val world = createWorld()
        val entityId = 456
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val entity = create(entityId) {
                it[test1Component] = Test1Component()
            }
            // 测试通过ID获取实体
            val retrievedEntity = world.entityService[entityId]
            assertEquals(entity, retrievedEntity, "retrieved entity should match created entity")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test contains entity`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val entity = create {
                it[test1Component] = Test1Component()
            }
            // 测试实体是否在服务中
            assertTrue(entity in world.entityService, "entity should be in entityService")
            // 创建一个不存在的实体ID
            val nonExistentEntity = Entity(9999)
            assertFalse(nonExistentEntity in world.entityService, "non-existent entity should not be in entityService")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test remove entity`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val testTag = Test1Tag.write
            val entity = create {
                it[test1Component] = Test1Component()
                it += testTag
            }
            // 确认实体存在
            assertTrue(entity in world.entityService, "entity should exist before removal")
            // 测试移除实体
            val removed = world.entityService.remove(entity)
            assertTrue(removed, "entity should be successfully removed")
            // 确认实体已不存在
            assertFalse(entity in world.entityService, "entity should not exist after removal")
            // 测试移除不存在的实体
            val nonExistentEntity = Entity(9999)
            assertFalse(world.entityService.remove(nonExistentEntity), "removing non-existent entity should return false")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test entity count and entities sequence`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            // 创建多个实体
            val entity1 = create {
                it[test1Component] = Test1Component()
            }
            val entity2 = create {
                it[test1Component] = Test1Component()
            }
            // 测试实体数量
            assertEquals(2, world.entityService.size, "entityService size should be 2")
            // 测试实体序列
            val entities = world.entityService.entities.toList()
            assertEquals(2, entities.size, "entities sequence size should be 2")
            assertTrue(entities.contains(entity1), "entities sequence should contain entity1")
            assertTrue(entities.contains(entity2), "entities sequence should contain entity2")
            // 移除一个实体后再测试
            world.entityService.remove(entity1)
            assertEquals(1, world.entityService.size, "entityService size should be 1 after removal")
            assertEquals(1, world.entityService.entities.toList().size, "entities sequence size should be 1 after removal")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test configure entity throws exception for non-existent entity`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            // 创建一个不存在的实体
            val nonExistentEntity = Entity(9999)
            // 测试配置不存在的实体时抛出异常
            assertFailsWith<IllegalArgumentException> {
                world.entityService.configure(nonExistentEntity) {}
            }
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun `test entity signals`() {
        val world = createWorld()
        val createdEntities = mutableListOf<Entity>()
        val updatedEntities = mutableListOf<Entity>()
        val destroyedEntities = mutableListOf<Entity>()

        // 注册信号监听器
        world.entityService.onEntityCreate.add { createdEntities.add(it) }
        world.entityService.onEntityUpdate.add { updatedEntities.add(it) }
        world.entityService.onEntityDestroy.add { destroyedEntities.add(it) }

        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val entity = create {
                it[test1Component] = Test1Component()
            }
            // 配置实体应该触发update信号
            configure(entity) {}
            // 移除实体应该触发destroy信号
            world.entityService.remove(entity)
        }
        while (world.isActive(schedule)) world.update(0.seconds)

        // 验证信号触发
        assertEquals(1, createdEntities.size, "create signal should be triggered once")
        assertEquals(1, updatedEntities.size, "update signal should be triggered once")
        assertEquals(1, destroyedEntities.size, "destroy signal should be triggered once")
        // 确认是同一个实体
        assertTrue(createdEntities[0] == updatedEntities[0], "created and updated entities should be the same")
        assertTrue(createdEntities[0] == destroyedEntities[0], "created and destroyed entities should be the same")
    }

    @Test
    fun `test component removal via update context`() {
        val world = createWorld()
        val schedule = world.schedule {
            val test1Component = Test1Component.write
            val testTag = Test1Tag.write
            val entity = create {
                it[test1Component] = Test1Component()
                it += testTag
            }
            // 验证组件和标签都存在
            configure(entity) {
                assertTrue(test1Component in it, "component should be present")
                assertTrue(testTag in it, "tag should be present")
            }
            // 移除组件和标签
            configure(entity) {
                it -= test1Component
                it -= testTag
            }
            // 验证组件和标签都已移除
            configure(entity) {
                assertFalse(test1Component in it, "component should be removed")
                assertFalse(testTag in it, "tag should be removed")
            }
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }
}