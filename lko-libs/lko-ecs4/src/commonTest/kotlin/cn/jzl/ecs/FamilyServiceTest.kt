package cn.jzl.ecs

import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.observers.exec
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.observers.observeWithData
import cn.jzl.ecs.query.shorthandQuery
import kotlin.test.Test
import kotlin.test.assertEquals

class FamilyServiceTest {

    data class TestComponent(val data: Float)
    data class TestComponent2(val data: Int)

    // 测试family方法创建Family实例
    @Test
    fun testFamilyCreation() {
        // 创建模拟的World和相关服务
        val world = world { }
        world.entity {
            it.addComponent(TestComponent(1.0f))
            it.addComponent(TestComponent2(1))
        }
        world.entity {
            it.addComponent(TestComponent(2.0f))
        }

        world.entity {
            it.addComponent(TestComponent2(2))
        }

        val query1 = world.shorthandQuery<TestComponent, TestComponent2>()
        val query2 = world.shorthandQuery<TestComponent> {
            not { component<TestComponent2>() }
        }
        val query3 = world.shorthandQuery<TestComponent2> {
            not { component<TestComponent>() }
        }
        val query4 = world.shorthandQuery<TestComponent>()
        val query5 = world.shorthandQuery<TestComponent2>()

        // 验证Family实例被正确创建
        assertEquals(query1.size, 1, "Family should contain 1 entity")
        assertEquals(query2.size, 1, "Family should contain 1 entity")
        assertEquals(query3.size, 1, "Family should contain 1 entity")
        assertEquals(query4.size, 2, "Family should contain 2 entities")
        assertEquals(query5.size, 2, "Family should contain 2 entities")
    }

    @Test
    fun testObserve() {
        data class TestEvent(val message: String)
        data class AnotherEvent(val value: Int)
    
        val world = world { }
        
        // 测试计数器，用于验证观察者是否被正确调用
        var observeCallCount = 0
        var observeWithDataCallCount = 0
        var entityObserveCallCount = 0
        
        // 创建不同类型的观察者
        world.observe<TestEvent>().filter().exec {
            observeCallCount++
            println("TestEvent observed (without data)")
        }
        
        world.observeWithData<TestEvent>().filter().exec {
            observeWithDataCallCount++
            println("TestEvent observed with data: ${event.message}")
        }
        
        world.observeWithData<AnotherEvent>().filter().exec {
            println("AnotherEvent observed with data: ${event.value}")
        }
        
        // 创建实体并设置实体特定的观察者
        val entity1 = world.entity {
            it.addComponent(TestComponent(1.0f))
        }
        println("entity1 $entity1")
        world.entityService.runOn(entity1) {
            println("entity1 $entityType")
        }

        val entity2 = world.entity { }
        val query = world.shorthandQuery<TestComponent>()
        query.forEach {
            println("entity1 ${it.entity} ${it.component1}")
        }

        world.observe(entity1) { yield(world.componentService.id<TestEvent>()) }.filter().exec {
            entityObserveCallCount++
            println("Entity-specific observer for entity1: $entity1")
        }

        world.observe<TestEvent>().filter().exec(query) {
            println("TestEvent observed (with data) for entity1: ${it.component1()}")
        }
        
        // 测试带数据的事件发射
        world.emit(entity1, TestEvent("Hello from entity1"))
        world.emit(entity2, TestEvent("Hello from entity2"))
        world.emit(entity1, AnotherEvent(42))
        
        // 验证带数据事件的观察者调用
        assertEquals(2, observeCallCount, "Generic TestEvent observer should be called twice for data events")
        assertEquals(2, observeWithDataCallCount, "Generic TestEvent observer with data should be called twice for data events")
        assertEquals(1, entityObserveCallCount, "Entity-specific observer should be called once for entity1 data event")
        
        // 测试不带数据的事件发射
        world.emit<TestEvent>(entity1)
        
        // 验证不带数据事件的观察者调用
        // observe<TestEvent>() 应该被触发（总次数3次）
        assertEquals(3, observeCallCount, "Generic TestEvent observer should be called three times (including no-data event)")
        // observeWithData<TestEvent>() 不应该被触发（保持2次）
        assertEquals(2, observeWithDataCallCount, "Generic TestEvent observer with data should NOT be called for no-data event")
        // 实体特定的观察者应该被触发（总次数2次）
        assertEquals(2, entityObserveCallCount, "Entity-specific observer should be called twice for entity1 (including no-data event)")
        
        println("Observe test completed successfully")
    }
}