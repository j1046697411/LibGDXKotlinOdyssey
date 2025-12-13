package cn.jzl.sect.ecs.welfare

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.MemberRole
import kotlin.time.Duration

/**
 * 福利领取错误
 */
sealed class WelfareClaimError {
    /**
     * 福利规则不存在
     */
    data object WelfareNotFound : WelfareClaimError()

    /**
     * 冷却中
     */
    data class OnCooldown(
        val remainingTime: Duration
    ) : WelfareClaimError()

    /**
     * 角色不允许领取
     */
    data class RoleNotAllowed(
        val role: MemberRole
    ) : WelfareClaimError()

    /**
     * 不是宗门成员
     */
    data object NotSectMember : WelfareClaimError()

    /**
     * 该角色没有配置奖励
     */
    data class NoRewardForRole(
        val role: MemberRole
    ) : WelfareClaimError()
}

/**
 * 福利领取事件
 */
data class OnWelfareClaimed(
    val welfare: Entity,
    val member: Entity,
    val reward: RoleWelfareReward
)

