package cn.jzl.ecs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ComponentServiceTest {

    data class TestComponent(val value: Float)
    data class AnotherComponent(val name: String)

    // 测试组件类型管理
    @Test
    fun testComponentTypeManagement() {
        val world = world { }
        
        // 获取组件ID
        val testComponentId = world.componentService.id<TestComponent>()
        val anotherComponentId = world.componentService.id<AnotherComponent>()
        
        // 验证不同组件类型有不同的ID
        assertNotNull(testComponentId, "TestComponent ID should not be null")
        assertNotNull(anotherComponentId, "AnotherComponent ID should not be null")
        assertFalse(testComponentId == anotherComponentId, "Different component types should have different IDs")
        
        // 验证相同组件类型多次调用返回相同ID
        val testComponentId2 = world.componentService.id<TestComponent>()
        assertEquals(testComponentId, testComponentId2, "Same component type should return same ID")
    }
}
