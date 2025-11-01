package cn.jzl.ecs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ScheduleServiceTest {

    @Test
    fun `test schedule basic task`() {
        val world = world {}
        var taskExecuted = false
        world.schedule { taskExecuted = true }
        // 第一次更新应该执行任务
        world.update(0.seconds)
        assertTrue(taskExecuted)
    }

    @Test
    fun `test waitNextFrame`() {
        val world = world {}
        var frameCount = 0
        var taskStep = 0
        
        world.schedule {
            taskStep = 1
            waitNextFrame()
            frameCount++
            taskStep = 2
            waitNextFrame()
            frameCount++
            taskStep = 3
        }
        
        // 第一次更新后，应该执行到第一个waitNextFrame
        world.update(0.seconds)
        assertEquals(1, taskStep)
        assertEquals(0, frameCount)
        
        // 第二次更新后，应该执行到第二个waitNextFrame
        world.update(0.seconds)
        assertEquals(2, taskStep)
        assertEquals(1, frameCount)
        
        // 第三次更新后，应该完成任务
        world.update(0.seconds)
        assertEquals(3, taskStep)
        assertEquals(2, frameCount)
    }

    @Test
    fun `test delay`() {
        val world = world {}
        var taskExecuted = false
        
        world.schedule {
            delay(100.milliseconds)
            taskExecuted = true
        }

        world.update(0.seconds)

        // 第一次更新，时间不够，任务不应该执行
        world.update(50.milliseconds)
        assertFalse(taskExecuted)
        
        // 第二次更新，累计时间足够，任务应该执行
        world.update(60.milliseconds)  // 总共110ms，超过100ms
        assertTrue(taskExecuted)
    }

    @Test
    fun `test multiple schedules`() {
        val world = world {}
        val results = mutableListOf<Int>()
        
        world.schedule {
            results.add(1)
        }
        
        world.schedule {
            results.add(2)
        }
        
        world.schedule {
            waitNextFrame()
            results.add(3)
        }
        
        // 第一次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2), results)
        
        // 第二次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2, 3), results)
    }

    @Test
    fun `test access world and components in schedule`() {
        val world = world {}
        val positionComponentType = PositionComponent
        
        // 创建一个带有位置组件的实体
        val entity = world.create {
            it[positionComponentType] = PositionComponent(10f, 20f)
        }
        
        var updatedPosition = false
        
        world.schedule {
            // 在schedule中访问world和组件
            world.configure(entity) {
                val position = it[positionComponentType]
                position.x = 30f
                position.y = 40f
            }
            updatedPosition = true
        }
        
        world.update(0.seconds)
        assertTrue(updatedPosition)
        
        world.configure(entity) {
            val position = it[positionComponentType]
            assertEquals(30f, position.x)
            assertEquals(40f, position.y)
        }
    }

    @Test
    fun `test schedule with entity operations`() {
        val world = world {}
        val positionComponentType = PositionComponent
        var entityCount = 0
        
        world.schedule {
            val newEntity = world.create { it[positionComponentType] = PositionComponent(5f, 5f) }
            entityCount++
            // 删除实体
            world.delete(newEntity)
            entityCount--
        }
        
        world.update(0.seconds)
        assertEquals(0, entityCount)
        assertEquals(0, world.entitySize)
    }

    @Test
    fun `test nested delays`() {
        val world = world {}
        var step = 0
        
        world.schedule {
            step = 1
            delay(50.milliseconds)
            step = 2
            delay(100.milliseconds)
            step = 3
        }
        
        // 初始状态
        assertEquals(0, step)

        // 第一次更新 - 0ms
        world.update(0.milliseconds)
        assertEquals(1, step) // 应该还没到第一个延迟

        // 第二次更新 - 50ms
        world.update(50.milliseconds)
        assertEquals(2, step) // 应该已经通过第一个延迟

        // 第三次更新 - 1ms（累计51ms）
        world.update(1.milliseconds)
        assertEquals(2, step) // 应该还没到第二个延迟

        // 第四次更新 - 99ms（累计150ms）
        world.update(99.milliseconds)
        assertEquals(3, step) // 应该还没到第二个延迟
        
        // 第五次更新 - 1ms（累计151ms）
        world.update(1.milliseconds)
        assertEquals(3, step) // 应该已经完成所有延迟
    }

    @Test
    fun `test task dependencies with waitNextFrame`() {
        val world = world {}
        val results = mutableListOf<Int>()
        
        // 第一个任务
        world.schedule {
            results.add(1)
            waitNextFrame()
            results.add(2)
        }
        
        // 第二个任务，应该等待第一个任务的第一部分完成
        world.schedule {
            results.add(3)
        }
        
        // 第一次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 3), results)
        
        // 第二次更新，第一个任务的第二部分执行
        world.update(0.seconds)
        assertEquals(listOf(1, 3, 2), results)
    }

    @Test
    fun `test schedule with cancellation flag`() {
        val world = world {}
        var executed = false
        var shouldCancel = false
        
        world.schedule {
            waitNextFrame()
            if (!shouldCancel) {
                executed = true
            }
        }
        
        // 第一次更新，任务进入等待状态
        world.update(0.seconds)
        assertFalse(executed)
        
        // 设置取消标志
        shouldCancel = true
        
        // 第二次更新，任务应该不会执行主要逻辑
        world.update(0.seconds)
        assertFalse(executed)
    }

    @Test
    fun `test schedule with exception handling`() {
        val world = world {}
        var exceptionCaught = false
        var afterException = false
        
        // 测试任务中的异常不会影响其他任务
        world.schedule {
            try {
                throw IllegalArgumentException("Test exception")
            } catch (e: Exception) {
                exceptionCaught = true
            }
            afterException = true
        }
        
        var otherTaskExecuted = false
        world.schedule {
            otherTaskExecuted = true
        }
        
        // 执行更新
        world.update(0.seconds)
        
        // 验证异常被捕获且后续代码执行
        assertTrue(exceptionCaught)
        assertTrue(afterException)
        assertTrue(otherTaskExecuted)
    }

    @Test
    fun `test periodic schedule`() {
        val world = world {}
        val executionCount = mutableListOf<Int>()
        var counter = 0
        
        world.schedule {
            while (counter < 3) {
                counter++
                executionCount.add(counter)
                delay(50.milliseconds)
            }
        }
        
        // 初始状态
        assertEquals(emptyList(), executionCount)
        
        // 第一次更新 - 执行第一次
        world.update(50.milliseconds)
        assertEquals(listOf(1), executionCount)
        
        // 第二次更新 - 执行第二次
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2), executionCount)
        
        // 第三次更新 - 执行第三次并完成循环
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2, 3), executionCount)
        
        // 第四次更新 - 不会再执行
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2, 3), executionCount)
    }

    @Test
    fun `test conditional execution in schedule`() {
        val world = world {}
        var conditionMet = false
        var result = 0
        
        world.schedule {
            // 条件不满足时等待
            while (!conditionMet) {
                waitNextFrame()
            }
            result = 42
        }
        
        // 第一次更新，条件不满足
        world.update(0.seconds)
        assertEquals(0, result)
        
        // 设置条件满足
        conditionMet = true
        
        // 第二次更新，条件满足，执行后续代码
        world.update(0.seconds)
        assertEquals(42, result)
    }

    @Test
    fun `test multiple schedule dependencies with entity operations`() {
        val world = world {}
        val positionComponentType = PositionComponent
        var entity1: Entity? = null
        var entity2: Entity? = null
        var updateComplete = false
        
        // 第一个任务：创建实体
        world.schedule {
            entity1 = world.create { it[positionComponentType] = PositionComponent(10f, 20f) }
            // 立即创建第二个实体，避免时序问题
            entity2 = world.create { it[positionComponentType] = PositionComponent(30f, 40f) }
        }
        
        // 第二个任务：等待实体创建并更新它们
        world.schedule {
            // 等待第一个任务完成
            waitNextFrame()
            
            // 确保实体已创建
            if (entity1 != null && entity2 != null) {
                // 更新实体组件
                world.configure(entity1!!) { it[positionComponentType].x = 100f }
                world.configure(entity2!!) { it[positionComponentType].x = 200f }
                updateComplete = true
            }
        }
        
        // 第一次更新：执行第一个任务
        world.update(0.seconds)
        assertNotNull(entity1)
        assertNotNull(entity2)
        assertFalse(updateComplete)
        
        // 第二次更新：执行第二个任务
        world.update(0.seconds)
        assertTrue(updateComplete)
        
        // 验证组件更新
        world.configure(entity1!!) { assertEquals(100f, it[positionComponentType].x) }
        world.configure(entity2!!) { assertEquals(200f, it[positionComponentType].x) }
    }
}