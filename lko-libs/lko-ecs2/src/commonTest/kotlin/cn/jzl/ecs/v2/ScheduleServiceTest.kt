package cn.jzl.ecs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.milliseconds

/**
 * ScheduleService测试类
 * 测试调度服务的各种功能，包括任务调度、等待下一帧、延迟执行、多线程并发等
 */
class ScheduleServiceTest : ECSBasicTest() {

    /**
     * 测试基本任务调度功能
     */
    @Test
    fun testBasicTaskExecution() {
        val world = createWorld()
        var taskExecuted = false

        world.schedule("testBasicTask") {
            taskExecuted = true
        }

        // 第一次更新应该执行任务
        world.update(0.seconds)
        assertTrue(taskExecuted, "任务应该被执行")
    }

    /**
     * 测试延迟任务功能
     */
    @Test
    fun testDelayTask() {
        val world = createWorld()
        var taskExecuted = false

        world.schedule("testDelayTask") {
            withTask {
                delay(100.milliseconds)
                taskExecuted = true
            }
        }
        // 第一次更新，延迟未完成，任务不应执行
        world.update(0.seconds)
        assertFalse(taskExecuted, "延迟未完成时任务不应执行")

        // 第二次更新，延迟未完成，任务不应执行
        world.update(50.milliseconds)
        assertFalse(taskExecuted, "延迟未完成时任务不应执行")

        // 第三次更新，累计延迟超过100ms，任务应执行
        world.update(60.milliseconds)
        assertTrue(taskExecuted, "延迟完成后任务应该执行")
    }

    /**
     * 测试等待下一帧功能
     */
    @Test
    fun testWaitNextFrame() {
        val world = createWorld()
        val executionOrder = mutableListOf<Int>()

        world.schedule("testWaitNextFrame") {
            executionOrder.add(1) // 第一帧执行
            withTask {
                waitNextFrame()
                executionOrder.add(3) // 第二帧执行
            }
        }

        world.schedule("testSecondTask") {
            executionOrder.add(2) // 第一帧执行
        }

        // 第一次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2), executionOrder, "第一帧应该按顺序执行任务")

        // 第二次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2, 3), executionOrder, "第二帧应该执行等待的任务")
    }

    /**
     * 测试多个任务的调度和执行顺序
     */
    @Test
    fun testMultipleTasks() {
        val world = createWorld()
        val results = mutableListOf<Int>()

        world.schedule("task1") {
            results.add(1)
        }

        world.schedule("task2") {
            results.add(2)
        }

        world.schedule("task3") {
            withTask {
                waitNextFrame()
                results.add(3)
            }
        }

        // 第一次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2), results, "第一次更新应该执行非等待任务")

        // 第二次更新
        world.update(0.seconds)
        assertEquals(listOf(1, 2, 3), results, "第二次更新应该执行等待的任务")
    }

    /**
     * 测试循环任务功能
     */
    @Test
    fun testLoopTask() {
        val world = createWorld()
        val executionCount = mutableListOf<Int>()
        var counter = 0

        world.schedule("testLoopTask") {
            withLoop {
                counter++
                executionCount.add(counter)
                if (counter >= 3) {
                    it.stop() // 停止循环
                }
            }
        }

        // 初始状态
        assertEquals(emptyList(), executionCount)

        // 第一次更新 - 执行第一次循环
        world.update(0.seconds)
        assertEquals(listOf(1), executionCount, "第一次更新应该执行第一次循环")

        // 第二次更新 - 执行第二次循环
        world.update(0.seconds)
        assertEquals(listOf(1, 2), executionCount, "第二次更新应该执行第二次循环")

        // 第三次更新 - 执行第三次循环并停止
        world.update(0.seconds)
        assertEquals(listOf(1, 2, 3), executionCount, "第三次更新应该执行第三次循环并停止")

        // 第四次更新 - 不会再执行
        world.update(0.seconds)
        assertEquals(listOf(1, 2, 3), executionCount, "第四次更新不应该执行任务")
    }

    /**
     * 测试实体创建功能
     */
    @Test
    fun testCreateEntity() {
        val world = createWorld()
        val schedule = world.schedule("testCreateEntity") {
            val entity = create { }
            assertTrue(world.isActive(entity), "实体应该是活动的")
        }

        while (world.isActive(schedule)) {
            world.update(0.seconds)
        }

        assertEquals(1, world.entityService.size, "实体数量应该为1")
    }

    /**
     * 测试调度器活跃状态检查
     */
    @Test
    fun testScheduleActiveState() {
        val world = createWorld()

        // 立即执行的任务
        val immediateSchedule = world.schedule("immediateTask") {
            // 空任务
        }

        // 延迟执行的任务
        val delayedSchedule = world.schedule("delayedTask") {
            withTask {
                delay(100.milliseconds)
            }
        }

        // 初始状态 - 两个调度器都应该是活跃的
        assertTrue(world.isActive(immediateSchedule), "立即执行的调度器初始应该是活跃的")
        assertTrue(world.isActive(delayedSchedule), "延迟执行的调度器初始应该是活跃的")

        // 第一次更新后 - 立即执行的任务应该完成
        world.update(0.seconds)
        assertFalse(world.isActive(immediateSchedule), "立即执行的调度器执行后应该不活跃")
        assertTrue(world.isActive(delayedSchedule), "延迟执行的调度器还应该是活跃的")

        // 第二次更新后 - 延迟任务应该完成
        world.update(100.milliseconds)
        // 第三次更新后 - 延时任务剩余流程应该执行
        world.update(0.seconds)
        assertFalse(world.isActive(delayedSchedule), "延迟执行的调度器执行后应该不活跃")
    }

    /**
     * 测试条件执行功能
     */
    @Test
    fun testConditionalExecution() {
        val world = createWorld()
        var conditionMet = false
        var result = 0

        world.schedule("testConditionalExecution") {
            // 条件不满足时等待
            while (!conditionMet) {
                waitNextFrame()
            }
            result = 42
        }

        // 第一次更新，条件不满足
        world.update(0.seconds)
        assertEquals(0, result, "条件不满足时结果应该保持不变")

        // 设置条件满足
        conditionMet = true

        // 第二次更新，条件满足，执行后续代码
        world.update(0.seconds)
        assertEquals(42, result, "条件满足后应该执行后续代码")
    }

    /**
     * 测试任务优先级功能
     */
    @Test
    fun testTaskPriority() {
        val world = createWorld()
        val executionOrder = mutableListOf<String>()

        // 低优先级任务
        world.schedule("lowPriorityTask", ScheduleTaskPriority.LOW) {
            executionOrder.add("low")
        }

        // 普通优先级任务
        world.schedule("normalPriorityTask", ScheduleTaskPriority.NORMAL) {
            executionOrder.add("normal")
        }

        // 高优先级任务
        world.schedule("highPriorityTask", ScheduleTaskPriority.HIGH) {
            executionOrder.add("high")
        }

        world.update(0.seconds)
        assertEquals(listOf("high", "normal", "low"), executionOrder, "任务应该按照优先级顺序执行")
    }

    /**
     * 测试周期性延迟任务
     */
    @Test
    fun testPeriodicDelayTask() {
        val world = createWorld()
        val executionTimes = mutableListOf<Int>()
        var counter = 0

        world.schedule("testPeriodicTask") {
            withTask {
                while (counter < 3) {
                    counter++
                    executionTimes.add(counter)
                    delay(50.milliseconds)
                }
            }
        }

        // 初始状态
        assertEquals(emptyList(), executionTimes)

        // 第一次更新 - 执行第一次
        world.update(50.milliseconds)
        assertEquals(listOf(1), executionTimes)

        // 第二次更新 - 执行第二次
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2), executionTimes)

        // 第三次更新 - 执行第三次并完成循环
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2, 3), executionTimes)

        // 第四次更新 - 不会再执行
        world.update(50.milliseconds)
        assertEquals(listOf(1, 2, 3), executionTimes)
    }

    /**
     * 测试实体和组件操作
     */
    @Test
    fun testEntityAndComponentOperations() {
        val world = createWorld()
        var testValue = 0

        val schedule = world.schedule("testEntityComponentOps") {
            // 创建实体
            val entity = create { }

            // 简单验证实体创建成功
            testValue = 42
        }

        // 执行更新
        while (world.isActive(schedule)) world.update(0.seconds)
        assertEquals(42, testValue, "测试值应该被设置为42")
    }

    /**
     * 测试调度器生命周期
     */
    @Test
    fun testScheduleLifecycle() {
        val world = createWorld()
        var taskExecuted = false

        // 创建调度器
        val schedule = world.schedule("testScheduleLifecycle") {
            taskExecuted = true
        }

        // 验证调度器初始状态
        assertTrue(world.isActive(schedule), "调度器初始状态应该是活跃的")

        // 执行更新
        world.update(0.seconds)

        // 验证任务执行和调度器状态
        assertTrue(taskExecuted, "任务应该被执行")
        assertFalse(world.isActive(schedule), "任务执行完成后调度器应该不活跃")
    }

    /**
     * 测试复杂协程流程
     */
    @Test
    fun testComplexCoroutineFlow() {
        val world = createWorld()
        val executionSteps = mutableListOf<Int>()

        world.schedule("testComplexFlow") {
            executionSteps.add(1) // 步骤1：开始
            withTask {
                delay(50.milliseconds)
                executionSteps.add(2) // 步骤2：延迟后
                waitNextFrame()
                executionSteps.add(3) // 步骤3：下一帧后
            }
        }

        // 执行必要的更新
        world.update(0.seconds) // 执行步骤1
        assertEquals(listOf(1), executionSteps, "应该先执行步骤1")
        world.update(50.milliseconds) // 执行步骤2
        assertEquals(listOf(1, 2), executionSteps, "应该先执行步骤2")
        world.update(0.seconds)      // 执行步骤3
        assertEquals(listOf(1, 2, 3), executionSteps, "应该先执行步骤3")
    }

    @Test
    fun testResourceAccess() {
        val world = createWorld()
        val order = mutableListOf<String>()
        world.schedule {
            val resource by resource<Int>()
            withTask {
                order.add("read")
                assertEquals(resource, 42, "读取资源值应该是42")
            }
            println("读取资源值: $resource")
        }
        world.schedule("writeResource") {
            var resource by mutableResource<Int>()
            withTask {
                resource = 42
                order.add("write")
            }
            println("写入资源值: $resource")
        }
        world.update(0.seconds)
        world.update(0.seconds)
        assertEquals(listOf("write", "read"), order, "写入资源后应该先执行读取任务")
    }
}
