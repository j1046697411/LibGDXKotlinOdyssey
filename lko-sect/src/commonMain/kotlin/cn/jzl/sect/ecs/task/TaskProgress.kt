package cn.jzl.sect.ecs.task

import cn.jzl.ecs.Entity

/**
 * 任务进度关系数据
 * 记录弟子领取任务的进度信息
 */
data class TaskProgress(
    val disciple: Entity,                                // 领取者
    val status: TaskStatus,                              // 当前状态
    val startTime: Long,                                 // 领取时间戳
    val submittedItems: Map<Entity, Int> = emptyMap(),   // 已提交物品
    val killedCount: Int = 0,                            // 已击杀数量
    val cultivationDuration: Long = 0L                   // 已修炼时长（毫秒）
)

