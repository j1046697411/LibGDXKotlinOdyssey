package cn.jzl.sect.ecs.sect

import cn.jzl.sect.ecs.MemberRole

/**
 * 成员配额配置
 * 定义宗门各角色的最大人数限制
 */
data class MemberQuota(
    val maxElders: Int = 5,
    val maxInnerDisciples: Int = 20,
    val maxOuterDisciples: Int = 50,
    val additionalPerLevel: Int = 10  // 每升一级增加的外门配额
) {
    /**
     * 获取指定角色的最大配额
     */
    fun getMaxFor(role: MemberRole, sectLevel: Long = 1L): Int = when (role) {
        MemberRole.LEADER -> 1
        MemberRole.ELDER -> maxElders
        MemberRole.INNER_DISCIPLE -> maxInnerDisciples
        MemberRole.OUTER_DISCIPLE -> maxOuterDisciples + (additionalPerLevel * (sectLevel - 1)).toInt()
    }
}

