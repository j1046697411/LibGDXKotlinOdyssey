package cn.jzl.ecs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
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
}