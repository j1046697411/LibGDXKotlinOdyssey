package cn.jzl.ecs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FamilyServiceTest : ECSBasicTest() {

    @Test
    fun testFamilyCreate() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        var count = 0
        family.onEntityInserted.add { count++ }
        world.create { it += Test1Component() }
        assertEquals(1, family.size)
        assertEquals(1, count)
    }

    @Test
    fun testFamilyWithMultipleComponents() {
        val world = createWorld()
        val family = world.family { all(Test1Component, Test2Component) }
        
        // 创建包含两个组件的实体
        val entity1 = world.create { 
            it += Test1Component()
            it += Test2Component()
        }
        
        // 创建只包含一个组件的实体
        val entity2 = world.create { 
            it += Test1Component()
        }
        
        assertEquals(1, family.size)
        assertTrue(entity1 in family)
        assertFalse(entity2 in family)
    }

    @Test
    fun testFamilyWithAnyComponents() {
        val world = createWorld()
        val family = world.family { any(Test1Component, Test2Component) }
        
        val entity1 = world.create { it += Test1Component() }
        val entity2 = world.create { it += Test2Component() }
        val entity3 = world.create { 
            it += Test1Component()
            it += Test2Component()
        }
        val entity4 = world.create { it += Test3Component() }
        
        assertEquals(3, family.size)
        assertTrue(entity1 in family)
        assertTrue(entity2 in family)
        assertTrue(entity3 in family)
        assertFalse(entity4 in family)
    }

    @Test
    fun testFamilyWithExcludeComponents() {
        val world = createWorld()
        val family = world.family { 
            all(Test1Component)
            none(Test2Component)
        }
        
        val entity1 = world.create { it += Test1Component() }
        val entity2 = world.create { 
            it += Test1Component()
            it += Test2Component()
        }
        val entity3 = world.create { it += Test2Component() }
        
        assertEquals(1, family.size)
        assertTrue(entity1 in family)
        assertFalse(entity2 in family)
        assertFalse(entity3 in family)
    }

    @Test
    fun testFamilyEntityInsertionEvents() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        
        var insertionCount = 0
        var removalCount = 0
        
        family.onEntityInserted.add { insertionCount++ }
        family.onEntityRemoved.add { removalCount++ }
        
        val entity = world.create { it += Test1Component() }
        assertEquals(1, insertionCount)
        assertEquals(0, removalCount)
        
        // 在configure上下文中移除组件
        world.configure(entity) { it -= Test1Component }
        assertEquals(1, insertionCount)
        assertEquals(1, removalCount)
        
        // 在configure上下文中重新添加组件
        world.configure(entity) { it += Test1Component() }
        assertEquals(2, insertionCount)
        assertEquals(1, removalCount)
    }

    @Test
    fun testFamilyEntityRemoval() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        
        val entity = world.create { it += Test1Component() }
        assertEquals(1, family.size)
        
        world.remove(entity)
        assertEquals(0, family.size)
        assertFalse(entity in family)
    }

    @Test
    fun testFamilyIteration() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        
        val entities = listOf(
            world.create { it += Test1Component() },
            world.create { it += Test1Component() },
            world.create { it += Test1Component() }
        )
        
        assertEquals(3, family.size)
        
        var iterationCount = 0
        family.entities.forEach { entity: Entity ->
            assertTrue(entity in entities)
            iterationCount++
        }
        
        assertEquals(3, iterationCount)
    }

    @Test
    fun testFamilyClear() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        
        val entity1 = world.create { it += Test1Component() }
        val entity2 = world.create { it += Test1Component() }
        
        assertEquals(2, family.size)
        
        // 逐个移除实体来模拟清空
        world.entityService.remove(entity1)
        world.entityService.remove(entity2)
        assertEquals(0, family.size)
    }

    @Test
    fun testFamilyComponentRemoval() {
        val world = createWorld()
        val family = world.family { all(Test1Component) }
        
        val entity = world.create { it += Test1Component() }
        assertEquals(1, family.size)
        
        // 在configure上下文中移除组件
        world.configure(entity) { it -= Test1Component }
        assertEquals(0, family.size)
        assertFalse(entity in family)
    }

    @Test
    fun testMultipleFamilies() {
        val world = createWorld()
        val family1 = world.family { all(Test1Component) }
        val family2 = world.family { all(Test2Component) }
        val family3 = world.family { all(Test1Component, Test2Component) }
        
        val entity = world.create { 
            it += Test1Component()
            it += Test2Component()
        }
        
        assertTrue(entity in family1)
        assertTrue(entity in family2)
        assertTrue(entity in family3)
        
        assertEquals(1, family1.size)
        assertEquals(1, family2.size)
        assertEquals(1, family3.size)
    }
}