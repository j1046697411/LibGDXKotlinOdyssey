# 任务清单: 宗门系统完善

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**规范**: [spec.md](./spec.md) | **计划**: [plan.md](./plan.md)

## 摘要

本任务清单基于宗门系统完善功能规范生成，包含 6 个用户故事的实现任务。

**任务统计**:
- 总任务数: 48
- 阶段 1 (设置): 4 任务
- 阶段 2 (基础): 6 任务
- 阶段 3 (US1 资源管理): 6 任务
- 阶段 4 (US2 任务系统): 10 任务
- 阶段 5 (US3 建筑升级): 6 任务
- 阶段 6 (US4 功法系统): 6 任务
- 阶段 7 (US5 升级奖励): 4 任务
- 阶段 8 (US6 福利系统): 4 任务
- 阶段 9 (完善): 2 任务

**并行执行机会**: 阶段 3-8 的用户故事在基础阶段完成后可部分并行执行，每个阶段内标记 [P] 的任务可并行开发。

---

## 依赖关系图

```
阶段 1: 设置
    └── 阶段 2: 基础 (扩展 MemberRole, 组件定义)
            ├── 阶段 3: US1 资源管理 (P1) ──────────┐
            │                                        │
            ├── 阶段 4: US2 任务系统 (P1) ──────────┤
            │       └── 依赖: US1 (奖励发放)         │
            │                                        │
            ├── 阶段 5: US3 建筑升级 (P2) ──────────┤
            │       └── 依赖: US1 (资源消耗)         │
            │                                        │
            ├── 阶段 6: US4 功法系统 (P2) ──────────┤
            │       └── 依赖: 阶段 2                  │
            │                                        │
            ├── 阶段 7: US5 升级奖励 (P3) ──────────┤
            │       └── 依赖: US1, US3               │
            │                                        │
            └── 阶段 8: US6 福利系统 (P3) ──────────┘
                    └── 依赖: US1
                            │
                            ▼
                    阶段 9: 完善
```

**推荐执行顺序**:
1. 先完成阶段 1-2 (设置和基础)
2. 完成 US1 (资源管理) - 其他用户故事依赖此功能
3. US2 和 US3 可并行开发
4. US4 和 US5 可并行开发
5. US6 最后完成
6. 执行阶段 9 完善任务

---

## 阶段 1: 设置

**目标**: 初始化项目结构，创建必要的目录和基础文件。

- [ ] T001 [P] 在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/` 创建目录结构
- [ ] T002 [P] 在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/` 创建目录结构
- [ ] T003 [P] 在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/` 创建目录结构
- [ ] T004 [P] 在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/` 创建目录结构

**检查点**: 目录结构已创建，准备开始基础组件开发。

---

## 阶段 2: 基础

**目标**: 扩展基础枚举和共享组件，为所有用户故事提供基础设施。

- [ ] T005 扩展 `MemberRole` 枚举，添加 `INNER_DISCIPLE` 角色，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt`
- [ ] T006 创建 `MemberQuota` 数据类，定义成员配额配置，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/MemberQuota.kt`
- [ ] T007 创建 `Permission` 密封类，定义权限类型，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/Permission.kt`
- [ ] T008 [P] 更新 `MemberData` 数据类，添加 `joinTime` 字段，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt`
- [ ] T009 [P] 创建 `SectConfig` 数据类，定义宗门配置，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectConfig.kt`
- [ ] T010 注册新组件到 `sectAddon`，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt`

**检查点**: 基础组件定义完成，可开始用户故事实现。

---

## 阶段 3: 用户故事 1 - 宗门资源管理 (P1)

**故事目标**: 作为宗门管理者，我希望能够管理宗门的各种资源（如灵石、灵草、丹药等），以便合理分配资源支持宗门发展和弟子修炼。

**独立测试标准**:
- 可以通过创建宗门、存入资源、查看资源余额、取出资源来完全测试
- 交付宗门资源存储和管理的核心价值

**验收标准**:
1. 给定一个已创建的宗门, 当管理者向宗门仓库存入 100 灵石时, 那么宗门资源中灵石数量增加 100
2. 给定宗门仓库有 100 灵石, 当管理者取出 30 灵石时, 那么宗门资源中灵石数量减少为 70
3. 给定宗门仓库有 50 灵石, 当管理者尝试取出 100 灵石时, 那么系统提示资源不足，操作失败

### 任务

- [ ] T011 创建资源类型物品预制体（灵石、低级灵草、中级灵草、高级灵草、养气丹、筑基丹等），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectResourcePrefabs.kt`
- [ ] T012 [P] 创建 `SectResourceService` 类，封装 InventoryService 提供宗门资源管理 API，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectResourceService.kt`
- [ ] T013 实现 `deposit()` 和 `withdraw()` 方法，支持单个资源存取，在 `SectResourceService.kt`
- [ ] T014 实现 `depositAll()` 和 `withdrawAll()` 方法，支持批量资源存取，在 `SectResourceService.kt`
- [ ] T015 实现 `transferFromMember()` 和 `transferToMember()` 方法，支持成员与宗门资源转移，在 `SectResourceService.kt`
- [ ] T016 实现资源查询方法 `getResourceAmount()`、`getAllResources()`、`hasEnoughResource()`、`getMissingResources()`，在 `SectResourceService.kt`

**检查点 US1**: 宗门资源管理功能完成，可独立测试资源存取和查询。

---

## 阶段 4: 用户故事 2 - 宗门任务系统 (P1)

**故事目标**: 作为弟子，我希望能够领取宗门发布的任务并完成后获得奖励，以便获取贡献度和资源来提升自己。

**独立测试标准**:
- 可以通过创建任务、弟子领取任务、完成任务、获得奖励来完全测试
- 交付任务驱动的宗门互动价值

**验收标准**:
1. 给定宗门有一个采药任务, 当弟子领取该任务时, 那么任务状态变为进行中，弟子的任务列表显示该任务
2. 给定弟子正在进行采药任务, 当弟子提交任务所需物品时, 那么任务完成，弟子获得贡献度奖励
3. 给定弟子正在进行任务, 当弟子放弃任务时, 那么任务返回任务池，弟子可重新领取

### 任务

- [ ] T017 创建 `TaskType` 枚举（GATHERING、COMBAT、CULTIVATION、EXPLORATION），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskType.kt`
- [ ] T018 创建 `TaskStatus` 枚举（AVAILABLE、IN_PROGRESS、COMPLETED、FAILED、CANCELLED），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskStatus.kt`
- [ ] T019 [P] 创建 `SectTask` 标签组件和相关数据组件（TaskRequirement、TaskRewardConfig、TaskLimit），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/Task.kt`
- [ ] T020 [P] 创建 `TaskProgress` 关系数据类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskProgress.kt`
- [ ] T021 创建 `TaskService` 类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskService.kt`
- [ ] T022 实现 `createTask()` 方法，支持创建不同类型的宗门任务，在 `TaskService.kt`
- [ ] T023 实现 `acceptTask()` 和 `cancelTask()` 方法，支持任务领取和放弃，在 `TaskService.kt`
- [ ] T024 实现 `submitItems()` 和 `completeTask()` 方法，支持任务提交和完成，在 `TaskService.kt`
- [ ] T025 实现任务查询方法（getTasksBySect、getAvailableTasks、getActiveTasksForDisciple、canAcceptTask 等），在 `TaskService.kt`
- [ ] T026 创建 `taskAddon`，注册组件和服务，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskAddon.kt`

**检查点 US2**: 任务系统功能完成，可独立测试任务创建、领取、提交和奖励发放。

---

## 阶段 5: 用户故事 3 - 宗门建筑升级 (P2)

**故事目标**: 作为宗门管理者，我希望能够升级宗门建筑，以便提升建筑的产出效率和容量。

**独立测试标准**:
- 可以通过创建建筑、满足升级条件、执行升级、查看升级效果来完全测试
- 交付建筑成长的价值

**验收标准**:
1. 给定一个 1 级炼丹房, 当满足升级条件（资源、宗门等级等）并执行升级时, 那么炼丹房升级为 2 级
2. 给定一个 2 级炼丹房, 当宗门等级不足时尝试升级, 那么系统提示宗门等级不足，升级失败
3. 给定一个升级后的炼丹房, 当进行炼丹时, 那么炼丹效率/成功率有所提升

### 任务

- [ ] T027 创建 `BuildingType` 枚举和 `BuildingConfig` 数据类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingType.kt`
- [ ] T028 [P] 创建新建筑类型标签（TrainingHall、TreasureVault），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/Building.kt`
- [ ] T029 [P] 创建 `BuildingBaseCost` 组件和 `BuildingUpgradeError` 密封类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingCost.kt`
- [ ] T030 创建 `BuildingService` 类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingService.kt`
- [ ] T031 实现建筑创建方法（createAlchemyHall、createLibrary、createTrainingHall、createTreasureVault），在 `BuildingService.kt`
- [ ] T032 实现建筑升级方法（calculateUpgradeCost、canUpgrade、upgrade）和查询方法，在 `BuildingService.kt`

**检查点 US3**: 建筑升级功能完成，可独立测试建筑创建、升级条件检查和升级执行。

---

## 阶段 6: 用户故事 4 - 宗门技能/功法传授 (P2)

**故事目标**: 作为弟子，我希望能够从宗门藏经阁学习功法和技能，以便提升自己的战斗能力和修炼效率。

**独立测试标准**:
- 可以通过添加功法到藏经阁、弟子学习功法、验证弟子获得功法效果来完全测试
- 交付知识传承的价值

**验收标准**:
1. 给定藏经阁有《基础剑法》, 当弟子消耗贡献度学习该功法时, 那么弟子获得该功法
2. 给定弟子贡献度不足, 当弟子尝试学习功法时, 那么系统提示贡献度不足，学习失败
3. 给定功法有等级要求, 当弟子等级不满足时尝试学习, 那么系统提示等级不足，学习失败

### 任务

- [ ] T033 创建 `TechniqueGrade` 枚举和相关数据类（TechniqueRequirement、TechniqueEffect、PassiveEffect），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/Technique.kt`
- [ ] T034 [P] 创建 `Technique` 标签组件和 `TechniqueLearned` 关系数据类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueLearned.kt`
- [ ] T035 [P] 创建 `TechniqueLearnError` 密封类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueError.kt`
- [ ] T036 创建 `TechniqueService` 类，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueService.kt`
- [ ] T037 实现功法创建和学习方法（createTechnique、addToLibrary、canLearn、learn），在 `TechniqueService.kt`
- [ ] T038 实现功法查询方法（getTechniquesByLibrary、getLearnedTechniques、hasLearned、getTotalAttributeModifiers 等），在 `TechniqueService.kt`

**检查点 US4**: 功法系统功能完成，可独立测试功法创建、学习要求验证和学习执行。

---

## 阶段 7: 用户故事 5 - 宗门升级奖励 (P3)

**故事目标**: 作为宗门成员，我希望在宗门升级时获得相应的奖励和解锁新功能，以便享受宗门发展带来的福利。

**独立测试标准**:
- 可以通过宗门升级、验证奖励发放、检查新功能解锁来完全测试
- 交付成长激励的价值

**验收标准**:
1. 给定宗门即将升级到 2 级, 当宗门经验达到升级要求时, 那么宗门升级，所有成员收到通知
2. 给定宗门升级到 2 级, 当升级完成时, 那么解锁新建筑类型（如练功房）
3. 给定宗门升级到 3 级, 当升级完成时, 那么建筑容量上限提升

### 任务

- [x] T039 创建 `SectLevelUnlocks` 数据类和 `OnSectLevelUp` 事件组件，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectUpgrade.kt`
- [x] T040 [P] 创建宗门等级解锁配置（各等级解锁的建筑类型、容量增加等），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectLevelConfig.kt`
- [x] T041 扩展 `SectService`，添加升级事件监听和解锁处理逻辑，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt`
- [x] T042 实现 `getUnlockedBuildingTypes()` 方法返回当前等级可用的建筑类型，在 `BuildingService.kt`

**检查点 US5**: 升级奖励功能完成，可独立测试宗门升级后的功能解锁和奖励发放。

---

## 阶段 8: 用户故事 6 - 宗门福利发放 (P3)

**故事目标**: 作为宗门成员，我希望定期获得宗门发放的福利，以便获取修炼资源。

**独立测试标准**:
- 可以通过设置福利规则、触发福利发放、验证成员收到福利来完全测试
- 交付定期激励的价值

**验收标准**:
1. 给定宗门设定了每日福利规则, 当新的一天开始时, 那么符合条件的成员自动获得福利
2. 给定福利规则根据成员角色不同, 当福利发放时, 那么长老获得比弟子更多的福利
3. 给定成员今日已领取福利, 当再次尝试领取时, 那么系统提示今日已领取

### 任务

- [x] T043 创建 `WelfareRule` 标签组件和相关数据类（WelfareConfig、WelfareReward、WelfareStatus），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/Welfare.kt`
- [x] T044 [P] 创建 `WelfareClaimError` 密封类和 `OnWelfareClaimed` 事件组件，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareError.kt`
- [x] T045 创建 `WelfareService` 类，实现福利规则管理和领取方法，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareService.kt`
- [x] T046 创建 `welfareAddon`，注册组件和服务，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareAddon.kt`

**检查点 US6**: 福利系统功能完成，可独立测试福利规则创建、冷却检查和领取执行。

---

## 阶段 9: 完善与横切关注点

**目标**: 集成所有功能，完善成员管理增强，确保系统完整性。

- [x] T047 创建 `SectMemberService` 类，实现权限检查、晋升机制和配额管理，在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectMemberService.kt`
- [x] T048 更新 `sectAddon`，集成所有新增的子 Addon（taskAddon、buildingAddon、techniqueAddon、welfareAddon），在 `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt`

**检查点 最终**: 所有功能集成完成，系统可完整运行。

---

## 并行执行示例

### 开发团队 2 人场景

**开发者 A (专注核心功能)**:
1. 阶段 1: T001-T004 (设置)
2. 阶段 2: T005-T007, T010 (基础核心)
3. 阶段 3: US1 所有任务 (资源管理)
4. 阶段 4: US2 所有任务 (任务系统)
5. 阶段 9: T047-T048 (完善)

**开发者 B (专注扩展功能)**:
1. 等待阶段 2 完成
2. 阶段 5: US3 所有任务 (建筑升级) - 可与 US2 并行
3. 阶段 6: US4 所有任务 (功法系统) - 可与 US3 并行
4. 阶段 7: US5 所有任务 (升级奖励)
5. 阶段 8: US6 所有任务 (福利系统)

### 单人开发场景

按任务编号顺序执行，每个阶段完成后验证检查点。

---

## 实施策略

### MVP 优先

**MVP 范围** (建议): 完成 US1 (宗门资源管理) + US2 (任务系统)
- 这两个功能构成宗门系统的核心互动循环
- 资源管理是其他功能的基础
- 任务系统提供弟子与宗门互动的主要途径

### 增量交付

1. **第一次交付**: 阶段 1-2 + US1 (资源管理可用)
2. **第二次交付**: US2 (任务系统可用，核心循环完成)
3. **第三次交付**: US3 + US4 (建筑和功法系统)
4. **第四次交付**: US5 + US6 + 阶段 9 (完整功能)

### 风险点

1. **US2 依赖 US1**: 任务奖励发放需要资源系统支持
2. **US3 依赖 US1**: 建筑升级需要消耗资源
3. **US5 依赖 US3**: 升级解锁需要建筑系统支持
4. **US6 依赖 US1**: 福利发放需要资源系统支持

---

## 文件路径汇总

### 新增文件

| 文件路径 | 相关任务 |
|----------|----------|
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/MemberQuota.kt` | T006 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/Permission.kt` | T007 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectConfig.kt` | T009 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectResourcePrefabs.kt` | T011 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectResourceService.kt` | T012-T016 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectUpgrade.kt` | T039 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectLevelConfig.kt` | T040 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/sect/SectMemberService.kt` | T047 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskType.kt` | T017 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskStatus.kt` | T018 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/Task.kt` | T019 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskProgress.kt` | T020 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskService.kt` | T021-T025 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/task/TaskAddon.kt` | T026 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingType.kt` | T027 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/Building.kt` | T028 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingCost.kt` | T029 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/building/BuildingService.kt` | T030-T032, T042 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/Technique.kt` | T033 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueLearned.kt` | T034 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueError.kt` | T035 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/technique/TechniqueService.kt` | T036-T038 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/Welfare.kt` | T043 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareError.kt` | T044 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareService.kt` | T045 |
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/welfare/WelfareAddon.kt` | T046 |

### 修改文件

| 文件路径 | 相关任务 |
|----------|----------|
| `lko-sect/src/commonMain/kotlin/cn/jzl/sect/ecs/Sects.kt` | T005, T008, T010, T041, T048 |

