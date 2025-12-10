package cn.jzl.sect.ecs.core

import cn.jzl.ecs.entity
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.core.coreAddon
import kotlin.test.*

class CoreTest {

    private val world by lazy {
        world {
            install(coreAddon)
        }
    }

    // 测试 Named 组件
    @Test
    fun testNamedComponent() {
        val named = Named("Test Entity")
        val entity = world.entity {
            it.addComponent(named)
        }

        world.entity(entity) {
            val entityNamed = it.getComponent<Named>()
            assertNotNull(entityNamed)
            assertEquals("Test Entity", entityNamed.name)
            assertEquals(named, entityNamed)
        }
    }

    // 测试 OwnedBy 关系
    @Test
    fun testOwnedByRelation() {
        val owner = world.entity {}
        val entity = world.entity {
            it.addRelation<OwnedBy>(owner)
        }

        world.entity(entity) {
            val entityOwner = it.getRelationUp<OwnedBy>()
            assertNotNull(entityOwner)
            assertEquals(owner, entityOwner)
        }
    }

    // 测试 OwnedBy 关系的唯一性
    @Test
    fun testOwnedByRelationUniqueness() {
        val owner1 = world.entity {}
        val owner2 = world.entity {}
        val entity = world.entity {
            it.addRelation<OwnedBy>(owner1)
        }

        // 再次添加不同的所有者关系，应该覆盖之前的关系
        world.entity(entity) {
            it.addRelation<OwnedBy>(owner2)
        }

        world.entity(entity) {
            val entityOwner = it.getRelationUp<OwnedBy>()
            assertNotNull(entityOwner)
            assertEquals(owner2, entityOwner)
        }
    }

    // 测试 Description 组件
    @Test
    fun testDescriptionComponent() {
        val description = Description("This is a test entity.")
        val entity = world.entity {
            it.addComponent(description)
        }

        world.entity(entity) {
            val entityDescription = it.getComponent<Description>()
            assertNotNull(entityDescription)
            assertEquals("This is a test entity.", entityDescription.description)
            assertEquals(description, entityDescription)
        }
    }

    // 测试多个核心组件的组合使用
    @Test
    fun testCoreComponentsCombination() {
        val owner = world.entity {}
        val entity = world.entity {
            it.addComponent(Named("Combined Entity"))
            it.addComponent(Description("An entity with multiple core components"))
            it.addRelation<OwnedBy>(owner)
        }

        world.entity(entity) {
            // 验证 Named 组件
            val named = it.getComponent<Named>()
            assertNotNull(named)
            assertEquals("Combined Entity", named.name)

            // 验证 Description 组件
            val description = it.getComponent<Description>()
            assertNotNull(description)
            assertEquals("An entity with multiple core components", description.description)

            // 验证 OwnedBy 关系
            val entityOwner = it.getRelationUp<OwnedBy>()
            assertNotNull(entityOwner)
            assertEquals(owner, entityOwner)
        }
    }

    // 测试移除 OwnedBy 关系
    @Test
    fun testRemoveOwnedByRelation() {
        val owner = world.entity {}
        val entity = world.entity {
            it.addRelation<OwnedBy>(owner)
        }

        // 移除 OwnedBy 关系
        world.entity(entity) {
            it.removeRelation<OwnedBy>(owner)
        }

        world.entity(entity) {
            val entityOwner = it.getRelationUp<OwnedBy>()
            assertNull(entityOwner)
        }
    }

    // 测试 Named 组件的更新
    @Test
    fun testUpdateNamedComponent() {
        val initialNamed = Named("Initial Name")
        val entity = world.entity {
            it.addComponent(initialNamed)
        }

        // 更新 Named 组件
        val updatedNamed = Named("Updated Name")
        world.entity(entity) {
            it.addComponent(updatedNamed)
        }

        world.entity(entity) {
            val entityNamed = it.getComponent<Named>()
            assertNotNull(entityNamed)
            assertEquals("Updated Name", entityNamed.name)
            assertEquals(updatedNamed, entityNamed)
            assertNotEquals(initialNamed, entityNamed)
        }
    }

    // 测试 Description 组件的更新
    @Test
    fun testUpdateDescriptionComponent() {
        val initialDescription = Description("Initial description")
        val entity = world.entity {
            it.addComponent(initialDescription)
        }

        // 更新 Description 组件
        val updatedDescription = Description("Updated description")
        world.entity(entity) {
            it.addComponent(updatedDescription)
        }

        world.entity(entity) {
            val entityDescription = it.getComponent<Description>()
            assertNotNull(entityDescription)
            assertEquals("Updated description", entityDescription.description)
            assertEquals(updatedDescription, entityDescription)
            assertNotEquals(initialDescription, entityDescription)
        }
    }
}