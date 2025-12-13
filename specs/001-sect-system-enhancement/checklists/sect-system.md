# 宗门系统 检查清单: 需求质量验证

**目的**: 验证宗门系统三个核心功能模块（SectResourceService、TaskService、BuildingService）的需求完整性、清晰度和一致性
**创建时间**: 2025-12-13
**功能**: [spec.md](../spec.md) | [plan.md](../plan.md) | [tasks.md](../tasks.md)
**范围**: 全部范围（D）| 深度: 标准（B）| 执行者: 作者自检（A）

---

## 需求完整性

### 宗门资源服务 (SectResourceService)

- [ ] CHK001 - 资源存入操作的边界条件（数量为 0 或负数）是否有明确的错误处理需求？ [Gap, Spec §FR-002]
- [ ] CHK002 - 资源取出失败时的错误返回类型是否已定义？（当前仅指定 `IllegalStateException`） [Clarity, Contract §withdraw]
- [ ] CHK003 - 批量操作 `withdrawAll()` 的事务性是否已指定？（部分成功时是否回滚） [Gap, Contract §withdrawAll]
- [ ] CHK004 - 成员与宗门之间的资源转移是否有权限检查需求？ [Gap, Contract §transferFromMember]
- [ ] CHK005 - 资源类型预制体的扩展机制是否有文档说明？ [Gap, Contract §资源类型预制体]

### 任务系统 (TaskService)

- [ ] CHK006 - 任务创建时的必填字段验证需求是否已定义？ [Gap, Contract §createTask]
- [ ] CHK007 - 任务并发领取时的竞争条件处理是否已指定？（边界情况中提到但未在 FR 中细化） [Clarity, Spec §边界情况]
- [ ] CHK008 - 任务超时自动失败的触发机制和通知需求是否已定义？ [Gap, Spec §FR-012]
- [ ] CHK009 - 动态奖励公式的具体计算参数和权重是否已量化？ [Clarity, Spec §FR-010a]
- [ ] CHK010 - 任务取消后的状态恢复需求是否已完整定义？（资源返还、进度清零等） [Gap, Contract §cancelTask]

### 建筑服务 (BuildingService)

- [ ] CHK011 - 建筑创建失败的所有错误场景是否已枚举完整？ [Coverage, Contract §createAlchemyHall]
- [ ] CHK012 - 建筑拆除功能的需求是否已定义？（任务中未见相关任务） [Gap, Tasks]
- [ ] CHK013 - 建筑升级过程中的状态（升级中）是否需要支持？ [Gap, Spec §FR-015]
- [ ] CHK014 - 建筑容量满时的用户反馈需求是否已指定？ [Gap, Contract §createAlchemyHall]

---

## 需求清晰度

### 宗门资源服务 (SectResourceService)

- [ ] CHK015 - "资源不足时阻止取出操作并给出明确提示"中的"明确提示"是否已量化？（错误码、消息格式等） [Clarity, Spec §FR-004]
- [ ] CHK016 - `getMissingResources()` 返回结果的排序规则是否已定义？ [Clarity, Contract §getMissingResources]
- [ ] CHK017 - 资源预制体的 `UnitPrice` 字段用途是否已明确说明？ [Clarity, Contract §资源类型预制体]

### 任务系统 (TaskService)

- [ ] CHK018 - "弟子可自由领取多个任务，上限 5 个"的计数规则是否明确？（仅进行中还是包含已完成未领奖？） [Clarity, Spec §FR-007a]
- [ ] CHK019 - TaskAcceptError 的所有错误类型是否已完整列举？ [Completeness, Contract §错误类型]
- [ ] CHK020 - "任务完成时间、完成质量"如何量化？ [Ambiguity, Spec §FR-010a]
- [ ] CHK021 - 任务类型（采集/战斗/修炼/探索）的具体差异和判定标准是否已定义？ [Clarity, Spec §FR-006a]

### 建筑服务 (BuildingService)

- [ ] CHK022 - "效率提升"和"容量提升"的具体数值公式是否已定义？ [Clarity, Spec §FR-016]
- [ ] CHK023 - `calculateUpgradeCost()` 中的指数增长公式 `基础×1.5^等级` 的"基础"值如何获取？ [Clarity, Spec §FR-014a]
- [ ] CHK024 - BuildingUpgradeError 的所有错误类型是否与 `canUpgrade()` 返回值一致？ [Consistency, Contract §canUpgrade]

---

## 需求一致性

- [ ] CHK025 - SectResourceService 的资源类型与 TaskService 奖励发放的资源类型是否一致？ [Consistency, Contract 跨服务]
- [ ] CHK026 - BuildingService 中的宗门等级要求与 SectService 的等级系统是否对齐？ [Consistency, Plan §服务依赖]
- [ ] CHK027 - 权限检查需求在三个服务中是否一致应用？（FR-027 权限分工） [Consistency, Spec §FR-027]
- [ ] CHK028 - 错误处理模式（抛异常 vs 返回 Result）在三个服务中是否一致？ [Consistency, Contract 跨服务]
- [ ] CHK029 - 成员角色枚举（MemberRole）在任务领取限制和权限检查中的使用是否一致？ [Consistency, Spec §FR-027]

---

## 验收标准质量

### 宗门资源服务 (SectResourceService)

- [ ] CHK030 - 验收场景 1-3 是否覆盖了批量操作的测试用例？ [Coverage, Spec §US1]
- [ ] CHK031 - "30 秒内完成一次资源存取操作"的测量方法是否已定义？ [Measurability, Spec §SC-001]

### 任务系统 (TaskService)

- [ ] CHK032 - 验收场景是否覆盖了任务超时失败的情况？ [Coverage, Spec §US2]
- [ ] CHK033 - "1 分钟内完成任务领取和查看任务详情"的操作路径是否已定义？ [Measurability, Spec §SC-002]
- [ ] CHK034 - 任务奖励发放的验收标准是否可测量？（具体数值验证） [Measurability, Spec §US2 验收场景2]

### 建筑服务 (BuildingService)

- [ ] CHK035 - "100% 成功执行"的边界条件是否已明确？ [Clarity, Spec §SC-003]
- [ ] CHK036 - 验收场景 3"炼丹效率/成功率有所提升"是否有具体数值标准？ [Measurability, Spec §US3 验收场景3]

---

## 场景覆盖度

### 主要流程 (Happy Path)

- [ ] CHK037 - 资源存取的完整流程是否已定义？（创建宗门→存入→查询→取出） [Coverage, Spec §US1]
- [ ] CHK038 - 任务生命周期的完整流程是否已定义？（创建→发布→领取→执行→提交→完成→奖励） [Coverage, Spec §US2]
- [ ] CHK039 - 建筑升级的完整流程是否已定义？（检查条件→消耗资源→升级→效果生效） [Coverage, Spec §US3]

### 异常流程 (Exception Path)

- [ ] CHK040 - 宗门解散时，资源、任务、建筑的清理需求是否已定义？ [Gap, Spec §边界情况]
- [ ] CHK041 - 成员被移除时，其进行中的任务处理需求是否已定义？ [Gap, Spec §边界情况]
- [ ] CHK042 - 系统异常导致事务中断时的恢复需求是否已定义？ [Gap, Recovery]

### 恢复流程 (Recovery Path)

- [ ] CHK043 - 任务失败后的重试机制需求是否已定义？ [Gap, Spec §FR-011]
- [ ] CHK044 - 建筑升级失败后的回滚机制需求是否已定义？ [Gap, Recovery]

---

## 边缘情况覆盖度

- [ ] CHK045 - 宗门资源为零时，所有依赖资源的操作是否有明确处理？ [Edge Case, Spec §边界情况]
- [ ] CHK046 - 弟子已领取 5 个任务时，领取第 6 个的错误提示是否已定义？ [Edge Case, Contract §acceptTask]
- [ ] CHK047 - 建筑达到最高等级时，升级按钮/API 的行为是否已定义？ [Edge Case, Gap]
- [ ] CHK048 - 同一弟子多次领取同一任务的处理是否已定义？ [Edge Case, Contract §acceptTask]
- [ ] CHK049 - 任务领取人数刚好达到上限时，最后一人领取的并发处理是否已定义？ [Edge Case, Spec §边界情况]

---

## 非功能性需求

### 性能

- [ ] CHK050 - "60 fps 游戏循环兼容"的性能约束是否有具体的响应时间要求？ [Clarity, Plan §性能目标]
- [ ] CHK051 - "单宗门 100+ 成员，100+ 任务并发"的性能指标是否可测量？ [Measurability, Plan §规模/范围]

### 可测试性

- [ ] CHK052 - 三个服务的单元测试覆盖率目标是否已定义？ [Gap, 测试要求]
- [ ] CHK053 - 服务间集成测试的范围和用例是否已定义？ [Gap, 测试要求]

### ECS 框架规范

- [ ] CHK054 - 所有组件是否遵循 "sealed class" 或 "value class" 规范？ [Consistency, Plan §章程检查]
- [ ] CHK055 - 关系数据类（TaskProgress、OwnedBy）的使用是否符合 ECS 关系系统规范？ [Consistency, Plan §IV. 关系系统优先]

---

## 依赖关系和假设

- [ ] CHK056 - "复用 InventoryService 管理宗门仓库"的前提条件是否已验证？ [Dependency, Plan §服务依赖]
- [ ] CHK057 - "复用 LevelingService 管理建筑等级"的接口兼容性是否已确认？ [Dependency, Plan §服务依赖]
- [ ] CHK058 - 假设 "功法学习不消耗功法本体"是否在代码中正确实现？ [Assumption, Spec §假设7]
- [ ] CHK059 - 假设 "福利按日结算"的时区和重置时间是否已定义？ [Assumption, Spec §假设6]

---

## 歧义和冲突

- [ ] CHK060 - FR-004 "明确提示"与 Contract 中 `IllegalStateException` 是否存在表述不一致？ [Conflict, Spec vs Contract]
- [ ] CHK061 - 建筑"容量"在不同上下文中（建筑容量 vs 宗门建筑数量）的含义是否有歧义？ [Ambiguity, Contract §getBuildingCapacity]
- [ ] CHK062 - "资源"一词是否需要与"物品"进行术语统一？ [Ambiguity, 术语定义]

---

## 代码规范合规性

- [ ] CHK063 - 新增代码是否位于正确的包路径下？（`cn.jzl.sect.ecs.*`） [Consistency, Plan §项目结构]
- [ ] CHK064 - Addon 注册模式是否符合项目规范？（使用 `createAddon()` 创建） [Consistency, Plan §III. 框架一致性]
- [ ] CHK065 - 依赖注入是否通过 `world.di.instance<T>()` 正确获取？ [Consistency, Plan §技术约束]
- [ ] CHK066 - 是否所有公开 API 都有 KDoc 注释？ [Gap, 代码规范]

---

## 备注

- 完成项目时勾选: `[x]`
- 内联添加评论或发现的具体问题
- 此检查清单应在代码实现前由作者自检，确保需求质量

**检查清单统计**:
- 总项目数: 66
- 需求完整性: 14 项
- 需求清晰度: 10 项
- 需求一致性: 5 项
- 验收标准质量: 7 项
- 场景覆盖度: 8 项
- 边缘情况覆盖度: 5 项
- 非功能性需求: 6 项
- 依赖关系和假设: 4 项
- 歧义和冲突: 3 项
- 代码规范合规性: 4 项

