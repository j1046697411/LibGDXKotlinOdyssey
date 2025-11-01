package cn.jzl.ecs.v2

import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class FamilyServiceTest : ECSBasicTest() {

    @Test
    fun testFamilyCreate() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyCreate") {
            var count = 0
            val family = family { all(Test1Component) }
            family.onEntityInserted.add { count++ }
            val test1Component = Test1Component.write
            create { it[test1Component] = Test1Component() }
            assertEquals(1, family.size)
            assertEquals(1, count)
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyWithAllComponents() {
        val world = createWorld()
        // 创建一个要求同时拥有两个组件的家族
        val schedule = world.schedule("testFamilyWithAllComponents") {
            val family = family { all(Test1Component) }
            val test1Component = Test1Component.write
            
            // 创建只有一个组件的实体
            val entity1 = create { 
                it[test1Component] = Test1Component() 
            }
            
            // 验证实体被正确添加到家族
            assertTrue(entity1 in family, "entity with all required components should be in family")
            assertEquals(1, family.size, "family size should be 1")
            assertTrue(family.entities.toList().contains(entity1), "family entities should contain entity1")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyWithNoneComponents() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyWithNoneComponents") {
            val test1Component = Test1Component.write
            // 创建一个要求没有指定组件的家族
            val family = family { none(Test1Component) }
            
            // 创建没有指定组件的实体
            val entity1 = create {}
            // 创建有指定组件的实体
            val entity2 = create { 
                it[test1Component] = Test1Component() 
            }
            
            // 验证实体被正确过滤
            assertTrue(entity1 in family, "entity without excluded component should be in family")
            assertFalse(entity2 in family, "entity with excluded component should not be in family")
            assertEquals(1, family.size, "family size should be 1")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyWithComplexConditions() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyWithComplexConditions") {
            // 创建一个复杂条件的家族：同时拥有Test1Component，并且没有Test1Tag
            val family = family { 
                all(Test1Component)
                none(Test1Tag)
            }
            val test1Component = Test1Component.write
            val test1Tag = Test1Tag.write
            
            // 创建符合条件的实体
            val entity1 = create { 
                it[test1Component] = Test1Component() 
            }
            // 创建不符合条件的实体
            val entity2 = create { 
                it[test1Component] = Test1Component() 
                it += test1Tag
            }
            
            // 验证实体被正确过滤
            assertTrue(entity1 in family, "entity meeting all conditions should be in family")
            assertFalse(entity2 in family, "entity not meeting all conditions should not be in family")
            assertEquals(1, family.size, "family size should be 1")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyEntityRemovedSignal() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyEntityRemovedSignal") {
            val family = family { all(Test1Component) }
            val insertedEntities = mutableListOf<Entity>()
            val removedEntities = mutableListOf<Entity>()
            
            // 注册信号监听器
            family.onEntityInserted.add { insertedEntities.add(it) }
            family.onEntityRemoved.add { removedEntities.add(it) }
            
            val test1Component = Test1Component.write
            val entity = create { 
                it[test1Component] = Test1Component() 
            }
            
            // 验证插入信号
            assertEquals(1, insertedEntities.size, "insertedEntities size should be 1")
            assertEquals(entity, insertedEntities[0], "inserted entity should match created entity")
            
            // 移除组件，使实体不再满足家族条件
            configure(entity) { 
                it -= test1Component 
            }
            
            // 验证移除信号
            assertEquals(1, removedEntities.size, "removedEntities size should be 1")
            assertEquals(entity, removedEntities[0], "removed entity should match created entity")
            assertEquals(0, family.size, "family size should be 0 after component removal")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyEntityDestroySignal() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyEntityDestroySignal") {
            val family = family { all(Test1Component) }
            val insertedEntities = mutableListOf<Entity>()
            val removedEntities = mutableListOf<Entity>()
            
            // 注册信号监听器
            family.onEntityInserted.add { insertedEntities.add(it) }
            family.onEntityRemoved.add { removedEntities.add(it) }
            
            val test1Component = Test1Component.write
            val entity = create { 
                it[test1Component] = Test1Component() 
            }
            
            // 验证插入信号
            assertEquals(1, insertedEntities.size, "insertedEntities size should be 1")
            
            // 销毁实体
            world.remove(entity)
            
            // 验证移除信号
            assertEquals(1, removedEntities.size, "removedEntities size should be 1")
            assertEquals(entity, removedEntities[0], "removed entity should match destroyed entity")
            assertEquals(0, family.size, "family size should be 0 after entity destruction")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyReuseWithSameDefinition() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyReuseWithSameDefinition") {
            // 创建两个具有相同定义的家族
            val family1 = family { all(Test1Component) }
            val family2 = family { all(Test1Component) }
            
            // 验证是否重用了相同的家族实例
            assertSame(family1, family2, "families with same definition should be reused")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyEntitiesSequence() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyEntitiesSequence") {
            val family = family { all(Test1Component) }
            val test1Component = Test1Component.write
            val entities = mutableListOf<Entity>()
            
            // 创建多个实体
            for (i in 1..3) {
                val entity = create { 
                    it[test1Component] = Test1Component() 
                }
                entities.add(entity)
            }
            
            // 验证家族实体序列
            val familyEntities = family.entities.toList()
            assertEquals(3, familyEntities.size, "family entities sequence size should be 3")
            entities.forEach { 
                assertTrue(familyEntities.contains(it), "family entities should contain created entity")
            }
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyComponentDynamicChanges() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyComponentDynamicChanges") {
            val family = family { all(Test1Component) }
            val test1Component = Test1Component.write
            
            // 创建一个没有组件的实体
            val entity = create {}
            
            // 初始状态：实体不在家族中
            assertFalse(entity in family, "entity should not be in family initially")
            assertEquals(0, family.size, "family size should be 0 initially")
            
            // 添加组件，使实体满足家族条件
            configure(entity) { 
                it[test1Component] = Test1Component() 
            }
            assertTrue(entity in family, "entity should be in family after adding component")
            assertEquals(1, family.size, "family size should be 1 after adding component")
            
            // 移除组件，使实体不再满足家族条件
            configure(entity) { 
                it -= test1Component 
            }
            assertFalse(entity in family, "entity should not be in family after removing component")
            assertEquals(0, family.size, "family size should be 0 after removing component")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }

    @Test
    fun testFamilyServiceAsSequence() {
        val world = createWorld()
        val schedule = world.schedule("testFamilyServiceAsSequence") {
            // 创建多个不同定义的家族
            val family1 = family { all(Test1Component) }
            val family2 = family {}
            
            // 验证FamilyService作为序列的功能
            val families = world.familyService.toList()
            // 确保能找到对应的家族定义
            assertTrue(families.contains(family1), "family service should contain family1")
            assertTrue(families.contains(family2), "family service should contain family2")
            // 验证家族总数
            assertEquals(2, families.size, "family service should contain exactly 2 families")
        }
        while (world.isActive(schedule)) world.update(0.seconds)
    }
}