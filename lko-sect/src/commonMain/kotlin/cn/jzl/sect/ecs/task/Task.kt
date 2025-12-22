package cn.jzl.sect.ecs.task

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.MemberRole
import kotlin.time.Duration

/**
 * 宗门任务标签组件
 */
sealed class SectTask

/**
 * 任务类型组件
 */
@JvmInline
value class TaskTypeComponent(val type: TaskType)

/**
 * 任务完成条件
 */
data class TaskRequirement(
    val requiredItems: Map<Entity, Int> = emptyMap(),   // 需提交的物品
    val requiredKills: Int = 0,                          // 需击杀数量
    val requiredDuration: Duration? = null               // 需修炼时长
)

/**
 * 奖励加成公式
 */
fun interface RewardBonusFormula {
    /**
     * 计算奖励加成倍率
     * @param completionTime 完成耗时
     * @param discipleLevel 弟子等级
     * @param quality 完成质量（0-1）
     * @return 加成倍率
     */
    fun calculate(
        completionTime: Duration,
        discipleLevel: Long,
        quality: Float
    ): Float
}

/**
 * 任务奖励配置
 */
data class TaskRewardConfig(
    val baseContribution: Int,                           // 基础贡献度
    val baseExperience: Long,                            // 基础经验
    val items: Map<Entity, Int> = emptyMap(),            // 物品奖励
    val bonusFormula: RewardBonusFormula? = null         // 奖励加成公式
)

/**
 * 任务限制
 */
data class TaskLimit(
    val maxAcceptors: Int = 1,                           // 最大领取人数
    val timeLimit: Duration? = null,                     // 时间限制
    val minLevel: Long = 1,                              // 最低等级要求
    val allowedRoles: Set<MemberRole> = setOf(
        MemberRole.ELDER,
        MemberRole.INNER_DISCIPLE,
        MemberRole.OUTER_DISCIPLE
    )
)

/**
 * 任务实际奖励（完成时计算）
 */
data class TaskReward(
    val contribution: Int,
    val experience: Long,
    val items: Map<Entity, Int>
)

/**
 * 任务失败原因
 */
enum class TaskFailReason {
    TIMEOUT,      // 超时
    CANCELLED,    // 主动取消
    REQUIREMENTS  // 条件不足
}

/**
 * 任务接受错误
 */
sealed class TaskAcceptError {
    data object TaskNotAvailable : TaskAcceptError()
    data object TaskFull : TaskAcceptError()
    data object AlreadyAccepted : TaskAcceptError()
    data class LevelTooLow(val required: Long, val current: Long) : TaskAcceptError()
    data class RoleNotAllowed(val role: MemberRole) : TaskAcceptError()
    data class TooManyActiveTasks(val current: Int, val max: Int) : TaskAcceptError()
    data object NotSectMember : TaskAcceptError()
}

/**
 * 任务提交错误
 */
sealed class TaskSubmitError {
    data object TaskNotInProgress : TaskSubmitError()
    data object NotTaskOwner : TaskSubmitError()
    data class InsufficientItems(val missing: Map<Entity, Int>) : TaskSubmitError()
    data class InsufficientKills(val required: Int, val current: Int) : TaskSubmitError()
    data class InsufficientDuration(val required: Duration, val current: Duration) : TaskSubmitError()
}

// 任务事件
data class OnTaskAccepted(val task: Entity, val disciple: Entity)
data class OnTaskCompleted(val task: Entity, val disciple: Entity, val reward: TaskReward)
data class OnTaskFailed(val task: Entity, val disciple: Entity, val reason: TaskFailReason)

