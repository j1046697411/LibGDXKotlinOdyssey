package cn.jzl.ecs.observers

import cn.jzl.datastructure.BitSet
import cn.jzl.ecs.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserverServiceTest {

    data class TestEvent(val value: Int)
    data class AnotherEvent(val message: String)
    data class TestComponent(val value: Float)

    // 简单测试方法，用于调试基本功能
    @Test
    fun testSimpleEventFlow() {
        val world = world { }
        var eventHandled = false

        // 创建实体
        val entity = world.entity {}
        println("Created entity: $entity")

        // 手动创建事件组件并添加到实体
        val eventId = world.componentService.id<TestEvent>()
        println("Event component ID: $eventId")

        // 订阅事件
        val observer = world.observeWithData<TestEvent>(entity)
            .exec {
                println("Event handler called with event: ${event.value}")
                eventHandled = true
            }
        println("Created observer: $observer")

        // 打印observer的基本信息
        println("Observer object: $observer")
        println("Observer class: ${observer::class.simpleName}")

        // 发布事件
        println("About to emit event")
        world.emit(entity, TestEvent(42))
        println("Emitted event")

        // 再次验证
        println("Final eventHandled value: $eventHandled")

        // 验证事件是否被处理
        assertTrue(eventHandled, "Event should be handled")
    }

    // 测试基本事件订阅和处理
    @Test
    fun testBasicEventSubscription() {
        val world = world { }
        var eventHandled = false
        var eventValue = 0

        // 创建实体
        val entity = world.entity {}

        // 订阅TestEvent事件，指定目标实体
        val observer = world.observeWithData<TestEvent>(entity)
            .exec {
                eventHandled = true
                eventValue = event.value
            }

        // 发布TestEvent事件
        world.emit(entity, TestEvent(42))

        // 验证事件是否被处理
        println("Event handled: $eventHandled, Event value: $eventValue")
        assertTrue(eventHandled, "TestEvent should be handled")
        assertEquals(42, eventValue, "Event value should be 42")
    }

    // 测试事件取消订阅
    @Test
    fun testEventUnsubscription() {
        val world = world { }
        var eventHandledCount = 0

        // 创建实体
        val entity = world.entity {}

        // 订阅TestEvent事件，指定目标实体
        val observer = world.observeWithData<TestEvent>(entity)
            .exec {
                eventHandledCount++
            }

        // 第一次发布事件
        world.emit(entity, TestEvent(1))
        println("Event handled count after first emit: $eventHandledCount")
        assertEquals(1, eventHandledCount, "Event should be handled once")

        // 取消订阅
        observer.close()
        world.entityService.update()

        // 第二次发布事件
        world.emit(entity, TestEvent(2))
        println("Event handled count after second emit: $eventHandledCount")
        assertEquals(1, eventHandledCount, "Event should not be handled after unsubscription")
    }

    // 测试使用try-with-resources自动取消订阅
    @Test
    fun testAutoUnsubscriptionWithTryWithResources() {
        val world = world { }
        var eventHandledCount = 0

        // 创建实体
        val entity = world.entity {}

        // 使用try-with-resources创建观察者，指定目标实体
        val observer = world.observeWithData<TestEvent>(entity)
            .exec {
                eventHandledCount++
            }

        try {
            // 发布事件
            world.emit(entity, TestEvent(1))
            println("Event handled count in try block: $eventHandledCount")
            assertEquals(1, eventHandledCount, "Event should be handled")
        } finally {
            // 手动关闭观察者
            observer.close()
            world.entityService.update()
        }

        // 再次发布事件
        world.emit(entity, TestEvent(2))
        println("Event handled count after try-with-resources: $eventHandledCount")
        assertEquals(1, eventHandledCount, "Event should not be handled after try-with-resources block")
    }

    // 测试事件过滤
    @Test
    fun testEventFiltering() {
        val world = world { }
        var eventHandled = false

        // 创建实体
        val entityWithComponent = world.entity {
            it.addComponent(TestComponent(1.0f))
        }
        val entityWithoutComponent = world.entity {}

        // 订阅TestEvent事件，并指定只处理带有TestComponent的实体
        world.observeWithData<TestEvent>(entityWithComponent).involving<TestComponent>().exec {
            eventHandled = true
        }

        // 向没有TestComponent的实体发布事件
        world.emit(entityWithoutComponent, TestEvent(42))
        println("Event handled after emit to entity without component: $eventHandled")
        assertFalse(eventHandled, "Event should not be handled for entity without TestComponent")

        // 向有TestComponent的实体发布事件
        world.emit(entityWithComponent, TestEvent(42))
        println("Event handled after emit to entity with component: $eventHandled")
        assertTrue(eventHandled, "Event should be handled for entity with TestComponent")
    }

    // 测试事件数据传递
    @Test
    fun testEventDataPassing() {
        val world = world { }
        var receivedEvent: TestEvent? = null
        var receivedAnotherEvent: AnotherEvent? = null

        // 创建实体
        val entity = world.entity {}

        // 订阅TestEvent事件，指定目标实体
        world.observeWithData<TestEvent>(entity)
            .exec {
                receivedEvent = event
            }

        // 订阅AnotherEvent事件，指定目标实体
        world.observeWithData<AnotherEvent>(entity)
            .exec {
                receivedAnotherEvent = event
            }

        // 发布TestEvent事件
        val testEvent = TestEvent(123)
        world.emit(entity, testEvent)
        println("Received TestEvent: $receivedEvent")
        assertEquals(testEvent, receivedEvent, "TestEvent should be passed correctly")

        // 发布AnotherEvent事件
        val anotherEvent = AnotherEvent("Hello, World!")
        world.emit(entity, anotherEvent)
        println("Received AnotherEvent: $receivedAnotherEvent")
        assertEquals(anotherEvent, receivedAnotherEvent, "AnotherEvent should be passed correctly")
    }

    // 测试多个观察者订阅同一事件
    @Test
    fun testMultipleObservers() {
        val world = world { }
        var observer1Count = 0
        var observer2Count = 0

        // 创建实体
        val entity = world.entity {}

        // 第一个观察者，指定目标实体
        world.observeWithData<TestEvent>(entity).exec {
            observer1Count++
        }

        // 第二个观察者，指定目标实体
        world.observeWithData<TestEvent>(entity).exec {
            observer2Count++
        }

        // 发布事件
        world.emit(entity, TestEvent(42))

        // 验证两个观察者都收到了事件
        println("Observer1 count: $observer1Count, Observer2 count: $observer2Count")
        assertEquals(1, observer1Count, "First observer should handle event")
        assertEquals(1, observer2Count, "Second observer should handle event")
    }

    // 测试不同事件类型的订阅
    @Test
    fun testDifferentEventTypes() {
        val world = world { }
        var testEventHandled = false
        var anotherEventHandled = false

        // 创建实体
        val entity = world.entity {}

        // 订阅TestEvent事件，指定目标实体
        world.observeWithData<TestEvent>(entity).exec {
            testEventHandled = true
        }

        // 订阅AnotherEvent事件，指定目标实体
        world.observeWithData<AnotherEvent>(entity).exec {
            anotherEventHandled = true
        }

        // 发布TestEvent事件
        world.emit(entity, TestEvent(42))
        println("TestEvent handled: $testEventHandled, AnotherEvent handled: $anotherEventHandled")
        assertTrue(testEventHandled, "TestEvent should be handled")
        assertFalse(anotherEventHandled, "AnotherEvent should not be handled")

        // 重置标志
        testEventHandled = false
        anotherEventHandled = false

        // 发布AnotherEvent事件
        world.emit(entity, AnotherEvent("Test"))
        println("TestEvent handled: $testEventHandled, AnotherEvent handled: $anotherEventHandled")
        assertFalse(testEventHandled, "TestEvent should not be handled")
        assertTrue(anotherEventHandled, "AnotherEvent should be handled")
    }

    // 测试带有组件条件的事件订阅
    @Test
    fun testEventSubscriptionWithComponentCondition() {
        val world = world { }
        var eventHandled = false

        // 创建两个实体
        val entityWithComponent = world.entity {
            it.addComponent(TestComponent(1.0f))
        }
        val entityWithoutComponent = world.entity {}

        // 订阅TestEvent事件，并指定只处理带有TestComponent的实体
        world.observeWithData<TestEvent>(entityWithComponent)
            .involving<TestComponent>()
            .exec {
                eventHandled = true
            }

        // 向带有组件的实体发布事件
        world.emit(entityWithComponent, TestEvent(42))
        println("Event handled after emit to entity with component: $eventHandled")
        assertTrue(eventHandled, "Event should be handled for entity with TestComponent")

        // 重置标志
        eventHandled = false

        // 向不带有组件的实体发布事件
        world.emit(entityWithoutComponent, TestEvent(42))
        println("Event handled after emit to entity without component: $eventHandled")
        assertFalse(eventHandled, "Event should not be handled for entity without TestComponent")
    }

    @Test
    fun testEntityCreatedEvent() {
        val world = world { }
        var count = 0

        // 订阅OnEntityCreated事件，指定目标实体
        world.observe<Components.OnEntityCreated>().exec {
            count++
        }

        // 创建实体
        world.entity { }
        world.entity { }

        // 验证事件是否被处理
        assertEquals(2, count, "OnEntityCreated event should be handled for entity creation")
    }

    @Test
    fun testEntityCreatedEventWithChild() {
        val world = world { }
        var count = 0

        // 订阅OnEntityCreated事件，指定目标实体
        world.observe<Components.OnEntityCreated>().exec {
            count++
        }

        // 创建实体
        val parent = world.entity { }
        world.childOf(parent) {}

        // 验证事件是否被处理
        assertEquals(2, count, "OnEntityCreated event should be handled for entity creation")
    }

    @Test
    fun testEntityUpdatedEvent() {
        val world = world { }
        var count = 0

        // 订阅OnEntityUpdated事件，指定目标实体
        world.observe<Components.OnEntityUpdated>().exec {
            count++
        }

        // 创建实体
        val entity = world.entity { }

        // 更新实体
        world.entity(entity) {
            it.addComponent(TestComponent(1.0f))
        }

        world.entity(entity) {
            it.addComponent(ComponentServiceTest.AnotherComponent("Test"))
        }

        // 验证事件是否被处理
        assertEquals(2, count, "OnEntityUpdated event should be handled for entity update")
    }
}
