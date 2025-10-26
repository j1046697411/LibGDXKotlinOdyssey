package cn.jzl.ecs.v2

// 优先级枚举（数值越小优先级越高）
enum class SchedulePriority(val priority: Int) {
    HIGHEST(0),    // 最高优先级，用于紧急任务
    HIGH(1),       // 高优先级，用于重要任务
    NORMAL(2),     // 普通优先级，默认优先级
    LOW(3),        // 低优先级，用于后台任务
    LOWEST(4);     // 最低优先级，用于非关键任务,
}