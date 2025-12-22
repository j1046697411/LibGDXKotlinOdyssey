package cn.jzl.sect.ecs.technique

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.MemberRole

/**
 * 功法品级
 */
enum class TechniqueGrade {
    MORTAL,      // 凡级
    YELLOW,      // 黄级
    PROFOUND,    // 玄级
    EARTH,       // 地级
    HEAVEN       // 天级
}

/**
 * 功法类型
 */
enum class TechniqueType {
    CULTIVATION, // 修炼功法
    COMBAT,      // 战斗技能
    AUXILIARY    // 辅助技能
}

/**
 * 功法学习要求
 */
data class TechniqueRequirement(
    val contributionCost: Int = 0,             // 消耗贡献度
    val minLevel: Long = 1L,                   // 最低等级要求
    val allowedRoles: Set<MemberRole> = setOf(
        MemberRole.LEADER,
        MemberRole.ELDER,
        MemberRole.INNER_DISCIPLE,
        MemberRole.OUTER_DISCIPLE
    ),
    val prerequisiteTechniques: Set<Entity> = emptySet() // 前置功法
)

/**
 * 功法效果
 */
data class TechniqueEffect(
    val attributeModifiers: Map<String, Float> = emptyMap(),  // 属性加成
    val passiveEffects: Set<PassiveEffect> = emptySet()       // 被动效果
)

/**
 * 被动效果
 */
sealed class PassiveEffect {
    /**
     * 修炼速度加成
     */
    data class CultivationSpeedBonus(val multiplier: Float) : PassiveEffect()

    /**
     * 战斗伤害加成
     */
    data class DamageBonus(val multiplier: Float) : PassiveEffect()

    /**
     * 防御加成
     */
    data class DefenseBonus(val multiplier: Float) : PassiveEffect()

    /**
     * 生命恢复
     */
    data class HealthRegeneration(val amountPerSecond: Float) : PassiveEffect()

    /**
     * 资源获取加成
     */
    data class ResourceGatheringBonus(val multiplier: Float) : PassiveEffect()
}

/**
 * 功法标签组件
 */
sealed class Technique

/**
 * 功法品级组件
 */
@JvmInline
value class TechniqueGradeComponent(val grade: TechniqueGrade)

/**
 * 功法类型组件
 */
@JvmInline
value class TechniqueTypeComponent(val type: TechniqueType)

