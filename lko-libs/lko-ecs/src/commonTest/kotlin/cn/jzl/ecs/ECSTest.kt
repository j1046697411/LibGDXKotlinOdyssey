package cn.jzl.ecs

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.util.bitsOf
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ECSTest {

    @Test
    fun `test entity creation and deletion`() {
        val world = world {}
        val entity = world.create()

        assertTrue(world.entitySize == 1)
        assertTrue(entity in world.entityService)

        world.delete(entity)
        assertTrue(world.entitySize == 0)
        assertFalse(entity in world.entityService)
    }

    @Test
    fun `test entity configuration with components`() {
        val world = world {}
        val positionComponentType = PositionComponent

        val entity = world.create {
            it[positionComponentType] = PositionComponent(10f, 20f)
            it[VelocityComponent] = VelocityComponent(5f, 0f)
        }
        world.configure(entity) {
            val position = it[positionComponentType]
            assertEquals(10f, position.x)
            assertEquals(20f, position.y)

            val velocity = it[VelocityComponent]
            assertEquals(5f, velocity.dx)
            assertEquals(0f, velocity.dy)
        }
    }

    @Test
    fun `test family creation and entity matching`() {
        val world = world {}
        val positionComponentType = PositionComponent
        val velocityComponentType = VelocityComponent

        // 创建包含Position组件的实体
        val entity1 = world.create {
            it[positionComponentType] = PositionComponent(10f, 20f)
        }

        // 创建包含Position和Velocity组件的实体
        val entity2 = world.create {
            it[positionComponentType] = PositionComponent(30f, 40f)
            it[velocityComponentType] = VelocityComponent(5f, 0f)
        }

        // 创建只匹配Position组件的family
        val positionFamily = world.family { it.all(positionComponentType) }

        // 创建匹配Position和Velocity组件的family
        val positionVelocityFamily = world.family { it.all(positionComponentType, velocityComponentType) }

        assertEquals(2, positionFamily.count())
        assertEquals(1, positionVelocityFamily.count())
        assertTrue(entity1 in positionFamily)
        assertTrue(entity2 in positionFamily)
        assertFalse(entity1 in positionVelocityFamily)
        assertTrue(entity2 in positionVelocityFamily)
    }

    @Test
    fun `test component type registration`() {
        val world = world {}
        val positionComponentType = PositionComponent
        val velocityComponentType = VelocityComponent

        assertNotNull(positionComponentType)
        assertNotNull(velocityComponentType)
        assertNotEquals(positionComponentType.index, velocityComponentType.index)
    }

    @Test
    fun `test entity signals`() {
        val world = world {}
        val positionComponentType = PositionComponent

        var entityCreatedCount = 0
        var entityChangedCount = 0
        var entityRemovedCount = 0

        world.onEntityCreated.add { entityCreatedCount++ }
        world.onEntityChanged.add { entityChangedCount++ }
        world.onEntityRemoved.add { entityRemovedCount++ }

        val entity = world.create()
        assertEquals(1, entityCreatedCount)

        world.configure(entity) {
            it[positionComponentType] = PositionComponent(10f, 20f)
        }
        assertEquals(1, entityChangedCount)

        world.delete(entity)
        assertEquals(1, entityRemovedCount)
    }

    @Test
    fun `test world update`() {
        val positionComponentType = PositionComponent
        val velocityComponentType = VelocityComponent

        // 注册系统
        val world = world { this bind singleton { new(::MovementSystem) } }

        // 创建实体
        val entity = world.create {
            it[positionComponentType] = PositionComponent(10f, 20f)
            it[velocityComponentType] = VelocityComponent(5f, 3f)
        }

        // 更新世界
        world.update(1.seconds)
        world.configure(entity) {
            val position = it[positionComponentType]
            assertEquals(15f, position.x)
            assertEquals(23f, position.y)
        }
    }

    @Test
    fun `test bits operations`() {
        val bits = bitsOf()
        bits.setBit(0)
        bits.setBit(2)
        bits.setBit(4)

        assertTrue(0 in bits)
        assertTrue(2 in bits)
        assertTrue(4 in bits)
        assertFalse(1 in bits)
        assertFalse(3 in bits)

        bits.clearBit(2)
        assertFalse(2 in bits)

        bits.clearAll()
        assertFalse(0 in bits)
        assertFalse(4 in bits)
    }

    @Test
    fun `test system priority`() {

        // 注册系统
        val world = world {
            this bind singleton { new(::HighPrioritySystem) }
            this bind singleton { new(::LowPrioritySystem) }
        }

        // 更新世界
        world.update(0.seconds)
        val highPrioritySystem by world.instance<HighPrioritySystem>()
        assertTrue(highPrioritySystem.updated)

        val lowPrioritySystem by world.instance<LowPrioritySystem>()
        assertTrue(lowPrioritySystem.updated)
    }

    @Test
    fun `test delayed removal component system`() {
        val world = world {}
        val positionComponentType = PositionComponent

        // 启用延迟移除
        world.enableDelayedRemoval(positionComponentType)

        // 创建实体
        val entity = world.create {
            it[positionComponentType] = PositionComponent(10f, 20f)
        }
        world.configure(entity) {
            assertNotNull(it.getOrNull(positionComponentType))
        }
        world.configure(entity) {
            it -= positionComponentType
        }
        world.configure(entity) {
            // 组件应该仍然存在，直到更新
            assertNotNull(it.getOrNull(positionComponentType))
        }
        // 更新世界
        world.update(0.seconds)
        world.configure(entity) {
            // 现在组件应该被移除了
            assertNull(it.getOrNull(positionComponentType))
        }
    }

    @Test
    fun `test world entity size`() {
        val world = world {}
        assertEquals(0, world.entitySize)

        val entity1 = world.create()
        assertEquals(1, world.entitySize)

        val entity2 = world.create()
        assertEquals(2, world.entitySize)

        world.delete(entity1)
        assertEquals(1, world.entitySize)

        world.delete(entity2)
        assertEquals(0, world.entitySize)
    }
}

// 用于测试的组件类
data class PositionComponent(var x: Float, var y: Float) {
    companion object : ComponentType<PositionComponent>()
}

data class VelocityComponent(var dx: Float, var dy: Float) {
    companion object : ComponentType<VelocityComponent>()
}

// 创建一个简单的移动系统
class MovementSystem(world: World) : IteratingSystem(world, configuration = {}) {
    override fun onTickEntity(entity: Entity, deltaTime: Duration) {
        // 确保实体同时有PositionComponent和VelocityComponent才处理
        if (PositionComponent in entity && VelocityComponent in entity) {
            val position = entity[PositionComponent]
            val velocity = entity[VelocityComponent]
            position.x += velocity.dx * deltaTime.inWholeSeconds
            position.y += velocity.dy * deltaTime.inWholeSeconds
        }
    }
}

class HighPrioritySystem(world: World) : UpdateSystem(world), Prioritized {
    override val priority: Int = 100
    var updated = false
    override fun update(deltaTime: Duration) {
        updated = true
    }
}

class LowPrioritySystem(world: World) : UpdateSystem(world), Prioritized {
    override val priority: Int = 0
    var updated = false
    override fun update(deltaTime: Duration) {
        val system by world.instance<HighPrioritySystem>()
        // 确保高优先级系统已经更新
        assertTrue(system.updated)
        updated = true
    }
}