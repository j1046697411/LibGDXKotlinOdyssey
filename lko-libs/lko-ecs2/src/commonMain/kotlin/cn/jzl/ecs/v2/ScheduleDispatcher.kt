package cn.jzl.ecs.v2

import kotlin.time.Duration

/**
 * ScheduleDispatcher.kt 定义了调度器分发器的核心接口
 * 
 * 实现逻辑：
 * 1. 作为调度系统的核心抽象接口，定义任务调度的标准操作
 * 2. 支持多类型任务管理，实现灵活的任务调度策略
 * 3. 提供基于优先级的任务排序机制
 * 4. 支持时间驱动的延迟任务执行
 * 5. 通过统一的update方法实现帧同步的任务处理
 * 
 * 设计考虑：
 * - 接口设计遵循单一职责原则，专注于任务调度核心功能
 * - 支持多种任务类型，满足不同场景下的调度需求
 * - 基于优先级的调度机制确保关键任务优先执行
 * - Duration参数支持精确的时间控制，适应不同帧率
 * - 任务归属跟踪（通过scheduleDescriptor）支持依赖关系管理
 */
interface ScheduleDispatcher {

    /**
 * 添加初始化任务
 * 
 * 实现步骤：
 * 1. 接收任务相关参数：调度器描述符、优先级和任务函数
 * 2. 根据实现类的策略存储任务到初始化任务队列
 * 3. 按优先级对任务进行排序
 * 4. 等待系统启动或下一次update调用时执行
 * 
 * 设计理念：
 * - 初始化任务通常用于系统启动时的一次性设置
 * - 通过调度器描述符跟踪任务归属，便于依赖管理
 * - 优先级机制确保初始化任务按正确顺序执行
 * - 延迟到下一帧执行避免初始化期间的并发问题
 * 
 * @param scheduleDescriptor 调度器描述符，用于标识任务的来源和归属
 * @param priority 任务优先级，决定多个任务之间的执行顺序
 * @param task 要执行的任务函数，接收当前帧的持续时间参数
 */
    fun addMainTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: SchedulePriority,
        task: (Duration) -> Unit
    )

    /**
 * 添加下一帧执行的任务
 * 
 * 实现步骤：
 * 1. 接收任务相关参数：调度器描述符、优先级和任务函数
 * 2. 将任务添加到工作任务队列
 * 3. 根据优先级进行排序，确保高优先级任务先执行
 * 4. 在下一帧的update调用中按顺序执行所有任务
 * 
 * 设计理念：
 * - 工作任务用于帧间同步的常规操作
 * - 延迟到下一帧执行避免同一帧内的状态一致性问题
 * - 优先级排序确保关键任务优先处理
 * - 适用于需要在主线程执行的非紧急操作
 * 
 * @param scheduleDescriptor 调度器描述符，用于标识任务的来源和归属
 * @param priority 任务优先级，决定多个任务之间的执行顺序
 * @param task 要执行的任务函数，接收当前帧的持续时间参数
 */
    fun addWorkTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: SchedulePriority,
        task: (Duration) -> Unit
    )

    /**
 * 添加延迟指定时间后执行的任务
 * 
 * 实现步骤：
 * 1. 接收任务相关参数：调度器描述符、优先级、延迟时间和任务函数
 * 2. 创建带剩余时间计数器的延迟任务
 * 3. 将任务添加到延迟任务队列
 * 4. 在每次update调用时更新剩余时间
 * 5. 当剩余时间小于等于零时，将任务提升到执行队列
 * 
 * 设计理念：
 * - 支持精确的时间延迟控制，满足定时操作需求
 * - 优先级机制确保同时到期的任务按重要性执行
 * - 基于持续时间的延迟计算，适应不同的帧率
 * - 适用于定时器、动画控制等时间相关操作
 * 
 * @param scheduleDescriptor 调度器描述符，用于标识任务的来源和归属
 * @param priority 任务优先级，决定多个任务之间的执行顺序
 * @param delay 延迟时间，指定任务将在多少时间后执行
 * @param task 要执行的任务函数，接收当前帧的持续时间参数
 */
    fun addDelayFrameTask(
        scheduleDescriptor: ScheduleDescriptor,
        priority: SchedulePriority,
        delay: Duration,
        task: (Duration) -> Unit
    )

    /**
 * 更新调度器状态，执行所有到期的任务
 * 
 * 实现步骤：
 * 1. 接收当前帧的时间增量delta
 * 2. 更新所有延迟任务的剩余时间（减去delta）
 * 3. 检查并提取所有到期的延迟任务
 * 4. 合并初始化任务、工作任务和到期的延迟任务
 * 5. 按优先级对合并后的任务队列进行排序
 * 6. 按顺序执行所有任务，传入当前帧的delta参数
 * 7. 清理已执行的任务队列
 * 
 * 设计理念：
 * - 帧同步的任务执行机制，确保任务按正确的时间顺序执行
 * - 统一的更新入口，简化主循环集成
 * - 优先级排序确保任务执行的正确性
 * - 支持在同一帧中处理多种任务类型
 * - 适用于游戏循环、渲染管道和系统更新
 * 
 * @param delta 当前帧与上一帧之间的时间间隔
 */
    fun update(delta: Duration)
}