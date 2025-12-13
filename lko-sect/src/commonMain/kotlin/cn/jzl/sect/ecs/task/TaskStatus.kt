package cn.jzl.sect.ecs.task

/**
 * 任务状态
 */
enum class TaskStatus {
    AVAILABLE,   // 可领取
    IN_PROGRESS, // 进行中
    COMPLETED,   // 已完成
    FAILED,      // 已失败（超时等）
    CANCELLED    // 已取消
}

