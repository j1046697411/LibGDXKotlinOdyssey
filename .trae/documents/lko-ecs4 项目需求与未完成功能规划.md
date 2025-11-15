## 项目目标与范围
- 提供一个跨平台（JVM/JS）的高性能 ECS 核心库，围绕实体（Entity）、组件标识（ComponentId）、关系（Relation）、原型（Archetype）与表（Table）实现数据布局与操作。
- 通过依赖注入（DI）组装 `World`，支持实体生命周期管理与组件/关系的增删改查。
- 以可扩展、可测试为导向，后续引入查询系统、系统调度、事件、序列化等能力。

## 现有能力梳理
- World 构建与 DI 绑定：`world(configuration)` 创建 DI 容器并绑定核心服务，返回 `World`（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/ECS.kt:11-22）。
- 实体标识：`EntityId` 使用 24 位 id + 8 位 version（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:16-24）。
- 关系模型：`Relation(kind: ComponentId, target: EntityId)` 表示实体与组件类型的关联（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:26-36）。
- 原型与表：`Archetype` 由 `EntityType`（排序的 `Relation` 集合）确定，表 `Table` 以列式存储组件数据（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:147-165,168-212）。
- ArchetypeService：根据 `EntityType` 缓存/创建 `Archetype`，提供根原型（空类型）（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:119-145）。
- 实体服务：创建/销毁实体、跟踪实体的（archetype,row）记录、在实体所属原型表上执行操作（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityService.kt:12-45）。
- 实体存储：维护活跃实体集合、回收与升级 version（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityStoreImpl.kt:9-64）。
- 关系服务：在原型间迁移以添加/移除关系，并支持携带数据的设值（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:39-77）。
- 组件标识服务：将 `KClassifier` 映射为 `ComponentId`（本质是实体 id）（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/component/ComponentService.kt:8-16）。
- 组件集合：内置 `any` 与内部 `Component` 类型标识（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/component/Components.kt:7-16,18）。
- 构建配置：KMP，依赖 `lkoDi` 与 `lkoDatastructure` 等（lko-ecs4/build.gradle.kts:3-26）。

## 典型使用流程（概念）
- 初始化世界：在 `world { ... }` 中绑定所需服务与组件集合。
- 创建实体：`entityService.create()` 返回 `Entity` 并记录在根原型中。
- 添加关系：通过 `RelationService(world).addRelation(entityId, relation, data?)` 在原型间迁移并设置数据。
- 访问数据：通过原型表的（row,column）可读写，但当前缺少公开 API 封装。

## 设计与数据结构要点
- 原型驱动布局：实体添加/移除关系触发在原型间迁移，保证同构实体的列式紧存储。
- 高效集合与位图：使用 `FastList` 与 `BitSet` 管理 id 与活跃集，追求 O(1) 插入/访问，迁移时线性拷贝。
- 实体回收与版本：销毁后 id 回收，提高内存局部性并用 `version` 防止悬挂引用（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityStoreImpl.kt:47-53,63-64）。

## 未完成功能清单（建议）
- DI 默认绑定完善：`world()` 未默认绑定 `ArchetypeService`、`ComponentService`、`Components`，与 `World` 的期望不一致（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/ECS.kt:11-19 与 lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:214-223）。
- 组件数据访问 API：提供按 `entityId` 与 `ComponentId` 的 get/set/has 封装，避免直接操作表索引。
- 关系查询 API：查询某实体的关系集合、按 `ComponentId` 过滤、读取携带数据。
- 实体/组件查询系统：支持“包含/不包含”多个组件条件的选择器，返回可迭代集合（或增量视图）。
- 系统与调度：引入 `System` 概念，按依赖或优先级调度，支持帧循环与协程并发。
- 事件机制：实体创建/销毁、关系添加/移除、组件数据变更事件，用于系统联动与增量查询。
- 序列化/反序列化：持久化 `World`（实体、原型、表数据），用于保存/加载或网络同步。
- 并发安全：定义线程模型与写入策略（单写多读/分区），引入原子与锁策略或作业系统。
- 跨平台适配：在 JS 环境下验证数据结构与位运算的兼容性，提供示例。
- 测试与基准：补充单元测试（实体生命周期、原型迁移、关系操作）与基准测试。
- 文档与示例：完善 KDoc、模块概述与使用示例代码片段，确保易用性。

## 里程碑与验收标准
- M1 DI补全与基础API（1 周）：
  - 完成 `ArchetypeService`/`ComponentService`/`Components` 的默认绑定；提供组件数据 get/set/has。
  - 验收：最小示例可创建实体、添加关系并读写组件数据。
- M2 查询系统（1-2 周）：
  - 实现按包含/排除组件的选择器与迭代器，支持快照与增量。
  - 验收：给定条件能稳定、正确返回实体集合，含基准对比。
- M3 系统调度与事件（2 周）：
  - 引入 `System` 接口、帧循环、事件总线，支持依赖排序与协程并发。
  - 验收：示例中系统能订阅事件并在帧内按顺序运行。
- M4 序列化与跨平台验证（1 周）：
  - 世界状态的保存/加载，JS 端兼容测试与示例。
  - 验收：同一状态在 JVM/JS 可一致加载并运行。
- M5 并发与性能优化（1 周）：
  - 定义线程模型，引入必要的同步或分区策略，补充基准。
  - 验收：并发场景下无数据竞态，性能达到预期。
- M6 文档与示例完善（持续）：
  - 完成 KDoc 与指南，提供端到端示例工程。
  - 验收：新用户可依据文档快速上手并通过测试。

## 风险与依赖
- 依赖 `lkoDi` 与 `lkoDatastructure` 的 API 稳定性与跨平台行为；JS 位运算与集合实现需特别关注。
- 查询与事件在原型迁移上的一致性与性能平衡，需要迭代打磨。

## 输出物
- 《lko-ecs4 概览与设计说明》文档（本草案内容为基础）。
- 《未完成功能规划》与里程碑计划。

请确认以上文档草案与规划；确认后将按里程碑推进并补充实现与测试。