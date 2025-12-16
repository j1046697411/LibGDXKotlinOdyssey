<!--
=== 同步影响报告 ===
版本更改: 1.1.0 → 1.2.0 (新增质量保证原则)
修改的原则列表:
  - I. ECS 优先 (保持)
  - II. 服务复用优先 (保持)
  - III. 框架一致性 (保持)
  - IV. 关系系统优先 (保持)
  - V. 属性系统优先 (保持)
  - VI. 质量保证优先 (新增)
添加的部分:
  - 核心原则新增1条（VI）
需要更新的模板:
  - .specify/templates/plan-template.md ✅ 已更新
  - .specify/templates/spec-template.md ✅ 无需更新
  - .specify/templates/tasks-template.md ✅ 已更新
后续 TODO: 无
========================
-->

# LibGDXKotlinOdyssey 项目章程

## 核心原则

### I. ECS 优先

游戏逻辑框架 **必须** 优先使用 `lko-ecs4` 模块实现。

**强制规则**:
- 所有游戏实体 **必须** 使用 `World.entity {}` 创建和管理
- 游戏对象的数据 **必须** 通过 Component 存储，使用 `sealed class` 或 `@JvmInline value class` 定义
- 实体间的关系 **必须** 使用 Relation 机制表达（如父子关系、所有权关系）
- 游戏系统逻辑 **必须** 通过 System（`system().exec()`）或事件观察（`observe().exec()`）实现
- 实体查询 **必须** 使用 `world.query {}` 配合 `EntityQueryContext` 实现
- 功能模块 **必须** 使用 `createAddon()` 封装，通过 `install()` 声明依赖

**理由**: lko-ecs4 提供高性能的原型驱动布局、类型安全的关系架构、以及完善的查询和事件系统，是项目的核心游戏逻辑框架。

### II. 服务复用优先

能复用的服务 **必须** 优先使用已有的服务实现。

**已有可复用服务清单**:
| 服务 | 模块 | 功能描述 |
|------|------|----------|
| `MoneyService` | `moneyAddon` | 货币管理、转账 |
| `LevelingService` | `levelingAddon` | 等级、经验值、升级事件 |
| `CharacterService` | `characterAddon` | 角色创建、管理 |
| `ItemService` | `itemAddon` | 物品预制体、物品创建、拆分 |
| `MarketService` | `marketAddon` | 市场、寄售、收购 |
| `SectService` | `sectAddon` | 宗门、成员、建筑 |
| `AttributeService` | (内置) | 属性管理 |
| `Countdown` | `countdownAddon` | 倒计时系统 |

**强制规则**:
- 新功能开发前 **必须** 检查上述服务是否已提供所需能力
- 如需扩展现有服务，**应该** 优先通过继承或组合方式扩展，而非重新实现
- 服务间依赖 **必须** 通过 DI 注入（`world.di.instance<Service>()`）
- 新增服务 **必须** 更新此清单

**理由**: 避免重复实现、保持代码一致性、降低维护成本。

### III. 框架一致性

新增加的逻辑 **必须** 符合 `lko-sect` 模块的已有框架和模式。

**Addon 模式规范**:
```kotlin
val exampleAddon = createAddon("example", { /* 配置 */ }) {
    install(dependencyAddon)           // 声明依赖
    injects { 
        this bind singleton { new(::ExampleService) }  // 注入服务
    }
    components { 
        world.componentId<MyComponent> { it.tag() }    // 注册组件
    }
    entities { /* 创建预制体 */ }
    systems { /* 注册系统 */ }
}
```

**服务类规范**:
```kotlin
class ExampleService(world: World) : EntityRelationContext(world) {
    private val otherService by world.di.instance<OtherService>()
    
    @ECSDsl
    fun createExample(...): Entity = world.entity { ... }
}
```

**组件定义规范**:
- 标签组件：`sealed class MyTag`
- 数据组件：`@JvmInline value class MyData(val value: T)` 或 `data class MyData(...)`
- 事件组件：`sealed class OnMyEvent` 或 `data class OnMyEvent(...)`

**强制规则**:
- Addon **必须** 使用 `createAddon()` 函数创建
- 服务类 **必须** 继承 `EntityRelationContext(world)`
- 组件类型配置 **必须** 在 `components {}` 块中完成
- 服务注入 **必须** 在 `injects {}` 块中使用 `singleton { new(::Service) }` 模式
- 事件发布 **必须** 使用 `world.emit<EventType>(entity)` 或 `world.emit(entity, EventData(...))`
- 事件订阅 **必须** 使用 `world.observe<EventType>().exec { ... }` 模式
- 查询上下文 **必须** 继承 `EntityQueryContext(world)` 并实现 `configure()`

**理由**: 保持代码风格统一，降低学习成本，便于团队协作和代码审查。

### IV. 关系系统优先

游戏实体之间的关系 **必须** 优先使用 ECS 的关系系统（Relation）构建。

**ECS 关系系统核心 API**:
| 方法 | 用途 |
|------|------|
| `entity.addRelation<RelationType>(target)` | 添加无数据关系 |
| `entity.addRelation(target, data)` | 添加带数据关系 |
| `entity.getRelation<T>(target)` | 获取关系数据 |
| `entity.getRelationUp<RelationType>()` | 获取向上关系目标 |
| `entity.getRelationsWithData<T>()` | 获取所有带数据的关系 |
| `entity.removeRelation<RelationType>(target)` | 移除关系 |
| `entity.hasRelation(relation)` | 检查关系是否存在 |

**已有关系类型清单**:
| 关系类型 | 用途 | 示例 |
|----------|------|------|
| `OwnedBy` | 所有权关系 | 物品属于角色、建筑属于宗门 |
| `MemberData` | 成员关系（带数据） | 宗门成员、角色贡献度 |
| `Countdown` | 倒计时关系 | 实体倒计时 |
| `AttributeValue` | 属性值关系 | 等级、经验值 |

**强制规则**:
- 实体之间的所有权关系 **必须** 使用 `addRelation<OwnedBy>(owner)` 表达
- 带额外数据的关系 **必须** 使用 `addRelation(target, data)` 模式
- 一对多关系查询 **必须** 使用 `getRelationsWithData<T>()` 获取所有关联
- 反向查询 **必须** 使用 `getRelationUp<T>()` 获取关系目标
- 新增关系类型 **必须** 更新此清单
- **禁止** 使用自定义字段存储实体引用（如 `ownerId: EntityId`），应使用关系系统

**理由**: ECS 关系系统提供类型安全的实体关联、自动的生命周期管理（级联删除）、高效的关系查询，避免悬空引用问题。

### V. 属性系统优先

扩展的属性 **必须** 优先使用属性系统（AttributeService）管理。

**属性系统核心概念**:
- **属性预制体**: 使用 `attributePrefab()` 创建，代表一个属性类型（如"等级"、"经验"）
- **属性值关系**: 使用 `AttributeValue` 作为实体与属性预制体之间的关系数据
- **属性查询**: 使用 `entity.getRelation<AttributeValue>(attributeEntity)` 获取属性值

**属性定义规范**:
```kotlin
attributes {
    register(Named("level"), Description("角色等级"))
    register(Named("experience"), Description("经验值"))
    register(Named("health"), Description("生命值"))
}
```

**属性使用规范**:
```kotlin
// 获取属性预制体
val attributeLevel = attributeService.attribute(Named("level"))

// 读取实体属性值
val level = entity.getRelation<AttributeValue?>(attributeLevel) ?: AttributeValue.ONE

// 更新实体属性值
entity.addRelation(attributeLevel, AttributeValue(newLevel))
```

**强制规则**:
- 数值型可变属性（等级、经验、生命值、攻击力等） **必须** 使用属性系统管理
- 属性预制体 **必须** 在 Addon 的 `attributes {}` 块中注册
- 属性值 **必须** 使用 `AttributeValue` 类型存储
- 属性更新 **必须** 通过关系系统的 `addRelation()` 方法
- **禁止** 为每个属性定义单独的组件类型（如 `HealthComponent`、`AttackComponent`），应使用属性系统

**理由**: 属性系统提供统一的数值管理、动态属性注册、可查询的属性元数据（名称、描述），避免组件类型膨胀。

### VI. 质量保证优先

代码质量 **必须** 通过严格的测试覆盖率和通过率来保证。

**强制规则**:
- 用例覆盖率 **必须** 达到代码的 80%
- 测试用例通过率 **必须** 达到 100%

**理由**: 确保系统的稳定性和可靠性，减少回归错误，符合项目质量高标准要求。

## 技术约束

**语言与平台**:
- 语言：Kotlin (Multiplatform)
- 平台：JVM (Desktop)、JS (Web)
- 构建：Gradle (Kotlin DSL)
- 依赖注入：lko-di 模块

**项目结构**:
```
lko-libs/          # 核心库
  lko-ecs4/        # ECS 框架 (核心)
  lko-di/          # 依赖注入
  lko-datastructure/ # 数据结构
  ...
lko-sect/          # 游戏逻辑模块
  src/commonMain/kotlin/cn/jzl/sect/
    ecs/           # ECS 组件、服务、Addon
    ui/            # UI 组件
lko-gdx-libs/      # LibGDX 相关库
lko-graph-libs/    # 图形相关库
```

**测试要求**:
- 核心服务 **应该** 有单元测试覆盖
- Addon 集成 **应该** 有集成测试验证

## 开发工作流程

**新功能开发流程**:
1. 分析需求，确定是否可复用现有服务
2. 设计 Addon 结构，声明依赖关系
3. 定义组件（Component）和事件（Event）
4. 实现服务（Service），遵循框架规范
5. 注册系统（System）处理游戏循环逻辑
6. 编写测试验证功能

**代码审查检查点**:
- [ ] 是否使用了 lko-ecs4 的 ECS 模式？
- [ ] 是否复用了已有服务？
- [ ] Addon 结构是否符合规范？
- [ ] 服务类是否继承 EntityRelationContext？
- [ ] 组件定义是否使用正确的类型（sealed class / value class）？
- [ ] 实体间关系是否使用 ECS 关系系统（而非自定义字段）？
- [ ] 数值型属性是否使用属性系统（而非单独组件）？

## 治理

本章程 **优先于** 所有其他开发实践。任何对章程的修正需要：

1. 提出修正提案并说明理由
2. 更新章程文档
3. 同步更新相关模板和依赖文档
4. 记录版本变更

**合规性验证**:
- 所有 PR **必须** 验证是否符合核心原则
- 复杂性增加 **必须** 有明确的业务理由
- 新增服务 **必须** 更新可复用服务清单

**版本**: 1.2.0 | **批准日期**: 2025-12-13 | **最后修正**: 2025-12-16
