package cn.jzl.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SystemTest {
    // 测试用的UpdateSystem
    private class TestUpdateSystem(world: World) : UpdateSystem(world) {
        var updateCount = 0
        override fun update(deltaTime: Duration) {
            updateCount++
        }
    }

    // 测试用的PositionSystem
    private class PositionSystem(world: World) : IteratingSystem(world, configuration = {}) {
        val processedEntities = mutableSetOf<Entity>()

        override fun onTickEntity(entity: Entity, deltaTime: Duration) {
            // 确保实体有PositionComponent才处理
            if (PositionComponent in entity) {
                processedEntities.add(entity)
            }
        }
    }

    // 测试用的FixedIntervalSystem
    private class FixedIntervalSystem(world: World) : IntervalSystem(world, Fixed(1.seconds)) {
        var updateCount = 0
        override fun onTick(deltaTime: Duration) {
            updateCount++
        }
    }

    // 测试用的EachFrameSystem
    private class EachFrameSystem(world: World) : IntervalSystem(world, EachFrame) {
        var updateCount = 0
        override fun onTick(deltaTime: Duration) {
            updateCount++
        }
    }

    // 测试用的CallbackSystem
    private class CallbackSystem(world: World) : IteratingSystem(world, configuration = {}) {
        var beforeTickCalled = false
        var afterTickCalled = false
        val processedEntities = mutableListOf<Entity>()
    
        override fun onBeforeTick(deltaTime: Duration) {
            beforeTickCalled = true
        }
    
        override fun onAfterTick(deltaTime: Duration) {
            afterTickCalled = true
        }
    
        override fun onTickEntity(entity: Entity, deltaTime: Duration) {
            // 确保实体有PositionComponent才处理
            if (PositionComponent in entity) {
                processedEntities.add(entity)
            }
        }
    }

    // 测试用的SystemA
    private class SystemA(world: World) : UpdateSystem(world), Prioritized {
        lateinit var executionOrder: MutableList<String>
        override val priority: Int = 10
        override fun update(deltaTime: Duration) {
            if (::executionOrder.isInitialized) {
                executionOrder.add("A")
            }
        }
    }

    // 测试用的SystemB
    private class SystemB(world: World) : UpdateSystem(world), Prioritized {
        lateinit var executionOrder: MutableList<String>
        override val priority: Int = 100
        override fun update(deltaTime: Duration) {
            if (::executionOrder.isInitialized) {
                executionOrder.add("B")
            }
        }
    }

    // 测试用的SystemC
    private class SystemC(world: World) : UpdateSystem(world) {
        lateinit var executionOrder: MutableList<String>
        override fun update(deltaTime: Duration) {
            if (::executionOrder.isInitialized) {
                executionOrder.add("C")
            }
        }
    }

    @Test
    fun `test update system creation and execution`() {
        // 注册系统
        val world = world { this bind singleton { new(::TestUpdateSystem) } }
        val testSystem by world.instance<TestUpdateSystem>()

        // 调用world.update应该执行系统更新
        world.update(1.seconds)
        assertEquals(1, testSystem.updateCount)

        world.update(1.seconds)
        assertEquals(2, testSystem.updateCount)
    }

    @Test
    fun `test iterating system with family`() {
        val positionComponentType = PositionComponent

        // 注册系统
        val world = world {
            this bind singleton { new(::PositionSystem) }
        }
        val positionSystem by world.instance<PositionSystem>()

        // 创建一些实体
        val entity1 = world.create { it[positionComponentType] = PositionComponent(10f, 20f) }
        val entity2 = world.create { it[positionComponentType] = PositionComponent(30f, 40f) }
        val entity3 = world.create() // 没有位置组件

        // 更新世界
        world.update(0.seconds)

        // 系统应该只处理有位置组件的实体
        assertEquals(2, positionSystem.processedEntities.size)
        assertTrue(entity1 in positionSystem.processedEntities)
        assertTrue(entity2 in positionSystem.processedEntities)
        assertFalse(entity3 in positionSystem.processedEntities)
    }

    @Test
    fun `test interval system with fixed interval`() {
        // 注册系统
        val world = world { this bind singleton { new(::FixedIntervalSystem) } }
        val intervalSystem by world.instance<FixedIntervalSystem>()
        
        // 第一次更新应该不触发（时间不够）
        world.update(0.5.seconds)
        assertEquals(0, intervalSystem.updateCount)

        // 第二次更新累计时间应该触发
        world.update(0.6.seconds) // 总共1.1秒
        assertEquals(1, intervalSystem.updateCount)

        // 再次更新应该不触发
        world.update(0.5.seconds)
        assertEquals(1, intervalSystem.updateCount)

        // 再更新应该触发第二次
        world.update(0.6.seconds) // 再累计1.1秒
        assertEquals(2, intervalSystem.updateCount)
    }

    @Test
    fun `test interval system with each frame`() {
        // 注册系统
        val world = world {
            this bind singleton { new(::EachFrameSystem) }
        }
        val frameSystem by world.instance<EachFrameSystem>()
        
        // 每次更新都应该触发
        world.update(0.1.seconds)
        assertEquals(1, frameSystem.updateCount)

        world.update(0.1.seconds)
        assertEquals(2, frameSystem.updateCount)

        world.update(0.1.seconds)
        assertEquals(3, frameSystem.updateCount)
    }

    @Test
    fun `test system callbacks`() {
        val positionComponentType = PositionComponent

        // 注册系统
        val world = world {
            this bind singleton { new(::CallbackSystem) }
        }
        val callbackSystem by world.instance<CallbackSystem>()
        
        // 创建实体
        val entity = world.create { it[positionComponentType] = PositionComponent(10f, 20f) }

        // 更新世界
        world.update(0.seconds)

        // 验证回调都被调用
        assertTrue(callbackSystem.beforeTickCalled)
        assertTrue(callbackSystem.afterTickCalled)
        assertTrue(entity in callbackSystem.processedEntities)
    }

    @Test
    fun `test system service sorting`() {
        val executionOrder = mutableListOf<String>()
    
        // 注册系统（故意按错误的顺序注册）
        val world = world {
            this bind singleton { new(::SystemC) }
            this bind singleton { new(::SystemA) }
            this bind singleton { new(::SystemB) }
        }
        
        // 设置executionOrder属性
        val systemC by world.instance<SystemC>()
        systemC.executionOrder = executionOrder
        
        val systemA by world.instance<SystemA>()
        systemA.executionOrder = executionOrder
        
        val systemB by world.instance<SystemB>()
        systemB.executionOrder = executionOrder
        
        // 更新世界
        world.update(0.seconds)
    
        // 验证系统按优先级排序执行
        assertEquals(listOf("B", "A", "C"), executionOrder)
    }
}