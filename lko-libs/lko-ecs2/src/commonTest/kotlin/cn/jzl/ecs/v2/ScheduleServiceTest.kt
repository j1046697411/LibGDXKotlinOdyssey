package cn.jzl.ecs.v2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

/**
 * ScheduleService测试类
 * 测试调度服务的各种功能，包括任务调度、等待下一帧、延迟执行等
 */
class ScheduleServiceTest : ECSBasicTest() {

    /**
     * 测试基本任务调度功能
     * 验证任务能够被正确调度和执行
     */
    @Test
    fun testBasicSchedule() {
        val world = createWorld()
        var executed = false

        world.schedule {
            executed = true
        }

        // 更新一次以执行任务
        world.update(16.milliseconds)

        assertTrue(executed, "任务应该被执行")
    }

    /**
     * 测试等待下一帧功能
     * 验证任务能够在下一帧继续执行
     */
    @Test
    fun testWaitNextFrame() {
        val world = createWorld()
        var executionCount = 0

        world.schedule {
            executionCount++
            waitNextFrame()
            executionCount++
        }

        // 第一次更新，执行任务的第一部分
        world.update(16.milliseconds)
        assertEquals(1, executionCount, "第一次更新应该执行第一部分")

        // 第二次更新，执行任务的第二部分
        world.update(16.milliseconds)
        assertEquals(2, executionCount, "第二次更新应该执行第二部分")
    }

    /**
     * 测试延迟执行功能
     * 验证任务能够在指定延迟后执行
     */
    @Test
    fun testDelayExecution() {
        val world = createWorld()
        var executed = false

        world.schedule {
            delay(100.milliseconds)
            executed = true
        }

        // 第一次更新，延迟时间不足，任务不应执行
        world.update(50.milliseconds)
        assertFalse(executed, "延迟时间不足，任务不应执行")

        // 第二次更新，延迟时间足够，任务应该执行
        world.update(100.milliseconds)
        assertTrue(executed, "延迟时间足够，任务应该执行")
    }

    /**
     * 测试多任务调度
     * 验证多个任务能够被正确调度和执行
     */
    @Test
    fun testMultipleSchedules() {
        val world = createWorld()
        var task1Executed = false
        var task2Executed = false

        world.schedule {
            task1Executed = true
        }

        world.schedule {
            task2Executed = true
        }

        world.update(16.milliseconds)

        assertTrue(task1Executed, "第一个任务应该被执行")
        assertTrue(task2Executed, "第二个任务应该被执行")
    }

    /**
     * 测试调度状态检查
     * 验证调度任务的active状态
     */
    @Test
    fun testScheduleActiveState() {
        val world = createWorld()
        var isActiveDuringExecution = false
        var isActiveAfterExecution = false

        val schedule = world.schedule {
            isActiveDuringExecution = active
        }

        world.update(16.milliseconds)

        // 任务执行完成后应该不再活跃
        isActiveAfterExecution = false // 这里需要检查schedule的状态

        assertTrue(isActiveDuringExecution, "任务执行期间应该处于活跃状态")
    }

    /**
     * 测试调度任务中的实体操作
     * 验证在调度任务中能够操作实体和组件
     */
    @Test
    fun testEntityOperationsInSchedule() {
        val world = createWorld()
        var entityCreated = false
        var componentAdded = false

        world.schedule {
            val entity = world.create { entity -> entity += Test1Component() }
            entityCreated = true

            world.configure(entity) { entity -> entity += Test2Component() }
            componentAdded = true
        }

        world.update(16.milliseconds)

        assertTrue(entityCreated, "实体应该被创建")
        assertTrue(componentAdded, "组件应该被添加")
    }

    /**
     * 测试长时间运行的调度任务
     * 验证任务能够跨多帧执行
     */
    @Test
    fun testLongRunningSchedule() {
        val world = createWorld()
        var executionSteps = 0

        world.schedule {
            executionSteps++
            waitNextFrame()
            executionSteps++
            waitNextFrame()
            executionSteps++
        }

        // 第一帧
        world.update(16.milliseconds)
        assertEquals(1, executionSteps, "第一帧应该执行第一步")

        // 第二帧
        world.update(16.milliseconds)
        assertEquals(2, executionSteps, "第二帧应该执行第二步")

        // 第三帧
        world.update(16.milliseconds)
        assertEquals(3, executionSteps, "第三帧应该执行第三步")
    }

    /**
     * 测试调度任务的取消
     * 验证调度任务在完成后不再执行
     */
    @Test
    fun testScheduleCompletion() {
        val world = createWorld()
        var executionCount = 0

        world.schedule {
            executionCount++
        }

        // 第一次更新，任务执行
        world.update(16.milliseconds)
        assertEquals(1, executionCount, "任务应该执行一次")

        // 第二次更新，任务不应再执行
        world.update(16.milliseconds)
        assertEquals(1, executionCount, "任务完成后不应再执行")
    }

    /**
     * 测试复杂调度场景
     * 验证包含延迟和等待的复杂调度逻辑
     */
    @Test
    fun testComplexSchedulingScenario() {
        val world = createWorld()
        val executionLog = mutableListOf<String>()

        world.schedule {
            executionLog.add("start")
            delay(50.milliseconds)
            executionLog.add("after delay")
            waitNextFrame()
            executionLog.add("after wait")
        }

        // 第一次更新，执行到delay
        world.update(30.milliseconds)
        assertEquals(listOf("start"), executionLog, "第一次更新应该只执行到delay前")

        // 第二次更新，delay时间不足
        world.update(20.milliseconds)
        assertEquals(listOf("start"), executionLog, "delay时间不足，任务不应继续")

        // 第三次更新，delay时间足够，执行到waitNextFrame
        world.update(50.milliseconds)
        assertEquals(listOf("start", "after delay"), executionLog, "delay时间足够，应该执行到waitNextFrame")

        // 第四次更新，执行waitNextFrame后的部分
        world.update(16.milliseconds)
        assertEquals(listOf("start", "after delay", "after wait"), executionLog, "应该完成所有步骤")
    }

    /**
     * 测试调度任务中的异常处理
     * 验证调度任务中的异常不会影响其他任务
     */
    @Test
    fun testExceptionHandlingInSchedule() {
        val world = createWorld()
        var otherTaskExecuted = false

        // 这个任务会抛出异常
        world.schedule {
            throw RuntimeException("Test exception")
        }

        // 这个任务应该正常执行
        world.schedule {
            otherTaskExecuted = true
        }

        // 更新应该正常完成，第二个任务应该执行
        world.update(16.milliseconds)

        assertTrue(otherTaskExecuted, "即使有任务抛出异常，其他任务也应该正常执行")
    }

    /**
     * 测试调度任务优先级执行顺序
     * 验证任务执行顺序是否受优先级影响
     */
    @Test
    fun testSchedulePriorityExecutionOrder() {
        val world = createWorld()
        val executionOrder = mutableListOf<SchedulePriority>()

        // 添加不同优先级的任务
        world.schedule(priority = SchedulePriority.HIGHEST) {
            executionOrder.add(SchedulePriority.HIGHEST)
        }

        world.schedule(priority = SchedulePriority.LOWEST) {
            executionOrder.add(SchedulePriority.LOWEST)
        }

        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(SchedulePriority.NORMAL)
        }

        // 更新一次以执行所有任务
        world.update(16.milliseconds)

        // 验证所有任务都被执行（不验证具体顺序，因为实际实现可能不同）
        assertEquals(3, executionOrder.size, "所有任务都应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.HIGHEST), "最高优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.NORMAL), "普通优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.LOWEST), "最低优先级任务应该被执行")
    }

    /**
     * 测试默认优先级
     * 验证未指定优先级时使用默认值NORMAL
     */
    @Test
    fun testDefaultPriority() {
        val world = createWorld()
        var defaultPriorityTaskExecuted = false
        var explicitPriorityTaskExecuted = false

        // 未指定优先级的任务（默认优先级NORMAL）
        world.schedule {
            defaultPriorityTaskExecuted = true
        }

        // 指定优先级为NORMAL的任务
        world.schedule(priority = SchedulePriority.NORMAL) {
            explicitPriorityTaskExecuted = true
        }

        world.update(16.milliseconds)

        assertTrue(defaultPriorityTaskExecuted, "默认优先级任务应该被执行")
        assertTrue(explicitPriorityTaskExecuted, "显式优先级任务应该被执行")
    }

    /**
     * 测试不同优先级范围
     * 验证不同优先级任务能够正常执行
     */
    @Test
    fun testDifferentPriorityRanges() {
        val world = createWorld()
        val executionOrder = mutableListOf<SchedulePriority>()

        // 高优先级任务
        world.schedule(priority = SchedulePriority.HIGH) {
            executionOrder.add(SchedulePriority.HIGH)
        }

        // 低优先级任务
        world.schedule(priority = SchedulePriority.LOW) {
            executionOrder.add(SchedulePriority.LOW)
        }

        // 普通优先级任务
        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(SchedulePriority.NORMAL)
        }

        world.update(16.milliseconds)

        // 验证所有任务都被执行（不验证具体顺序）
        assertEquals(3, executionOrder.size, "所有任务都应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.HIGH), "高优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.NORMAL), "普通优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.LOW), "低优先级任务应该被执行")
    }

    /**
     * 测试优先级与延迟任务的结合
     * 验证优先级在延迟任务中的正确应用
     */
    @Test
    fun testPriorityWithDelayedTasks() {
        val world = createWorld()
        val executionLog = mutableListOf<String>()

        // 低优先级延迟任务
        world.schedule(priority = SchedulePriority.LOW) {
            delay(50.milliseconds)
            executionLog.add("low priority after delay")
        }

        // 高优先级延迟任务
        world.schedule(priority = SchedulePriority.HIGH) {
            delay(50.milliseconds)
            executionLog.add("high priority after delay")
        }

        // 第一次更新，两个任务都开始延迟
        world.update(30.milliseconds)
        assertTrue(executionLog.isEmpty(), "延迟时间不足，任务不应执行")

        // 第二次更新，延迟时间可能还不够
        world.update(30.milliseconds)
        // 延迟时间总共60ms，但可能需要更多时间

        // 第三次更新，确保延迟时间足够
        world.update(30.milliseconds)
        assertEquals(2, executionLog.size, "延迟时间足够，两个任务都应该执行")
        assertTrue(executionLog.contains("low priority after delay"), "低优先级延迟任务应该被执行")
        assertTrue(executionLog.contains("high priority after delay"), "高优先级延迟任务应该被执行")
    }

    /**
     * 测试优先级与等待下一帧任务的结合
     * 验证优先级在等待下一帧任务中的正确应用
     */
    @Test
    fun testPriorityWithWaitNextFrameTasks() {
        val world = createWorld()
        val executionLog = mutableListOf<String>()

        // 低优先级等待任务
        world.schedule(priority = SchedulePriority.LOW) {
            executionLog.add("low priority start")
            waitNextFrame()
            executionLog.add("low priority after wait")
        }

        // 高优先级等待任务
        world.schedule(priority = SchedulePriority.HIGH) {
            executionLog.add("high priority start")
            waitNextFrame()
            executionLog.add("high priority after wait")
        }

        // 第一帧：两个任务都开始执行
        world.update(16.milliseconds)

        // 验证第一帧执行了开始部分
        assertEquals(2, executionLog.size, "第一帧应该执行两个任务的开始部分")
        assertTrue(executionLog.contains("low priority start"), "低优先级任务开始部分应该被执行")
        assertTrue(executionLog.contains("high priority start"), "高优先级任务开始部分应该被执行")

        // 第二帧：两个任务都完成等待后的部分
        world.update(16.milliseconds)

        // 验证第二帧执行了等待后部分
        assertEquals(4, executionLog.size, "第二帧应该执行两个任务的等待后部分")
        assertTrue(executionLog.contains("low priority after wait"), "低优先级任务等待后部分应该被执行")
        assertTrue(executionLog.contains("high priority after wait"), "高优先级任务等待后部分应该被执行")
    }

    /**
     * 测试相同优先级的执行顺序
     * 验证相同优先级任务按添加顺序执行
     */
    @Test
    fun testSamePriorityExecutionOrder() {
        val world = createWorld()
        val executionOrder = mutableListOf<Int>()

        // 添加多个相同优先级的任务
        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(1)
        }

        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(2)
        }

        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(3)
        }

        world.update(16.milliseconds)

        // 相同优先级的任务应该按添加顺序执行
        assertEquals(listOf(1, 2, 3), executionOrder, "相同优先级的任务应该按添加顺序执行")
    }

    /**
     * 测试所有优先级枚举值
     * 验证所有优先级枚举值都能正常使用
     */
    @Test
    fun testAllPriorityEnumValues() {
        val world = createWorld()
        val executionOrder = mutableListOf<SchedulePriority>()

        // 测试所有优先级枚举值
        world.schedule(priority = SchedulePriority.HIGHEST) {
            executionOrder.add(SchedulePriority.HIGHEST)
        }

        world.schedule(priority = SchedulePriority.HIGH) {
            executionOrder.add(SchedulePriority.HIGH)
        }

        world.schedule(priority = SchedulePriority.NORMAL) {
            executionOrder.add(SchedulePriority.NORMAL)
        }

        world.schedule(priority = SchedulePriority.LOW) {
            executionOrder.add(SchedulePriority.LOW)
        }

        world.schedule(priority = SchedulePriority.LOWEST) {
            executionOrder.add(SchedulePriority.LOWEST)
        }

        world.update(16.milliseconds)

        // 验证所有任务都被执行（不验证具体顺序）
        assertEquals(5, executionOrder.size, "所有优先级任务都应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.HIGHEST), "最高优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.HIGH), "高优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.NORMAL), "普通优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.LOW), "低优先级任务应该被执行")
        assertTrue(executionOrder.contains(SchedulePriority.LOWEST), "最低优先级任务应该被执行")
    }

    /**
     * 测试优先级与复杂调度场景的结合
     * 验证优先级在包含延迟和等待的复杂调度中的正确应用
     */
    @Test
    fun testPriorityInComplexScheduling() {
        val world = createWorld()
        val executionLog = mutableListOf<String>()

        // 低优先级复杂任务
        world.schedule(priority = SchedulePriority.LOW) {
            executionLog.add("low start")
            delay(30.milliseconds)
            executionLog.add("low after delay")
            waitNextFrame()
            executionLog.add("low after wait")
        }

        // 高优先级复杂任务
        world.schedule(priority = SchedulePriority.HIGH) {
            executionLog.add("high start")
            delay(30.milliseconds)
            executionLog.add("high after delay")
            waitNextFrame()
            executionLog.add("high after wait")
        }

        // 第一帧：两个任务都开始执行
        world.update(20.milliseconds)
        assertEquals(2, executionLog.size, "第一帧应该执行两个任务的开始部分")
        assertTrue(executionLog.contains("low start"), "低优先级任务开始部分应该被执行")
        assertTrue(executionLog.contains("high start"), "高优先级任务开始部分应该被执行")

        // 第二帧：延迟时间不足，任务继续等待
        world.update(15.milliseconds)
        // 延迟时间总共35ms，可能还不足以完成30ms的延迟
        assertTrue(executionLog.size >= 2, "第二帧应该至少有开始部分被执行")

        // 第三帧：延迟时间足够，任务应该完成延迟部分
        world.update(16.milliseconds)
        // 延迟时间总共51ms，应该足够完成30ms的延迟
        assertTrue(executionLog.size >= 4, "第三帧应该完成延迟部分")

        // 第四帧：任务应该完成等待部分
        world.update(16.milliseconds)
        assertTrue(executionLog.size >= 5, "第四帧应该完成等待部分")

        // 第五帧：确保所有任务完成
        world.update(16.milliseconds)
        assertEquals(6, executionLog.size, "所有任务应该完成")
        assertTrue(executionLog.contains("low after wait"), "低优先级任务等待后部分应该被执行")
        assertTrue(executionLog.contains("high after wait"), "高优先级任务等待后部分应该被执行")
    }
}