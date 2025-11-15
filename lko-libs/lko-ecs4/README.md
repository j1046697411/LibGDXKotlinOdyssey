# lko-ecs4 概览与设计说明

## 项目目标与范围
- 提供一个跨平台（JVM/JS）的高性能 ECS 核心库，围绕实体（Entity）、组件标识（ComponentId）、关系（Relation）、原型（Archetype）与表（Table）实现紧凑的数据布局与操作。
- 通过依赖注入（DI）组装 `World`，支持实体生命周期管理与组件/关系的增删与数据读写。
- 以可扩展、可测试为导向，后续引入查询系统、系统调度、事件、序列化等能力。

## 快速开始（概念）
```kotlin
import cn.jzl.di.DIMainBuilder
import cn.jzl.ecs.world
import cn.jzl.ecs.EntityId
import cn.jzl.ecs.Relation
import cn.jzl.ecs.component.ComponentService
import cn.jzl.ecs.component.Components
import cn.jzl.ecs.EntityId as EID
import cn.jzl.ecs.Relation as Rel
import cn.jzl.ecs.entity.EntityService
import cn.jzl.ecs.ArchetypeService

val w = world {
    this bind singleton { new(::ArchetypeService) }
    this bind singleton { new(::ComponentService) }
    this bind singleton { new(::Components) }
}

val entity = w.entityService.create()
val positionId = w.componentService.getOrRegisterComponentIdForClass(Position::class)
val rel = Rel(positionId, entity.entityId)
cn.jzl.ecs.RelationService(w).addRelation(entity.entityId, rel, Position(0f, 0f))
```

## 架构与核心类型
- `World`：DI 容器承载的服务集合（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/ECS.kt:11-22；lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:214-223）。
- `EntityId`：24 位 id + 8 位 version，避免悬挂引用（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:16-24）。
- `Relation(kind: ComponentId, target: EntityId)`：实体与组件类型的关联（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:26-36）。
- `EntityType`：排序的关系集合，确定原型（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:79-117）。
- `Archetype` 与 `Table`：原型与其列式表数据（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:147-165,168-212）。
- `ArchetypeService`：原型缓存与创建（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:119-145）。
- `EntityService`/`EntityStore`：实体生命周期与记录管理（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityService.kt:12-45；lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityStoreImpl.kt:9-64）。
- `ComponentService`/`Components`：类型到 `ComponentId` 的映射与内置标识（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/component/ComponentService.kt:8-16；lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/component/Components.kt:7-16,18）。

## 数据结构与算法
- 原型驱动布局：实体添加/移除关系触发在原型间迁移，保证同构实体的列式紧存储与良好局部性。
- 快速容器与位图：`FastList` 与 `BitSet` 管理 id 与活跃集，插入/访问近 O(1)，迁移线性拷贝。
- 回收与升级：销毁后 id 回收、`version` 递增，避免旧引用误用（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/entity/EntityStoreImpl.kt:47-53,63-64）。

## API 与使用建议
- DI 绑定：在 `world {}` 中显式绑定 `ArchetypeService`、`ComponentService`、`Components`。
- 关系操作：使用 `RelationService(world)` 的 `addRelation/removeRelation`；携带数据时通过带 `data` 重载。
- 数据访问：建议提供按 `entityId` 与 `ComponentId` 的 get/set/has 封装（当前尚缺公开 API，见未完成功能）。
- 查询与系统：在引入查询系统与系统调度后，通过选择器获取实体集合并在系统更新周期中处理。

## 未完成功能规划
- DI 默认绑定完善：`world()` 未默认绑定 `ArchetypeService`、`ComponentService`、`Components`，与 `World` 期望不一致（lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/ECS.kt:11-19；lko-ecs4/src/commonMain/kotlin/cn/jzl/ecs/EntityId.kt:214-223）。
- 组件数据访问 API：公开 get/set/has 封装，避免直接表索引。
- 关系查询 API：查询某实体的关系集合、按 `ComponentId` 过滤并读取数据。
- 实体/组件查询系统：支持包含/排除条件的选择器，返回可迭代集合或增量视图。
- 系统与调度：引入 `System` 概念、帧循环、依赖排序与协程并发。
- 事件机制：实体创建/销毁、关系增删、组件数据变更事件。
- 序列化/反序列化：保存/加载世界状态，支持 JVM/JS 一致性。
- 并发安全：明确线程模型与同步策略，补充基准。
- 跨平台适配：JS 环境下验证位运算与集合实现兼容。
- 测试与基准：单元测试与基准覆盖实体生命周期、原型迁移、关系操作。

## 里程碑与验收
- M1 DI补全与基础API（1 周）：最小示例可创建实体、添加关系并读写数据。
- M2 查询系统（1-2 周）：选择器稳定正确并具备基准。
- M3 系统调度与事件（2 周）：系统可订阅事件并按顺序运行。
- M4 序列化与跨平台（1 周）：JVM/JS 状态一致加载与运行。
- M5 并发与性能（1 周）：并发场景下无竞态，性能达标。
- M6 文档与示例（持续）：完整 KDoc 与端到端示例工程。

## 依赖与兼容性
- 依赖 `lkoDi` 与 `lkoDatastructure`，需关注跨平台行为与 JS 位运算兼容（lko-ecs4/build.gradle.kts:12-18）。

## 维护与测试建议
- 在每次原型迁移与关系操作处补充测试与基准，关注迁移开销与表紧凑性。
- 引入事件后对增量查询的一致性进行稳定性验证。