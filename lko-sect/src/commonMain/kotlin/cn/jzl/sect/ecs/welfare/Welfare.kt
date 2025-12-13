package cn.jzl.sect.ecs.welfare

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.MemberRole
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * 福利规则标签组件
 */
sealed class WelfareRule

/**
 * 福利配置
 */
data class WelfareConfig(
    val name: String,                                      // 福利名称
    val cooldown: Duration = 24.hours,                     // 冷却时间
    val allowedRoles: Set<MemberRole> = setOf(
        MemberRole.LEADER,
        MemberRole.ELDER,
        MemberRole.INNER_DISCIPLE,
        MemberRole.OUTER_DISCIPLE
    )
)

/**
 * 福利奖励配置
 * 根据角色定义不同的奖励
 */
data class WelfareReward(
    val roleRewards: Map<MemberRole, RoleWelfareReward>
) {
    /**
     * 获取指定角色的奖励
     */
    fun getRewardFor(role: MemberRole): RoleWelfareReward? = roleRewards[role]
}

/**
 * 角色福利奖励
 */
data class RoleWelfareReward(
    val contribution: Int = 0,                // 贡献度奖励
    val experience: Long = 0L,                // 经验奖励
    val items: Map<Entity, Int> = emptyMap()  // 物品奖励
)

/**
 * 福利领取状态
 * 作为成员与福利规则之间的关系数据
 */
data class WelfareStatus(
    val lastClaimTime: Long,     // 上次领取时间
    val totalClaimed: Int = 1    // 总领取次数
)

/**
 * 福利组件
 */
@JvmInline
value class WelfareConfigComponent(val config: WelfareConfig)

/**
 * 福利奖励组件
 */
@JvmInline
value class WelfareRewardComponent(val reward: WelfareReward)

