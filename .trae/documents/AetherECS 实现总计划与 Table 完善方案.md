## 现状评估
- 已具备关系模型：`EntityId`/`ComponentId`/`Relation`/`EntityType`/`Archetype`（`Relations.kt`），并修正了关系构造的角色位保留。
- `Archetype` 已提供 `dataHoldingType` 与惰性 `table`（列式存储）。`World` 可创建/销毁实体，增删关系并在原型间迁移。
- 需要将 `Table` 从基础列容器升级为 SoA 引擎的核心，完成类型化、行管理与数据迁移；并围绕需求文档补齐实体管理、组件注册、查询、关系与系统调度等模块。

## Table 完善（SoA 列式引擎）
1. 类型化列
- 为 `HOLDS_DATA` 关系建立类型化列：`ComponentArray<T>` 封装 `ObjectFastList<T?>`，提供 `get/set/ensureCapacity/size`。
- 建立 `Relation → 列` 的稳定索引：基于 `Archetype.dataHoldingType.indexOf(relation)`。
2. 行与实体映射
- `rowCount`、`addRow()/removeRow()` 使用 swap-remove 保持稠密；维护 `entity → row` 与 `row → entity` 双向索引。
- `World` 迁移时：按 `oldArchetype.table` → `newArchetype.table` 的列交集进行行级数据搬移。
3. 扩容与压缩
- `ensureCapacity(entityCount)` 扩展所有列与索引；`compact()` 执行碎片整理并稳定迭代顺序（可保留行交换策略）。
4. 数据 API
- `set/get(relation, row)` 与类型安全重载 `set/get(componentClass)`；非法关系（非 `HOLDS_DATA`）直接抛错。
5. 度量与校验
- `StorageMetrics` 汇总总内存、使用内存、实体数、列数；越界与空洞校验工具。

## 实体管理（EntityManager）
- `World` 实现 `EntityManager`：`createEntity()`、`createEntity(name)`、`destroyEntity()`、`isValid()`、`get/setEntityName()`。
- 支持 32/64 位 ID 切换（配置项），保留版本号（防重用错误）。
- 名称索引：`name → EntityId` 与反向映射；销毁时清理。

## 组件注册与操作（ComponentRegistry/ComponentOperations）
- `ComponentRegistry`：`KClass<T> → ComponentId` 注册，保留角色位；保存 `ComponentInfo`（大小、对齐、是否关系、是否持数）。
- `ComponentOperations`：基于 `Relation(ComponentId, target)` 表达组件；
  - `addComponent(entity, T)` → 为该组件生成/映射目标（自身或指定），设置 `HOLDS_DATA` 并写入 `Table`；
  - `get/remove/hasComponent` 提供按 `KClass<T>` 与 `ComponentId` 两种入口；
  - 批量操作走 SoA 列批量写入，支持 SIMD 预留。

## 原型系统（ArchetypeManager）
- 原型缓存：`DefaultArchetypeProvider` 基于 `EntityType` 键；
- `ArchetypeManager`：`getArchetype(components)`、`findArchetypes(Query)`、`getEntityArchetype(entity)`；
- 边图：公开 `getAddEdges/getRemoveEdges`（来自 `Archetype` 内部缓存）；
- 迁移：`World` 在增删关系时通过交集列复制与索引重建，行级数据保持一致。

## 查询系统（QueryBuilder/Query/Iterator）
- QueryBuilder：`with/without/optional`（类与关系）、`orderBy`、`withRelation(relation, target|targetType)`。
- Query 执行：
  - 遍历匹配的 `Archetype`，在其 `Table` 上按行迭代；
  - `iterator()` 提供 `getComponent`（类型安全）与 `getComponentUnchecked(index)`（索引直取）。
- 性能：`cached()` 基于组件签名缓存原型列表；`parallel()` 分片原型与行。
- 优化器：生成简单计划（原型过滤、列交集、排序策略），预留复杂策略。

## 关系系统（RelationshipManager）
- 基础操作：`add/remove/hasRelation`，`getTargets/getSources/getRelationData`；数据与表列一致。
- 索引：维护 `source → (relation → targets)` 与 `target → (relation → sources)` 的稀疏索引结构；
- 高级特性：
  - 传递/对称/反身/继承为可选策略，配置化启用；
  - 通过派生索引或计算边支持，避免每次写入级联同步造成高开销。

## 系统执行（System/SystemScheduler）
- `System` 定义 `phase/priority/execute`，提供内置查询辅助（inline + reified）。
- 调度器：
  - 注册/反注册、按 `phase` 执行与 `executeAll()`；
  - 依赖管理与明确顺序；`enableParallelExecution` 控制并行。
- 性能测量挂钩：对系统执行进行时间采样。

## 事件系统（EventManager/ReactiveSystem）
- 发布/订阅与句柄；系统内反应式类基于 `EventManager` 自动订阅与处理。
- 支持帧内批处理与跨帧队列。

## 调试与序列化（DebugTools/Profiler/WorldSerializer）
- DebugTools：世界验证、组件存在性与内存检查、实体检查与类型视图。
- Profiler：帧级、系统级与查询级测量；帧报告输出。
- 序列化：世界快照、增量变更与应用；支持 `binary/json/protobuf` 可插拔策略。

## 性能与内存优化
- 真正的 SoA：所有数据在列内连续存储；行稠密、索引简洁；
- 预取友好：`ensureCapacity` 进行增长策略与对齐；
- 内存池：为行与列块提供简单池化，减少分配；
- 查询缓存：可配置大小（默认 ≤ 64MB），失效策略可控。

## 配置与 API 风格
- 世界建造器：`World.create { components { ... } systems { ... } config { ... } }`；
- 32/64 位 ID、并行系统与存储策略在 `config` 中管理；
- 组件注册、系统注册与模块装配（`ECSModule`）。

## 测试与里程碑
- 单元测试：实体管理、组件操作、查询、关系、存储；
- 集成测试：完整场景与性能基准、并发与内存泄漏；
- 里程碑：
  - 阶段 1（核心）：实体管理、组件注册与操作、基础 SoA、简单查询；
  - 阶段 2（高级）：关系系统、查询优化、调度器、事件系统；
  - 阶段 3（性能）：内存布局优化、查询缓存、批量操作、基准测试；
  - 阶段 4（工具）：调试工具、序列化、模块系统、文档与示例；
  - 阶段 5（生产）：稳定性测试、性能调优、API 冻结与发布。

## 验收与成功标准
- 达到目标性能与内存指标；核心操作 100% 类型安全；全面测试覆盖；完整文档与示例。