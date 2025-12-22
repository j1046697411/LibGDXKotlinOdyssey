# 研究文档: 宗门系统完善

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**状态**: 已完成

## 概述

本文档记录宗门系统完善功能的技术研究和设计决策。所有决策均基于项目章程原则：ECS 优先、服务复用优先、框架一致性、关系系统优先、属性系统优先。

---

## 研究任务 1: 宗门资源管理方案

### 问题

如何实现宗门资源（灵石、灵草、丹药等）的存储和管理？

### 研究发现

**现有方案分析**:

1. **InventoryService 现状**:
   - 已实现 `addItem(owner, itemPrefab, count)` - 添加物品
   - 已实现 `removeItem(owner, itemPrefab, count)` - 移除物品
   - 已实现 `getItemCount(owner, itemPrefab)` - 获取物品数量
   - 已实现 `hasEnoughItems(owner, itemPrefab, count)` - 检查物品是否足够
   - 物品通过 `OwnedBy` 关系关联到所有者

2. **物品系统特性**:
   - 支持可堆叠物品（Stackable 标签）
   - 物品有预制体（Prefab）概念
   - 物品可以有数量（Amount 组件）

### 决策

**Decision**: 复用 InventoryService 管理宗门资源

**Rationale**: 
- 宗门资源本质上是物品，灵石、灵草、丹药都可以定义为物品预制体
- InventoryService 已提供完整的物品增删改查能力
- 使用 `OwnedBy` 关系将物品关联到宗门实体即可
- 避免重复实现物品管理逻辑

**Alternatives considered**:
1. 新建 SectResourceService - 会重复 InventoryService 的大部分逻辑
2. 使用 Map 组件存储资源 - 不符合 ECS 优先原则

### 实现要点

```kotlin
// 宗门资源 = 物品属于宗门
// 存入资源
inventoryService.addItem(sectEntity, lingshiPrefab, 100)

// 取出资源
inventoryService.removeItem(sectEntity, lingshiPrefab, 50)

// 查询资源
val lingshiCount = inventoryService.getItemCount(sectEntity, lingshiPrefab)
```

---

## 研究任务 2: 任务系统设计

### 问题

如何设计任务系统以支持任务创建、领取、进度跟踪和奖励发放？

### 研究发现

**需求分析**:
- 任务分4类型：采集、战斗、修炼、探索
- 弟子可并发领取多个任务（上限5个）
- 任务有时间限制
- 奖励动态计算

**现有系统可复用**:
- Countdown 系统 - 用于任务时间限制
- InventoryService - 用于奖励物品发放
- 关系系统 - 用于任务-弟子关联

### 决策

**Decision**: 使用带数据关系 `TaskProgress` 管理任务领取

**Rationale**:
- 任务和弟子是多对多关系（一个任务可被多人领取，一个弟子可领取多个任务）
- 需要存储领取时间、完成状态等额外数据
- ECS 关系系统天然支持这种带数据的实体关联

**Alternatives considered**:
1. 中间表实体 - 增加复杂度，查询不便
2. 组件数组存储任务ID - 不符合关系系统优先原则

### 关键组件设计

```kotlin
// 任务类型
enum class TaskType {
    GATHERING,   // 采集
    COMBAT,      // 战斗
    CULTIVATION, // 修炼
    EXPLORATION  // 探索
}

// 任务状态
enum class TaskStatus {
    AVAILABLE,   // 可领取
    IN_PROGRESS, // 进行中
    COMPLETED,   // 已完成
    FAILED,      // 已失败
    CANCELLED    // 已取消
}

// 任务进度关系数据（任务 -> 弟子）
data class TaskProgress(
    val disciple: Entity,           // 领取者
    val status: TaskStatus,         // 当前状态
    val startTime: Long,            // 领取时间
    val completedItems: Map<Entity, Int> = emptyMap() // 已提交物品
)

// 任务奖励配置
data class TaskReward(
    val contribution: Int,          // 贡献度奖励
    val items: Map<Entity, Int>,    // 物品奖励
    val experience: Long            // 经验奖励
)
```

---

## 研究任务 3: 建筑升级系统

### 问题

如何实现建筑等级管理和升级机制？

### 研究发现

**现有系统**:
- LevelingService 提供 `upgradeable()` 方法使实体可升级
- 支持自定义 ExperienceFormula
- 升级时发出 OnUpgradeEvent 事件

**需求**:
- 建筑有等级属性
- 升级消耗灵石+特定材料
- 资源消耗按指数增长（基础×1.5^等级）
- 升级后提升建筑属性

### 决策

**Decision**: 复用 LevelingService + AttributeService 管理建筑

**Rationale**:
- 建筑等级管理与角色等级相似，复用 LevelingService
- 建筑属性（效率、容量）使用 AttributeService 管理
- 升级资源消耗通过单独的 BuildingUpgradeService 计算

**Alternatives considered**:
1. 新建独立的建筑等级系统 - 重复 LevelingService 逻辑
2. 直接用组件存储等级 - 不符合属性系统优先原则

### 实现要点

```kotlin
// 建筑升级条件计算
fun calculateUpgradeCost(building: Entity): Map<Entity, Int> {
    val level = levelingService.getLevel(building)
    val baseCost = building.getComponent<BuildingBaseCost>()
    return baseCost.resources.mapValues { (_, base) ->
        (base * 1.5.pow(level.toDouble())).toInt()
    }
}

// 建筑效果属性
attributes {
    register(Named("efficiency"), Description("建筑效率"))
    register(Named("capacity"), Description("建筑容量"))
}
```

---

## 研究任务 4: 功法/技能系统

### 问题

如何实现功法存储、学习要求验证和弟子已学功法管理？

### 研究发现

**需求**:
- 功法存放在藏经阁
- 功法有学习要求（贡献度、等级）
- 弟子可学习功法
- 功法可被多人学习（不消耗本体）

**ECS 模式分析**:
- 功法是实体（有名称、描述、效果等组件）
- 藏经阁与功法是所有权关系（OwnedBy）
- 弟子与功法是学习关系（需要自定义关系类型）

### 决策

**Decision**: 使用带数据关系 `TechniqueLearned` 管理已学功法

**Rationale**:
- 弟子和功法是多对多关系
- 需要记录学习时间、熟练度等数据
- 使用关系系统而非在弟子组件中存储功法ID列表

**Alternatives considered**:
1. 在 Character 组件中添加 learnedTechniques 列表 - 违反关系系统优先原则
2. 创建中间实体 - 增加不必要的复杂度

### 关键组件设计

```kotlin
// 功法预制体标签
sealed class Technique

// 功法学习要求
data class TechniqueRequirement(
    val contribution: Int,          // 所需贡献度
    val level: Long,                // 所需等级
    val prerequisiteTechniques: Set<Entity> = emptySet() // 前置功法
)

// 功法学习关系数据（弟子 -> 功法）
data class TechniqueLearned(
    val technique: Entity,          // 功法实体
    val learnTime: Long,            // 学习时间
    val proficiency: Int = 0        // 熟练度
)

// 功法效果（属性加成）
data class TechniqueEffect(
    val attributeModifiers: Map<Named, Long>  // 属性修改器
)
```

---

## 研究任务 5: 升级奖励与功能解锁

### 问题

如何实现宗门升级时的奖励发放和功能解锁？

### 研究发现

**现有系统**:
- LevelingService 在升级时发出 `OnUpgradeEvent(oldLevel, newLevel)` 事件
- 可以通过 `world.observe<OnUpgradeEvent>().exec {}` 订阅升级事件

**需求**:
- 宗门升级通知所有成员
- 根据等级解锁新建筑类型
- 根据等级提升建筑容量上限

### 决策

**Decision**: 通过事件订阅机制实现升级触发逻辑

**Rationale**:
- 利用现有的 OnUpgradeEvent 事件系统
- 在 sectAddon 中订阅宗门升级事件
- 解锁数据通过配置组件定义

**Alternatives considered**:
1. 轮询检查等级变化 - 低效且不符合事件驱动模式
2. 在 LevelingService 中添加宗门特定逻辑 - 违反单一职责原则

### 实现要点

```kotlin
// 宗门等级解锁配置
data class SectLevelUnlock(
    val level: Long,
    val buildingTypes: Set<KClass<out Building>>,  // 解锁的建筑类型
    val memberCapacity: Int,                        // 成员容量提升
    val buildingCapacity: Int                       // 建筑容量提升
)

// 订阅宗门升级事件
world.observe<OnUpgradeEvent>().involving<Sect>().exec { event ->
    val unlocks = getSectLevelUnlocks(event.newLevel)
    applyUnlocks(entity, unlocks)
    notifyAllMembers(entity, "宗门升级到 ${event.newLevel} 级！")
}
```

---

## 研究任务 6: 福利系统设计

### 问题

如何实现周期性福利发放和领取记录？

### 研究发现

**现有系统**:
- Countdown 系统提供倒计时机制
- 倒计时完成时发出 OnCountdownComplete 事件

**需求**:
- 福利按日结算
- 不同角色福利不同
- 防止重复领取

### 决策

**Decision**: 使用 Countdown 系统 + 关系记录实现福利周期

**Rationale**:
- 使用 Countdown 关系设置下次福利可领取时间
- 福利规则存储为宗门的组件
- 领取记录使用关系系统（成员 -> 福利规则）

**Alternatives considered**:
1. 外部定时器 - 不符合 ECS 优先原则
2. 每帧检查时间 - 低效

### 关键组件设计

```kotlin
// 福利规则
data class WelfareRule(
    val name: String,
    val period: Duration,                    // 发放周期
    val rewardsByRole: Map<MemberRole, WelfareReward> // 按角色的奖励
)

data class WelfareReward(
    val money: Int,
    val items: Map<Entity, Int>,
    val contribution: Int
)

// 福利领取状态关系（成员 -> 福利规则实体）
data class WelfareStatus(
    val lastClaimTime: Long,    // 上次领取时间
    val nextClaimTime: Long     // 下次可领取时间
)
```

---

## 研究任务 7: 成员管理增强

### 问题

如何实现分层权限和综合考核晋升机制？

### 研究发现

**现有系统**:
- MemberRole 枚举：LEADER, ELDER, DISCIPLE
- MemberData 关系存储角色和贡献度

**需求**:
- 新增 INNER_DISCIPLE（内门弟子）角色
- 职能分工式权限
- 分层成员配额
- 综合考核晋升

### 决策

**Decision**: 扩展 MemberRole 枚举，添加权限检查方法

**Rationale**:
- 在现有 MemberRole 基础上新增 INNER_DISCIPLE
- 使用 sealed class 定义权限类型
- 成员配额存储为宗门组件
- 晋升条件检查封装在 SectMemberService 中

**Alternatives considered**:
1. 新建独立的权限系统 - 过度设计
2. 使用字符串表示角色 - 类型不安全

### 实现要点

```kotlin
// 扩展角色枚举
enum class MemberRole {
    LEADER,          // 宗主：全权限
    ELDER,           // 长老：管理权限
    INNER_DISCIPLE,  // 内门弟子：任务发布权限
    OUTER_DISCIPLE   // 外门弟子：仅参与权限
}

// 权限类型
sealed class Permission {
    object ManageMembers : Permission()      // 管理成员
    object ManageBuildings : Permission()    // 管理建筑
    object PublishTasks : Permission()       // 发布任务
    object TakeTasks : Permission()          // 领取任务
    object LearnTechniques : Permission()    // 学习功法
    object ClaimWelfare : Permission()       // 领取福利
}

// 权限检查
fun MemberRole.hasPermission(permission: Permission): Boolean = when (this) {
    LEADER -> true
    ELDER -> permission !is Permission.ManageMembers || ...
    INNER_DISCIPLE -> permission in setOf(PublishTasks, TakeTasks, ...)
    OUTER_DISCIPLE -> permission in setOf(TakeTasks, LearnTechniques, ClaimWelfare)
}

// 成员配额
data class MemberQuota(
    val maxElders: Int = 5,
    val maxInnerDisciples: Int = 20,
    val maxOuterDisciples: Int = 50  // 随宗门等级提升
)

// 晋升条件
data class PromotionRequirement(
    val targetRole: MemberRole,
    val minContribution: Int,
    val minLevel: Long,
    val requiredTasks: Set<Entity> = emptySet(),  // 考核任务
    val requiresApproval: Boolean = true           // 是否需要审批
)
```

---

## 研究结论

### 所有 NEEDS CLARIFICATION 已解决

| 原问题 | 解决方案 |
|--------|----------|
| 资源存储方式 | 复用 InventoryService，资源即物品 |
| 任务-弟子关联 | 使用带数据关系 TaskProgress |
| 建筑等级管理 | 复用 LevelingService |
| 功法学习记录 | 使用带数据关系 TechniqueLearned |
| 福利周期触发 | 使用 Countdown 系统 |
| 权限与晋升 | 扩展 MemberRole，添加权限检查方法 |

### 复用服务清单

| 服务 | 用途 |
|------|------|
| InventoryService | 宗门资源管理、任务奖励发放、福利发放 |
| LevelingService | 建筑等级管理、晋升等级检查 |
| MoneyService | 灵石货币管理 |
| ItemService | 物品预制体定义（资源类型） |
| AttributeService | 建筑效果属性、功法属性加成 |
| Countdown | 任务时限、福利周期 |

### 新增关系类型

| 关系类型 | 源实体 | 目标实体 | 数据 |
|----------|--------|----------|------|
| TaskProgress | Task | Disciple | 领取时间、状态、已提交物品 |
| TechniqueLearned | Disciple | Technique | 学习时间、熟练度 |
| WelfareStatus | Member | WelfareRule | 上次领取时间、下次可领取时间 |

