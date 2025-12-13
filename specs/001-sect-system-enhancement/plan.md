# 实施计划: 宗门系统完善

**分支**: `001-sect-system-enhancement` | **日期**: 2025-12-13 | **规范**: [spec.md](./spec.md)
**输入**: 来自 `/specs/001-sect-system-enhancement/spec.md` 的功能规范

## 摘要

本计划旨在完善现有宗门系统，基于已澄清的功能规范，新增以下核心功能：
1. **宗门资源管理** - 使用 InventoryService 复用物品系统管理宗门仓库
2. **宗门任务系统** - 采用 ECS 关系系统管理任务领取与进度
3. **建筑升级系统** - 复用 LevelingService 实现建筑等级管理
4. **功法/技能系统** - 使用关系系统管理弟子已学功法
5. **宗门升级奖励** - 监听 OnUpgradeEvent 触发解锁机制
6. **福利系统** - 使用 Countdown 系统管理周期性福利发放
7. **成员管理增强** - 扩展 MemberRole 枚举与权限检查

## 技术背景

**语言/版本**: Kotlin Multiplatform (JVM Desktop, JS Web)
**主要依赖**: lko-ecs4 (ECS框架), lko-di (依赖注入), lko-sect (游戏逻辑模块)
**存储**: 内存 (ECS World)，后续可扩展序列化
**测试**: Kotlin Test (commonTest)
**目标平台**: JVM (Desktop), JS (Web)
**项目类型**: 游戏逻辑库 (Multiplatform Library)
**性能目标**: 60 fps 游戏循环兼容
**约束条件**: 遵循 ECS 架构，组件数据不可变
**规模/范围**: 单宗门 100+ 成员，100+ 任务并发

## 章程检查

*门控: 必须在阶段 0 研究前通过. 阶段 1 设计后重新检查.*

**核心原则验证** (参考 `.specify/memory/constitution.md`):

- [x] **I. ECS 优先**: 所有新实体使用 `World.entity {}` 创建，数据通过 Component 存储，关系使用 Relation 表达
- [x] **II. 服务复用优先**: 
  - 复用 `LevelingService` 管理建筑等级
  - 复用 `InventoryService` 管理宗门仓库（物品即资源）
  - 复用 `MoneyService` 管理灵石货币
  - 复用 `ItemService` 管理物品预制体
  - 复用 `Countdown` 系统管理任务时限和福利周期
- [x] **III. 框架一致性**: 遵循 Addon 模式，服务继承 `EntityRelationContext`
- [x] **IV. 关系系统优先**: 使用 `OwnedBy` 管理所有权，使用自定义关系管理任务进度和功法学习
- [x] **V. 属性系统优先**: 建筑效率/容量等属性使用 AttributeService 管理

**技术约束验证**:
- [x] 使用 Kotlin Multiplatform
- [x] 使用 lko-di 进行依赖注入
- [x] 代码组织符合项目结构规范

## 项目结构

### 文档(此功能)

```
specs/001-sect-system-enhancement/
├── plan.md              # 此文件
├── research.md          # 阶段 0 输出 - 设计决策研究
├── data-model.md        # 阶段 1 输出 - 数据模型定义
├── quickstart.md        # 阶段 1 输出 - 快速开始指南
├── contracts/           # 阶段 1 输出 - API 合同
└── tasks.md             # 阶段 2 输出 - 实施任务
```

### 源代码(仓库根目录)

```
lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/
├── sect/                     # 宗门核心模块
│   ├── SectService.kt       # 宗门服务（扩展）
│   ├── SectResource.kt      # 宗门资源管理
│   └── SectMember.kt        # 成员管理增强
├── task/                     # 任务系统（新增）
│   ├── Task.kt              # 任务组件与服务
│   ├── TaskProgress.kt      # 任务进度关系
│   └── TaskReward.kt        # 任务奖励计算
├── building/                 # 建筑系统（新增）
│   ├── Building.kt          # 建筑组件与服务
│   ├── BuildingUpgrade.kt   # 建筑升级逻辑
│   └── BuildingEffect.kt    # 建筑效果属性
├── technique/                # 功法系统（新增）
│   ├── Technique.kt         # 功法组件与服务
│   └── TechniqueLearning.kt # 功法学习关系
├── welfare/                  # 福利系统（新增）
│   ├── Welfare.kt           # 福利规则与服务
│   └── WelfareRecord.kt     # 福利领取记录
└── Sects.kt                  # 宗门 Addon（更新依赖）
```

**结构决策**: 按功能模块划分目录，每个模块包含组件定义、服务实现和相关关系类型。

## 复杂度跟踪

| 违规 | 为什么需要 | 拒绝更简单替代方案的原因 |
|-----------|------------|-------------------------------------|
| 无 | - | 设计完全遵循章程原则 |

## 设计决策摘要

### 关键设计决策

| 决策领域 | 决定 | 理由 |
|----------|------|------|
| 资源存储 | 复用 InventoryService | 宗门资源本质是物品，避免重复实现物品管理逻辑 |
| 任务-弟子关系 | 使用带数据关系 `TaskProgress` | 需要存储领取时间、完成状态等额外数据 |
| 建筑等级 | 复用 LevelingService | 建筑升级逻辑与角色升级相似，复用经验公式机制 |
| 功法学习 | 使用带数据关系 `TechniqueLearned` | 需要记录学习时间、熟练度等额外数据 |
| 福利周期 | 复用 Countdown 系统 | 利用现有倒计时机制实现周期性触发 |
| 权限检查 | 扩展 MemberRole 枚举 | 新增 INNER_DISCIPLE 区分内门/外门弟子 |

### 服务依赖关系

```
sectAddon (扩展)
├── taskAddon (新增)
│   ├── coreAddon
│   ├── countdownAddon
│   └── inventoryAddon (用于奖励发放)
├── buildingAddon (新增)
│   ├── coreAddon
│   └── levelingAddon
├── techniqueAddon (新增)
│   ├── coreAddon
│   └── attributeAddon (用于功法属性加成)
└── welfareAddon (新增)
    ├── coreAddon
    ├── countdownAddon
    └── inventoryAddon (用于福利发放)
```

## 阶段 1 设计产物

以下文档在阶段 1 中生成：
- `research.md` - 详细的设计决策研究
- `data-model.md` - 完整的数据模型定义
- `contracts/` - API 合同（服务接口定义）
- `quickstart.md` - 开发者快速开始指南

---

## 设计后章程检查（重新评估）

*阶段 1 设计完成后的合规性验证*

**核心原则验证**:

| 原则 | 符合 | 说明 |
|------|------|------|
| I. ECS 优先 | ✅ | 所有实体使用 `World.entity {}` 创建，组件使用 `sealed class` 或 `value class` |
| II. 服务复用优先 | ✅ | 复用 LevelingService、InventoryService、MoneyService、ItemService、Countdown |
| III. 框架一致性 | ✅ | 所有服务继承 `EntityRelationContext`，使用 `createAddon()` 创建 Addon |
| IV. 关系系统优先 | ✅ | 使用 `OwnedBy`、`TaskProgress`、`TechniqueLearned`、`WelfareStatus` 关系 |
| V. 属性系统优先 | ✅ | 建筑效率/容量使用 AttributeService，角色等级使用属性系统 |

**技术约束验证**:

| 约束 | 符合 | 说明 |
|------|------|------|
| Kotlin Multiplatform | ✅ | 代码位于 commonMain 目录 |
| lko-di 依赖注入 | ✅ | 服务通过 `world.di.instance<T>()` 注入 |
| 项目结构规范 | ✅ | 按功能模块划分目录 |

**复杂度检查**:

- ❌ 无复杂度违规
- 设计保持简单，复用现有系统

**结论**: 设计完全符合项目章程，可进入阶段 2 任务分解。

