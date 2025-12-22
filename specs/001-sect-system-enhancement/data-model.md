# 数据模型: 宗门系统完善

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**状态**: 已完成

## 概述

本文档定义宗门系统完善功能的所有数据模型，包括实体、组件、关系和事件。

---

## 实体定义

### 1. 宗门 (Sect) - 已存在，需扩展

宗门是游戏中的组织单位，拥有成员、建筑、资源和任务。

**标签组件**: `sealed class Sect`

**数据组件**:
| 组件 | 类型 | 描述 | 状态 |
|------|------|------|------|
| Named | `@JvmInline value class` | 宗门名称 | 已存在 |
| SectReputation | `@JvmInline value class` | 宗门声望 | 已存在 |
| Money | `@JvmInline value class` | 宗门资金（灵石） | 已存在 |
| MemberQuota | `data class` | 成员配额配置 | 新增 |
| SectConfig | `data class` | 宗门配置 | 新增 |

**关系**:
| 关系类型 | 目标 | 数据 | 描述 |
|----------|------|------|------|
| MemberData | Character | MemberData | 成员关系（已存在） |
| AttributeValue | Attribute | AttributeValue | 等级/经验（已存在） |

---

### 2. 宗门成员 (Member) - 扩展 MemberData

宗门成员通过 MemberData 关系与宗门关联。

**关系数据组件**:
```kotlin
data class MemberData(
    val player: Entity,              // 成员实体
    val role: MemberRole,            // 成员角色
    val contribution: Contribution,  // 贡献度
    val joinTime: Long = 0L          // 加入时间（新增）
)
```

**成员角色枚举**（扩展）:
```kotlin
enum class MemberRole {
    LEADER,          // 宗主：全权限
    ELDER,           // 长老：管理权限
    INNER_DISCIPLE,  // 内门弟子：任务发布权限（新增）
    OUTER_DISCIPLE   // 外门弟子：仅参与权限（重命名）
}
```

**成员配额组件**:
```kotlin
data class MemberQuota(
    val maxElders: Int = 5,
    val maxInnerDisciples: Int = 20,
    val maxOuterDisciples: Int = 50,
    val additionalPerLevel: Int = 10  // 每升一级增加的外门配额
)
```

---

### 3. 建筑 (Building) - 已存在，需扩展

建筑是宗门拥有的功能性设施。

**标签组件**: `sealed class Building`

**建筑类型标签**:
| 标签 | 描述 | 状态 |
|------|------|------|
| AlchemyHall | 炼丹房 | 已存在 |
| Library | 藏经阁 | 已存在 |
| TrainingHall | 练功房 | 新增 |
| TreasureVault | 藏宝阁 | 新增 |

**数据组件**:
| 组件 | 类型 | 描述 | 状态 |
|------|------|------|------|
| Named | `@JvmInline value class` | 建筑名称 | 已存在 |
| BuildingBaseCost | `data class` | 升级基础消耗 | 新增 |

**关系**:
| 关系类型 | 目标 | 数据 | 描述 |
|----------|------|------|------|
| OwnedBy | Sect | - | 所属宗门 |
| AttributeValue | efficiency | AttributeValue | 效率属性 |
| AttributeValue | capacity | AttributeValue | 容量属性 |
| AttributeValue | level | AttributeValue | 等级（LevelingService） |

**建筑基础消耗组件**:
```kotlin
data class BuildingBaseCost(
    val resources: Map<Entity, Int>  // 物品预制体 -> 基础数量
)
```

---

### 4. 宗门任务 (SectTask) - 新增

宗门发布的任务。

**标签组件**: `sealed class SectTask`

**数据组件**:
| 组件 | 类型 | 描述 |
|------|------|------|
| Named | `@JvmInline value class` | 任务名称 |
| Description | `@JvmInline value class` | 任务描述 |
| TaskTypeComponent | `@JvmInline value class` | 任务类型 |
| TaskRequirement | `data class` | 任务完成条件 |
| TaskRewardConfig | `data class` | 任务奖励配置 |
| TaskLimit | `data class` | 任务限制（人数、时间） |

**关系**:
| 关系类型 | 目标 | 数据 | 描述 |
|----------|------|------|------|
| OwnedBy | Sect | - | 所属宗门 |
| TaskProgress | Character | TaskProgress | 任务进度（弟子领取） |
| Countdown | - | Countdown | 任务时限 |

**任务类型枚举**:
```kotlin
enum class TaskType {
    GATHERING,   // 采集任务
    COMBAT,      // 战斗任务
    CULTIVATION, // 修炼任务
    EXPLORATION  // 探索任务
}
```

**任务状态枚举**:
```kotlin
enum class TaskStatus {
    AVAILABLE,   // 可领取
    IN_PROGRESS, // 进行中
    COMPLETED,   // 已完成
    FAILED,      // 已失败（超时等）
    CANCELLED    // 已取消
}
```

**任务完成条件组件**:
```kotlin
data class TaskRequirement(
    val requiredItems: Map<Entity, Int> = emptyMap(),  // 需提交的物品
    val requiredKills: Int = 0,                         // 需击杀数量
    val requiredDuration: Duration? = null              // 需修炼时长
)
```

**任务奖励配置组件**:
```kotlin
data class TaskRewardConfig(
    val baseContribution: Int,                    // 基础贡献度
    val baseExperience: Long,                     // 基础经验
    val items: Map<Entity, Int> = emptyMap(),     // 物品奖励
    val bonusFormula: RewardBonusFormula? = null  // 奖励加成公式
)

fun interface RewardBonusFormula {
    fun calculate(
        completionTime: Duration,   // 完成耗时
        discipleLevel: Long,        // 弟子等级
        quality: Float              // 完成质量（0-1）
    ): Float  // 返回加成倍率
}
```

**任务限制组件**:
```kotlin
data class TaskLimit(
    val maxAcceptors: Int = 1,              // 最大领取人数
    val timeLimit: Duration? = null,        // 时间限制
    val minLevel: Long = 1,                 // 最低等级要求
    val allowedRoles: Set<MemberRole> = setOf(
        MemberRole.ELDER,
        MemberRole.INNER_DISCIPLE,
        MemberRole.OUTER_DISCIPLE
    )
)
```

**任务进度关系数据**:
```kotlin
data class TaskProgress(
    val disciple: Entity,                            // 领取者
    val status: TaskStatus,                          // 当前状态
    val startTime: Long,                             // 领取时间戳
    val submittedItems: Map<Entity, Int> = emptyMap() // 已提交物品
)
```

---

### 5. 功法 (Technique) - 新增

可学习的修炼方法或技能。

**标签组件**: `sealed class Technique`

**数据组件**:
| 组件 | 类型 | 描述 |
|------|------|------|
| Named | `@JvmInline value class` | 功法名称 |
| Description | `@JvmInline value class` | 功法描述 |
| TechniqueRequirement | `data class` | 学习要求 |
| TechniqueEffect | `data class` | 功法效果 |
| TechniqueRank | `@JvmInline value class` | 功法品阶 |

**关系**:
| 关系类型 | 目标 | 数据 | 描述 |
|----------|------|------|------|
| OwnedBy | Library | - | 存放于藏经阁 |

**功法品阶枚举**:
```kotlin
enum class TechniqueGrade {
    COMMON,      // 凡品
    YELLOW,      // 黄品
    PROFOUND,    // 玄品
    EARTH,       // 地品
    HEAVEN       // 天品
}
```

**功法学习要求组件**:
```kotlin
data class TechniqueRequirement(
    val contribution: Int,                             // 消耗贡献度
    val minLevel: Long,                                // 最低等级
    val prerequisites: Set<Entity> = emptySet(),       // 前置功法
    val allowedRoles: Set<MemberRole> = MemberRole.entries.toSet()
)
```

**功法效果组件**:
```kotlin
data class TechniqueEffect(
    val attributeModifiers: Map<Named, Long> = emptyMap(),  // 属性加成
    val passiveEffects: Set<PassiveEffect> = emptySet()     // 被动效果
)

sealed class PassiveEffect {
    data class ExpBonus(val percentage: Float) : PassiveEffect()
    data class GatheringBonus(val percentage: Float) : PassiveEffect()
    data class CombatBonus(val percentage: Float) : PassiveEffect()
}
```

**功法学习关系数据**（弟子 -> 功法）:
```kotlin
data class TechniqueLearned(
    val technique: Entity,      // 功法实体
    val learnTime: Long,        // 学习时间戳
    val proficiency: Int = 0    // 熟练度（0-100）
)
```

---

### 6. 福利规则 (WelfareRule) - 新增

福利发放规则定义。

**标签组件**: `sealed class WelfareRule`

**数据组件**:
| 组件 | 类型 | 描述 |
|------|------|------|
| Named | `@JvmInline value class` | 福利名称 |
| Description | `@JvmInline value class` | 福利描述 |
| WelfareConfig | `data class` | 福利配置 |

**关系**:
| 关系类型 | 目标 | 数据 | 描述 |
|----------|------|------|------|
| OwnedBy | Sect | - | 所属宗门 |

**福利配置组件**:
```kotlin
data class WelfareConfig(
    val period: Duration,                              // 发放周期
    val rewardsByRole: Map<MemberRole, WelfareReward>  // 按角色的奖励
)

data class WelfareReward(
    val money: Int = 0,                        // 灵石奖励
    val items: Map<Entity, Int> = emptyMap(),  // 物品奖励
    val contribution: Int = 0                  // 贡献度奖励
)
```

**福利领取状态关系**（成员 -> 福利规则）:
```kotlin
data class WelfareStatus(
    val ruleEntity: Entity,     // 福利规则实体
    val lastClaimTime: Long,    // 上次领取时间戳
    val claimCount: Int = 0     // 累计领取次数
)
```

---

## 事件定义

### 现有事件（复用）

| 事件 | 数据 | 触发时机 |
|------|------|----------|
| OnUpgradeEvent | oldLevel, newLevel | 实体升级时 |
| OnCountdownComplete | - | 倒计时完成时 |
| OnCreatedCharacter | - | 角色创建时 |

### 新增事件

| 事件 | 数据 | 触发时机 |
|------|------|----------|
| OnTaskAccepted | task, disciple | 弟子领取任务时 |
| OnTaskCompleted | task, disciple, reward | 任务完成时 |
| OnTaskFailed | task, disciple, reason | 任务失败时 |
| OnTechniqueLearned | technique, disciple | 功法学习成功时 |
| OnMemberPromoted | member, oldRole, newRole | 成员晋升时 |
| OnWelfareClaimed | member, welfare, reward | 福利领取时 |
| OnSectLevelUp | sect, oldLevel, newLevel, unlocks | 宗门升级时 |
| OnBuildingUpgraded | building, oldLevel, newLevel | 建筑升级时 |

**事件组件定义**:
```kotlin
// 任务事件
data class OnTaskAccepted(val task: Entity, val disciple: Entity)
data class OnTaskCompleted(val task: Entity, val disciple: Entity, val reward: TaskReward)
data class OnTaskFailed(val task: Entity, val disciple: Entity, val reason: TaskFailReason)

enum class TaskFailReason {
    TIMEOUT,      // 超时
    CANCELLED,    // 主动取消
    REQUIREMENTS  // 条件不足
}

// 功法事件
data class OnTechniqueLearned(val technique: Entity, val disciple: Entity)

// 成员事件
data class OnMemberPromoted(val member: Entity, val oldRole: MemberRole, val newRole: MemberRole)

// 福利事件
data class OnWelfareClaimed(val member: Entity, val welfare: Entity, val reward: WelfareReward)

// 宗门升级事件
data class OnSectLevelUp(
    val sect: Entity,
    val oldLevel: Long,
    val newLevel: Long,
    val unlocks: SectLevelUnlocks
)

data class SectLevelUnlocks(
    val newBuildingTypes: Set<String>,   // 解锁的建筑类型
    val memberCapacityIncrease: Int,     // 成员容量增加
    val buildingCapacityIncrease: Int    // 建筑容量增加
)

// 建筑升级事件
data class OnBuildingUpgraded(val building: Entity, val oldLevel: Long, val newLevel: Long)
```

---

## 属性定义

### 现有属性（复用）

| 属性名 | 描述 | 使用场景 |
|--------|------|----------|
| Level | 等级 | 角色、宗门、建筑 |
| Experience | 经验值 | 角色、宗门、建筑 |

### 新增属性

| 属性名 | 描述 | 使用场景 |
|--------|------|----------|
| Efficiency | 效率 | 建筑（炼丹成功率、修炼速度等） |
| Capacity | 容量 | 建筑（藏经阁藏书量、仓库容量） |
| Health | 生命值 | 角色战斗属性 |
| Attack | 攻击力 | 角色战斗属性 |
| Defense | 防御力 | 角色战斗属性 |

**属性注册**:
```kotlin
// 在 buildingAddon 中注册
attributes {
    register(Named("Efficiency"), Description("建筑效率"))
    register(Named("Capacity"), Description("建筑容量"))
}

// 在 characterAddon 中注册（如需要）
attributes {
    register(Named("Health"), Description("生命值"))
    register(Named("Attack"), Description("攻击力"))
    register(Named("Defense"), Description("防御力"))
}
```

---

## 关系类型汇总

### 现有关系

| 关系类型 | 源 -> 目标 | 数据 | 描述 |
|----------|------------|------|------|
| OwnedBy | * -> Owner | - | 通用所有权关系 |
| MemberData | Sect -> Character | MemberData | 宗门成员关系 |
| AttributeValue | Entity -> Attribute | AttributeValue | 属性值关系 |
| Countdown | Entity -> * | Countdown | 倒计时关系 |

### 新增关系

| 关系类型 | 源 -> 目标 | 数据 | 描述 |
|----------|------------|------|------|
| TaskProgress | SectTask -> Character | TaskProgress | 任务领取进度 |
| TechniqueLearned | Character -> Technique | TechniqueLearned | 已学功法 |
| WelfareStatus | Character -> WelfareRule | WelfareStatus | 福利领取状态 |

---

## 验证规则

### 成员管理验证

1. **角色配额验证**：添加成员前检查对应角色是否超过配额
2. **权限验证**：操作前检查操作者是否有对应权限
3. **晋升条件验证**：晋升前检查贡献度、等级、考核任务

### 任务验证

1. **并发任务上限**：弟子领取任务前检查当前进行中任务数（默认≤5）
2. **任务人数上限**：领取前检查任务当前领取人数
3. **等级要求**：领取前检查弟子等级
4. **角色权限**：领取前检查弟子角色是否允许

### 建筑升级验证

1. **资源验证**：升级前检查宗门资源是否足够
2. **宗门等级验证**：升级前检查宗门等级是否满足要求
3. **建筑类型解锁**：创建建筑前检查是否已解锁该类型

### 功法学习验证

1. **贡献度验证**：学习前检查弟子贡献度是否足够
2. **等级验证**：学习前检查弟子等级是否满足
3. **前置功法验证**：学习前检查是否已学习前置功法
4. **重复学习验证**：检查是否已学习该功法

### 福利领取验证

1. **周期验证**：领取前检查是否已过冷却时间
2. **角色验证**：检查福利规则是否包含该角色的奖励

