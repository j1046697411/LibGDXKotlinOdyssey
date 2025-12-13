package cn.jzl.sect.ecs.technique

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.MemberRole

/**
 * 功法学习错误
 */
sealed class TechniqueLearnError {
    /**
     * 功法不存在
     */
    data object TechniqueNotFound : TechniqueLearnError()

    /**
     * 藏经阁中没有此功法
     */
    data object NotInLibrary : TechniqueLearnError()

    /**
     * 贡献度不足
     */
    data class InsufficientContribution(
        val required: Int,
        val current: Int
    ) : TechniqueLearnError()

    /**
     * 等级不足
     */
    data class LevelTooLow(
        val required: Long,
        val current: Long
    ) : TechniqueLearnError()

    /**
     * 角色不允许学习
     */
    data class RoleNotAllowed(
        val role: MemberRole
    ) : TechniqueLearnError()

    /**
     * 已经学习过此功法
     */
    data object AlreadyLearned : TechniqueLearnError()

    /**
     * 缺少前置功法
     */
    data class MissingPrerequisites(
        val missing: Set<Entity>
    ) : TechniqueLearnError()

    /**
     * 不是宗门成员
     */
    data object NotSectMember : TechniqueLearnError()
}

