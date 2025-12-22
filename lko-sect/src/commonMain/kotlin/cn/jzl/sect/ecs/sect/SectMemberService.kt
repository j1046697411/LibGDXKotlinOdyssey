package cn.jzl.sect.ecs.sect

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.sect.ecs.*

/**
 * 宗门成员服务
 * 提供成员权限检查、晋升机制和配额管理
 */
class SectMemberService(world: World) : EntityRelationContext(world) {

    private val sectService by world.di.instance<SectService>()
    private val levelingService by world.di.instance<LevelingService>()
    private val attributeService by world.di.instance<AttributeService>()

    /**
     * 检查成员是否有指定权限
     */
    fun hasPermission(sect: Entity, member: Entity, permission: Permission): Boolean {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val memberData = sectService.getMemberData(sect, member) ?: return false
        return Permission.hasPermission(memberData.role, permission)
    }

    /**
     * 获取成员的所有权限
     */
    fun getPermissions(sect: Entity, member: Entity): Set<Permission> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val memberData = sectService.getMemberData(sect, member) ?: return emptySet()
        return Permission.getPermissionsFor(memberData.role)
    }

    /**
     * 检查是否可以晋升成员
     */
    fun canPromote(
        sect: Entity,
        member: Entity,
        targetRole: MemberRole
    ): PromotionError? {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val memberData = sectService.getMemberData(sect, member)
            ?: return PromotionError.NotSectMember

        // 不能降级（通过此方法）
        if (targetRole.ordinal >= memberData.role.ordinal) {
            return PromotionError.InvalidTargetRole(memberData.role, targetRole)
        }

        // 检查配额
        val quota = sect.getComponent<MemberQuota?>() ?: MemberQuota()
        val sectLevel = getSectLevel(sect)
        val currentCount = getMemberCountByRole(sect, targetRole)
        val maxCount = quota.getMaxFor(targetRole, sectLevel)

        if (currentCount >= maxCount) {
            return PromotionError.QuotaExceeded(targetRole, currentCount, maxCount)
        }

        // 检查贡献度要求
        val requiredContribution = getRequiredContribution(targetRole)
        if (memberData.contribution.value < requiredContribution) {
            return PromotionError.InsufficientContribution(
                requiredContribution,
                memberData.contribution.value
            )
        }

        // 检查等级要求
        val requiredLevel = getRequiredLevel(targetRole)
        val memberLevel = getMemberLevel(member)
        if (memberLevel < requiredLevel) {
            return PromotionError.LevelTooLow(requiredLevel, memberLevel)
        }

        return null
    }

    /**
     * 晋升成员
     */
    fun promote(
        sect: Entity,
        member: Entity,
        targetRole: MemberRole,
        approver: Entity
    ): PromotionError? {
        // 检查审批人权限
        if (!hasPermission(sect, approver, Permission.ManageMembers)) {
            return PromotionError.ApproverNoPermission
        }

        val error = canPromote(sect, member, targetRole)
        if (error != null) return error

        sectService.changeMemberRole(sect, member, targetRole)
        return null
    }

    /**
     * 获取宗门指定角色的成员数量
     */
    fun getMemberCountByRole(sect: Entity, role: MemberRole): Int {
        return sectService.getMembers(sect).count { (_, data) -> data.role == role }
    }

    /**
     * 获取宗门各角色的配额使用情况
     */
    fun getQuotaUsage(sect: Entity): Map<MemberRole, Pair<Int, Int>> {
        require(sect.hasTag<Sect>()) { "实体${sect.id}不是宗门" }

        val quota = sect.getComponent<MemberQuota?>() ?: MemberQuota()
        val sectLevel = getSectLevel(sect)

        return MemberRole.entries.associateWith { role ->
            val current = getMemberCountByRole(sect, role)
            val max = quota.getMaxFor(role, sectLevel)
            current to max
        }
    }

    /**
     * 检查是否可以添加新成员
     */
    fun canAddMember(sect: Entity, role: MemberRole): Boolean {
        val quota = sect.getComponent<MemberQuota?>() ?: MemberQuota()
        val sectLevel = getSectLevel(sect)
        val currentCount = getMemberCountByRole(sect, role)
        val maxCount = quota.getMaxFor(role, sectLevel)
        return currentCount < maxCount
    }

    /**
     * 获取宗门等级
     */
    private fun getSectLevel(sect: Entity): Long {
        val levelAttribute = attributeService.attribute(ATTRIBUTE_LEVEL)
        return sect.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    /**
     * 获取成员等级
     */
    private fun getMemberLevel(member: Entity): Long {
        val levelAttribute = attributeService.attribute(ATTRIBUTE_LEVEL)
        return member.getRelation<AttributeValue?>(levelAttribute)?.value ?: 1L
    }

    /**
     * 获取晋升到指定角色所需的贡献度
     */
    private fun getRequiredContribution(role: MemberRole): Int = when (role) {
        MemberRole.LEADER -> 10000
        MemberRole.ELDER -> 5000
        MemberRole.INNER_DISCIPLE -> 1000
        MemberRole.OUTER_DISCIPLE -> 0
    }

    /**
     * 获取晋升到指定角色所需的等级
     */
    private fun getRequiredLevel(role: MemberRole): Long = when (role) {
        MemberRole.LEADER -> 50L
        MemberRole.ELDER -> 30L
        MemberRole.INNER_DISCIPLE -> 10L
        MemberRole.OUTER_DISCIPLE -> 1L
    }

    companion object {
        private val ATTRIBUTE_LEVEL = cn.jzl.sect.ecs.core.Named("level")
    }
}

/**
 * 晋升错误
 */
sealed class PromotionError {
    data object NotSectMember : PromotionError()
    data object ApproverNoPermission : PromotionError()
    data class InvalidTargetRole(val current: MemberRole, val target: MemberRole) : PromotionError()
    data class QuotaExceeded(val role: MemberRole, val current: Int, val max: Int) : PromotionError()
    data class InsufficientContribution(val required: Int, val current: Int) : PromotionError()
    data class LevelTooLow(val required: Long, val current: Long) : PromotionError()
}

