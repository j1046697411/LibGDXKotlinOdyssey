# API 合同: 功法服务 (TechniqueService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/technique/`

## 概述

TechniqueService 提供功法的创建、学习要求验证和弟子已学功法管理功能。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.technique

class TechniqueService(world: World) : EntityRelationContext(world) {

    // ==================== 功法创建 ====================
    
    /**
     * 创建功法预制体
     * @param named 功法名称
     * @param description 功法描述
     * @param grade 功法品阶
     * @param requirement 学习要求
     * @param effect 功法效果
     * @param block 额外配置
     * @return 功法实体
     */
    @ECSDsl
    fun createTechnique(
        named: Named,
        description: Description,
        grade: TechniqueGrade,
        requirement: TechniqueRequirement,
        effect: TechniqueEffect = TechniqueEffect(),
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity
    
    /**
     * 将功法添加到藏经阁
     * @param technique 功法实体
     * @param library 藏经阁实体
     * @throws IllegalArgumentException 如果不是功法实体
     * @throws IllegalArgumentException 如果不是藏经阁实体
     */
    fun addToLibrary(technique: Entity, library: Entity)

    // ==================== 功法学习 ====================
    
    /**
     * 检查弟子是否可学习功法
     * @param technique 功法实体
     * @param disciple 弟子实体
     * @param sect 宗门实体 (用于检查贡献度)
     * @return 可学习返回 null，否则返回不可学习原因
     */
    fun canLearn(technique: Entity, disciple: Entity, sect: Entity): TechniqueLearnError?
    
    /**
     * 弟子学习功法
     * @param technique 功法实体
     * @param disciple 弟子实体
     * @param sect 宗门实体 (用于扣除贡献度)
     * @throws IllegalStateException 如果贡献度不足
     * @throws IllegalStateException 如果等级不足
     * @throws IllegalStateException 如果前置功法未学习
     * @throws IllegalStateException 如果已学习该功法
     */
    fun learn(technique: Entity, disciple: Entity, sect: Entity)

    // ==================== 查询 ====================
    
    /**
     * 获取藏经阁中的所有功法
     * @param library 藏经阁实体
     * @return 功法实体序列
     */
    fun getTechniquesByLibrary(library: Entity): Sequence<Entity>
    
    /**
     * 获取弟子已学的所有功法
     * @param disciple 弟子实体
     * @return 功法实体和学习数据的序列
     */
    fun getLearnedTechniques(disciple: Entity): Sequence<Pair<Entity, TechniqueLearned>>
    
    /**
     * 检查弟子是否已学习某功法
     * @param disciple 弟子实体
     * @param technique 功法实体
     * @return 是否已学习
     */
    fun hasLearned(disciple: Entity, technique: Entity): Boolean
    
    /**
     * 获取功法的学习要求
     * @param technique 功法实体
     * @return 学习要求
     */
    fun getRequirement(technique: Entity): TechniqueRequirement
    
    /**
     * 获取功法的效果
     * @param technique 功法实体
     * @return 功法效果
     */
    fun getEffect(technique: Entity): TechniqueEffect
    
    /**
     * 获取弟子的功法属性加成总和
     * @param disciple 弟子实体
     * @return 属性加成 (属性名 -> 加成值)
     */
    fun getTotalAttributeModifiers(disciple: Entity): Map<Named, Long>
    
    /**
     * 获取弟子的被动效果列表
     * @param disciple 弟子实体
     * @return 被动效果集合
     */
    fun getTotalPassiveEffects(disciple: Entity): Set<PassiveEffect>
}
```

---

## 数据类型

```kotlin
enum class TechniqueGrade {
    COMMON,      // 凡品
    YELLOW,      // 黄品
    PROFOUND,    // 玄品
    EARTH,       // 地品
    HEAVEN       // 天品
}

data class TechniqueRequirement(
    val contribution: Int,                             // 消耗贡献度
    val minLevel: Long,                                // 最低等级
    val prerequisites: Set<Entity> = emptySet(),       // 前置功法
    val allowedRoles: Set<MemberRole> = MemberRole.entries.toSet()
)

data class TechniqueEffect(
    val attributeModifiers: Map<Named, Long> = emptyMap(),  // 属性加成
    val passiveEffects: Set<PassiveEffect> = emptySet()     // 被动效果
)

sealed class PassiveEffect {
    data class ExpBonus(val percentage: Float) : PassiveEffect()
    data class GatheringBonus(val percentage: Float) : PassiveEffect()
    data class CombatBonus(val percentage: Float) : PassiveEffect()
    data class CultivationBonus(val percentage: Float) : PassiveEffect()
}

data class TechniqueLearned(
    val technique: Entity,      // 功法实体
    val learnTime: Long,        // 学习时间戳
    val proficiency: Int = 0    // 熟练度（0-100）
)

sealed class TechniqueLearnError {
    object TechniqueNotFound : TechniqueLearnError()
    object AlreadyLearned : TechniqueLearnError()
    data class InsufficientContribution(val required: Int, val actual: Int) : TechniqueLearnError()
    data class LevelTooLow(val required: Long, val actual: Long) : TechniqueLearnError()
    data class MissingPrerequisites(val missing: Set<Entity>) : TechniqueLearnError()
    data class RoleNotAllowed(val role: MemberRole) : TechniqueLearnError()
}
```

---

## 事件发射

| 事件 | 触发时机 |
|------|----------|
| `OnTechniqueLearned` | `learn` 成功后 |

---

## 使用示例

```kotlin
// 创建功法
val technique = techniqueService.createTechnique(
    named = Named("基础剑法"),
    description = Description("入门级剑法，提升攻击力"),
    grade = TechniqueGrade.COMMON,
    requirement = TechniqueRequirement(
        contribution = 100,
        minLevel = 1
    ),
    effect = TechniqueEffect(
        attributeModifiers = mapOf(Named("Attack") to 10L),
        passiveEffects = setOf(PassiveEffect.CombatBonus(0.05f))
    )
) {}

// 添加到藏经阁
techniqueService.addToLibrary(technique, libraryEntity)

// 检查学习条件
val error = techniqueService.canLearn(technique, discipleEntity, sectEntity)
if (error == null) {
    // 学习功法
    techniqueService.learn(technique, discipleEntity, sectEntity)
    println("学习成功！")
}

// 查询已学功法
val learned = techniqueService.getLearnedTechniques(discipleEntity)
learned.forEach { (tech, data) ->
    println("功法: ${tech}, 熟练度: ${data.proficiency}")
}

// 获取总属性加成
val modifiers = techniqueService.getTotalAttributeModifiers(discipleEntity)
println("攻击力加成: ${modifiers[Named("Attack")]}")
```

