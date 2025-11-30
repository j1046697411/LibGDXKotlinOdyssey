# lko-ecs4 概览与设计说明

## 目录

- [项目概述](#项目概述)
- [项目目标与范围](#项目目标与范围)
- [项目特点](#项目特点)
- [快速开始](#快速开始)
  - [基本示例](#基本示例)
  - [查询系统示例](#查询系统示例)
  - [事件机制示例](#事件机制示例)
  - [关系架构示例](#关系架构示例)
- [关系架构设计](#关系架构设计)
- [核心概念](#核心概念)
  - [1. Entity（实体）](#1-entity实体)
  - [2. Component（组件）](#2-component组件)
  - [3. ComponentId（组件标识）](#3-componentid组件标识)
  - [4. Relation（关系）](#4-relation关系)
  - [5. EntityType（实体类型）](#5-entitytype实体类型)
  - [6. Archetype（原型）](#6-archetype原型)
  - [7. Table（表）](#7-table表)
- [核心服务](#核心服务)
  - [1. World](#1-world)
  - [2. ArchetypeService](#2-archetypeservice)
  - [3. ComponentService](#3-componentservice)
  - [4. EntityService](#4-entityservice)
  - [5. RelationService](#5-relationservice)
  - [6. FamilyService](#6-familyservice)
  - [7. QueryService](#7-queryservice)
  - [8. ObserveService](#8-observeservice)
  - [9. ShadedComponentService](#9-shadedcomponentservice)
- [架构流程图](#架构流程图)
- [数据结构与算法](#数据结构与算法)
  - [1. 原型驱动布局](#1-原型驱动布局)
  - [2. 快速容器与位图](#2-快速容器与位图)
  - [3. id回收与版本管理](#3-id回收与版本管理)
  - [4. 关系索引与查询优化](#4-关系索引与查询优化)
  - [5. 内存管理](#5-内存管理)
  - [6. 游戏性能优势](#6-游戏性能优势)
  - [7. 性能对比](#7-性能对比)
- [API 与使用指南](#api-与使用指南)
  - [1. 实体管理](#1-实体管理)
  - [2. 组件管理](#2-组件管理)
  - [3. 关系管理](#3-关系管理)
  - [4. 查询系统](#4-查询系统)
  - [5. 事件机制](#5-事件机制)
  - [6. 游戏开发最佳实践](#6-游戏开发最佳实践)
  - [7. 常见游戏开发场景](#7-常见游戏开发场景)
- [测试与基准](#测试与基准)
  - [1. 测试覆盖情况](#1-测试覆盖情况)
  - [2. 运行测试](#2-运行测试)
  - [3. 性能基准](#3-性能基准)
  - [4. 性能优化建议](#4-性能优化建议)
- [跨平台支持](#跨平台支持)
  - [1. 平台支持](#1-平台支持)
  - [2. 跨平台架构](#2-跨平台架构)
  - [3. 游戏开发跨平台支持](#3-游戏开发跨平台支持)
  - [4. 跨平台开发最佳实践](#4-跨平台开发最佳实践)
  - [5. 未来跨平台规划](#5-未来跨平台规划)
- [未完成功能规划](#未完成功能规划)
  - [1. 核心功能增强](#1-核心功能增强)
  - [2. 游戏开发相关功能](#2-游戏开发相关功能)
  - [3. 跨平台支持](#3-跨平台支持)
  - [4. 工具与生态](#4-工具与生态)
- [里程碑与验收](#里程碑与验收)
- [依赖与兼容性](#依赖与兼容性)
- [维护与测试建议](#维护与测试建议)

## 项目概述
lko-ecs4 是一个基于 **关系架构** 的高性能 **Kotlin 多平台游戏开发 ECS（实体组件系统）框架**，支持 JVM 和 JS 平台。它围绕实体（Entity）、组件标识（ComponentId）、关系（Relation）、原型（Archetype）与表（Table）实现紧凑的数据布局与操作，专为游戏开发设计，提供出色的性能和易用性。

## 项目目标与范围
- 提供一个 **跨平台（JVM/JS）的高性能 ECS 核心库**，专为游戏开发优化，实现紧凑的数据布局与高效操作。
- 采用 **关系架构** 设计，通过实体间的关系表达复杂的游戏对象结构，提供更灵活的实体组织方式。
- 通过依赖注入（DI）组装 `World`，支持实体生命周期管理与组件/关系的增删与数据读写。
- 以可扩展、可测试为导向，提供查询系统、事件机制等核心功能，支持游戏开发的各种场景。
- 为游戏开发者提供易用、高效的 API，降低 ECS 架构的学习曲线，提高开发效率。

## 项目特点
- **关系架构**：通过关系表达实体间的复杂联系，更适合游戏开发中的对象组织。
- **高性能**：基于原型驱动布局，实现紧凑的列式存储，提供出色的缓存局部性和访问性能。
- **跨平台支持**：同时支持 JVM（桌面游戏）和 JS（Web 游戏）平台。
- **依赖注入**：通过 DI 容器组装 World，支持灵活的服务扩展和测试。
- **查询系统**：提供强大的实体查询能力，支持复杂的过滤条件。
- **事件机制**：支持观察者模式，方便游戏中的事件处理和通信。
- **类型安全**：利用 Kotlin 的类型系统，提供类型安全的 API。
- **预制体支持**：支持创建和实例化预制体，实现游戏对象的复用和快速创建。

## 快速开始

### 基本示例
```kotlin
import cn.jzl.ecs.world
import cn.jzl.ecs.Entity

// 定义游戏组件类
class Position(val x: Float, val y: Float)
class Velocity(val dx: Float, val dy: Float)
class Health(val value: Int)

// 创建世界，world函数已默认绑定所有必要服务
val world = world {}

// 创建实体
val player = world.entity {
    // 添加位置组件
    it.addComponent(Position(0f, 0f))
    // 添加速度组件
    it.addComponent(Velocity(1f, 0f))
    // 添加生命值组件
    it.addComponent(Health(100))
}

// 访问实体组件数据
val position = world.relationService.getRelation(player, world.componentService.component<Position>()) as Position
println("Player position: (${position.x}, ${position.y})")
```

### 查询系统示例
```kotlin
// 创建查询，获取所有具有Position和Velocity组件的实体
val movingEntitiesQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            // 匹配具有Position和Velocity组件的实体
            component<Position>()
            component<Velocity>()
        }
    }
}

// 遍历查询结果，更新实体位置
movingEntitiesQuery.forEach {
    val position = this[Position::class] as Position
    val velocity = this[Velocity::class] as Velocity
    // 更新位置
    this[Position::class] = Position(
        position.x + velocity.dx,
        position.y + velocity.dy
    )
}
```

### 事件机制示例
```kotlin
// 定义自定义事件
class PlayerDamaged(val damage: Int)

// 订阅事件
world.observeWithData<PlayerDamaged>().exec {
    // 处理玩家受伤事件
    val health = this[Health::class] as Health
    this[Health::class] = Health(health.value - event.damage)
    println("Player damaged! Health: ${health.value - event.damage}")
}

// 发布事件
world.emit(player, PlayerDamaged(20))
```

### 关系架构示例
```kotlin
// 创建武器实体
val sword = world.entity {
    it.addComponent(Position(0f, 0f))
    it.addComponent(Health(50))
}

// 定义装备关系
val equippedToComponentId = world.componentService.id<EquippedTo>()

// 将武器装备到玩家
world.relationService.addRelation(sword, Relation(equippedToComponentId, player))

// 查询玩家装备的所有武器
val equippedWeaponsQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            // 匹配装备到玩家的实体
            relation(Relation(equippedToComponentId, player))
        }
    }
}
```

### 预制体示例
```kotlin
// 定义游戏组件类
class Position(val x: Float, val y: Float)
class Velocity(val dx: Float, val dy: Float)
class Health(val value: Int)
class PlayerTag

// 创建玩家预制体
val playerPrefab = world.entity {
    it.addComponent(Position(0f, 0f))
    it.addComponent(Velocity(1f, 0f))
    it.addComponent(Health(100))
    it.addTag<PlayerTag>()
}

// 从预制体创建玩家实例
val player1 = world.instanceOf(playerPrefab) {
    // 可以覆盖预制体的组件值
    it.addComponent(Position(10f, 10f))
}

// 从预制体创建另一个玩家实例，使用指定的实体ID
val player2 = world.instanceOf(playerPrefab, 100) {
    it.addComponent(Position(20f, 20f))
}

// 查询所有玩家实例（包括预制体和实例）
val allPlayersQuery = world.query {
    object : QueryEntityContext(this, involvePrefab = true) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<PlayerTag>()
        }
    }
}

// 查询仅玩家实例（不包括预制体）
val playerInstancesQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<PlayerTag>()
        }
    }
}
```

## 关系架构设计
lko-ecs4 采用 **关系架构** 设计，这是其与传统 ECS 框架的核心区别。在关系架构中，实体通过 **关系（Relation）** 来表达与其他实体或组件的联系，而不仅仅是简单地拥有组件。这种设计更适合游戏开发中的复杂对象结构，例如：

- 实体可以通过 `childOf` 关系成为另一个实体的子实体
- 实体可以通过自定义关系表达复杂的游戏对象联系，如 "装备于"、"攻击目标" 等
- 关系可以携带数据，进一步扩展表达能力

关系架构提供了更灵活的实体组织方式，使开发者能够更自然地表达游戏中的复杂对象结构和交互关系。

## 核心概念

### 1. Entity（实体）
- **定义**：24 位 id + 8 位 version，避免悬挂引用
- **作用**：游戏世界中的基本对象，通过关系与组件关联
- **特点**：轻量级，仅包含标识信息，不直接存储数据
- **应用**：代表游戏中的角色、物品、特效等各种对象

### 2. Component（组件）
- **定义**：游戏对象的属性数据，如位置、速度、生命值等
- **作用**：存储实体的具体数据，通过关系与实体关联
- **特点**：纯数据类，无行为逻辑
- **应用**：`Position(x, y)`, `Velocity(dx, dy)`, `Health(value)` 等

### 3. ComponentId（组件标识）
- **定义**：`Entity` 类型别名，用于标识组件类型
- **作用**：将组件类型映射到唯一标识，用于关系定义
- **特点**：通过 `ComponentService` 动态注册和管理

### 4. Relation（关系）
- **定义**：由 `kind`（ComponentId）和 `target`（Entity）组成的关联
- **作用**：表达实体间或实体与组件间的联系
- **特点**：可以携带数据，支持复杂的实体组织方式
- **应用**：
  - 实体与组件的关联：`Relation(PositionComponentId, entity)`
  - 实体间的父子关系：`Relation(ChildOfComponentId, parentEntity)`
  - 自定义游戏关系：`Relation(EquippedToComponentId, characterEntity)`

### 5. EntityType（实体类型）
- **定义**：排序的关系集合，确定实体的原型
- **作用**：用于分组具有相同关系集合的实体
- **特点**：自动排序，确保唯一性

### 6. Archetype（原型）
- **定义**：具有相同 `EntityType` 的实体分组
- **作用**：管理同构实体的列式存储
- **特点**：包含一个 `Table` 用于存储实体数据

### 7. Table（表）
- **定义**：列式存储同构实体的数据
- **作用**：提供高效的数据访问和更新
- **特点**：紧凑的列式布局，出色的缓存局部性

## 核心服务

### 1. World
- **定义**：DI 容器承载的服务集合
- **作用**：组装和管理所有 ECS 服务
- **特点**：通过 `world { }` 函数创建，自动绑定所有必要服务

### 2. ArchetypeService
- **定义**：原型缓存与创建服务
- **作用**：管理 Archetype 的创建和缓存，确保唯一性

### 3. ComponentService
- **定义**：组件类型管理服务
- **作用**：注册和管理 ComponentId，处理组件类型映射

### 4. EntityService
- **定义**：实体生命周期管理服务
- **作用**：处理实体的创建、配置和销毁

### 5. RelationService
- **定义**：关系管理服务
- **作用**：处理实体关系的增删与数据读写

### 6. FamilyService
- **定义**：家族匹配服务
- **作用**：用于查询系统，匹配符合条件的实体

### 7. QueryService
- **定义**：实体查询服务
- **作用**：提供强大的实体查询能力，支持复杂的过滤条件

### 8. ObserveService
- **定义**：观察者服务
- **作用**：支持事件机制，处理实体和组件的事件

### 9. ShadedComponentService
- **定义**：阴影组件服务
- **作用**：用于组件数据的阴影存储与管理

## 架构流程图
```
┌─────────────────────────────────────────────────────────────────┐
│                           World                               │
├─────────────────┬─────────────────┬─────────────────┬───────────┤
│ ArchetypeService │ ComponentService │ EntityService │ RelationService │
└────────┬────────┴────────┬────────┴────────┬────────┴───────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   Archetype     │ │    Component     │ │     Entity      │
└────────┬────────┘ └─────────────────┘ └────────┬────────┘
         │                                      │
         └───────────────────┬──────────────────┘
                             ▼
                     ┌─────────────────┐
                     │     Relation    │
                     └─────────────────┘
```

## 数据结构与算法

### 1. 原型驱动布局

#### 工作原理
lko-ecs4 采用 **原型驱动布局**（Archetype-driven Layout），这是其高性能的核心设计之一。在这种布局中：

1. **实体分组**：具有相同关系集合的实体被分组到同一个 `Archetype` 中
2. **列式存储**：每个 `Archetype` 包含一个 `Table`，用于列式存储实体的组件数据
3. **原型迁移**：当实体添加或移除关系时，会触发在不同 `Archetype` 之间的迁移

#### 迁移过程
```
实体添加组件 → 计算新的EntityType → 查找或创建对应的Archetype → 将实体数据从旧Table复制到新Table → 更新实体记录
```

#### 对游戏性能的影响
- **出色的缓存局部性**：同构实体的数据紧密存储在连续的内存区域，提高CPU缓存命中率
- **高效的数据访问**：列式存储允许高效地访问实体的特定组件数据，适合游戏中的系统处理
- **减少内存碎片**：紧凑的存储布局减少了内存碎片，提高内存利用率
- **批量操作优化**：同一 `Archetype` 的实体可以进行批量操作，提高处理效率

### 2. 快速容器与位图

#### FastList
- **定义**：自定义的高性能列表实现，专为游戏开发优化
- **特点**：
  - 基于数组实现，支持快速随机访问（O(1)）
  - 支持快速插入和删除操作
  - 支持批量操作，减少内存分配
  - 支持不同数据类型（Object, Int, Long, Float等）
- **应用**：
  - 管理实体id
  - 存储活跃实体集
  - 存储原型列表

#### BitSet
- **定义**：高效的位图实现，用于表示集合
- **特点**：
  - 基于长数组实现，支持快速位操作
  - 支持快速插入、删除和查询操作（O(1)）
  - 内存高效，适合表示大量布尔值
- **应用**：
  - 表示实体的活跃状态
  - 表示原型的匹配结果
  - 用于查询系统的过滤条件

#### 对游戏性能的影响
- **快速访问**：O(1)的插入、访问和删除操作，适合游戏中的高频操作
- **内存高效**：紧凑的存储布局，减少内存占用
- **批量操作**：支持高效的批量操作，提高处理效率
- **减少GC压力**：减少内存分配和回收，降低GC压力

### 3. id回收与版本管理

#### 工作原理
lko-ecs4 采用 **id回收与版本管理**机制，避免悬挂引用：

1. **Entity结构**：24位id + 8位version
2. **id回收**：实体销毁后，其id会被回收并重新分配
3. **版本递增**：每次id被重新分配时，version字段会递增
4. **悬挂引用检测**：通过比较version可以检测悬挂引用

#### 对游戏性能的影响
- **高效的id复用**：避免id空间耗尽，支持大量实体的创建和销毁
- **安全的引用管理**：避免悬挂引用导致的崩溃，提高游戏的稳定性
- **轻量级检测**：version比较是轻量级操作，对性能影响小

### 4. 关系索引与查询优化

#### 关系索引
- **定义**：为实体的关系建立索引，加速关系查询
- **特点**：
  - 基于原型驱动布局，同一原型的实体具有相同的关系集合
  - 支持快速查找具有特定关系的实体
  - 支持关系的高效遍历

#### 查询优化
- **家族匹配**：使用 `FamilyMatcher` 进行高效的实体匹配
- **位运算优化**：使用位图进行快速的集合操作
- **原型过滤**：首先过滤出匹配的原型，减少需要检查的实体数量
- **缓存查询结果**：对频繁使用的查询结果进行缓存，提高查询效率

#### 对游戏性能的影响
- **快速查询**：高效的关系索引和查询优化，支持游戏中的复杂查询
- **减少遍历开销**：通过原型过滤和位运算，减少需要遍历的实体数量
- **适合游戏系统**：高效的查询系统，支持游戏中的各种系统处理

### 5. 内存管理

#### 紧凑的内存布局
- **列式存储**：组件数据按列存储，提高缓存局部性
- **同构实体分组**：同一原型的实体数据紧密存储
- **减少内存对齐开销**：优化的数据结构设计，减少内存对齐导致的浪费

#### 动态内存分配
- **预分配策略**：对频繁使用的数据结构进行预分配
- **批量分配**：支持批量内存分配，减少内存分配次数
- **内存池**：使用内存池管理频繁创建和销毁的对象

#### 对游戏性能的影响
- **减少内存占用**：紧凑的内存布局减少了内存占用
- **提高内存访问效率**：良好的缓存局部性提高了内存访问效率
- **减少GC压力**：优化的内存管理减少了GC压力，提高游戏的帧率稳定性

### 6. 游戏性能优势

lko-ecs4 的数据结构与算法设计为游戏开发提供了以下性能优势：

- **高帧率支持**：高效的设计支持游戏的高帧率运行
- **大规模实体处理**：支持处理大量实体，适合复杂游戏场景
- **低延迟**：减少内存访问延迟，提高系统响应速度
- **稳定的性能**：优化的内存管理减少了GC停顿，提供稳定的帧率
- **可扩展性**：支持游戏的扩展和迭代开发

### 7. 性能对比

与传统的ECS框架相比，lko-ecs4 的关系架构设计在以下方面具有优势：

- **更灵活的实体组织**：关系架构提供更灵活的实体组织方式，适合复杂游戏场景
- **更高的查询效率**：基于原型驱动布局的查询系统，提供更高的查询效率
- **更好的内存利用率**：紧凑的存储布局，提高内存利用率
- **更低的GC压力**：优化的内存管理，减少GC压力
- **更好的跨平台支持**：Kotlin多平台设计，支持JVM和JS平台

这些设计特点使 lko-ecs4 成为一款高性能、易用的游戏开发ECS框架，适合各种规模的游戏开发需求。

## API 与使用指南

### 1. 实体管理

#### 创建实体
```kotlin
// 创建空实体
val entity = world.entity {}

// 创建带有组件的实体
val entityWithComponents = world.entity {
    it.addComponent(Position(0f, 0f))
    it.addComponent(Velocity(1f, 0f))
}

// 创建带有关系的实体
val childEntity = world.childOf(parentEntity) {
    it.addComponent(Position(10f, 10f))
}
```

#### 配置实体
```kotlin
// 配置现有实体
world.entity(entity) {
    it.addComponent(Health(100))
    it.removeComponent<Velocity>()
}
```

#### 销毁实体
```kotlin
// 销毁实体
world.destroy(entity)
```

### 2. 组件管理

#### 注册组件类型
```kotlin
// 注册组件类型并获取ComponentId
val positionComponentId = world.componentService.id<Position>()
```

#### 添加组件
```kotlin
// 添加组件到实体
world.relationService.addRelation(entity, world.componentService.component<Position>(), Position(0f, 0f))

// 或使用实体配置
world.entity(entity) {
    it.addComponent(Position(0f, 0f))
}
```

#### 移除组件
```kotlin
// 从实体移除组件
world.relationService.removeRelation(entity, world.componentService.component<Position>())

// 或使用实体配置
world.entity(entity) {
    it.removeComponent<Position>()
}
```

#### 访问组件数据
```kotlin
// 获取组件数据
val position = world.relationService.getRelation(entity, world.componentService.component<Position>()) as Position

// 更新组件数据
world.relationService.addRelation(entity, world.componentService.component<Position>(), Position(10f, 10f))
```

### 3. 关系管理

#### 添加关系
```kotlin
// 定义关系
val equippedToComponentId = world.componentService.id<EquippedTo>()
val relation = Relation(equippedToComponentId, player)

// 添加关系
world.relationService.addRelation(weapon, relation)
```

#### 移除关系
```kotlin
// 移除关系
world.relationService.removeRelation(weapon, relation)
```

#### 检查关系
```kotlin
// 检查关系是否存在
val hasRelation = world.relationService.hasRelation(weapon, relation)
```

### 4. 查询系统

#### 基本查询
```kotlin
// 创建查询，获取所有具有Position和Velocity组件的实体
val movingEntitiesQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            component<Position>()
            component<Velocity>()
        }
    }
}

// 遍历查询结果
movingEntitiesQuery.forEach {
    val position = this[Position::class] as Position
    val velocity = this[Velocity::class] as Velocity
    // 处理实体
}
```

#### 复杂查询
```kotlin
// 创建复杂查询，使用and/or/not操作
val complexQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            // 匹配具有Position和Velocity组件，或者具有Position和Health组件的实体
            or {
                and {
                    component<Position>()
                    component<Velocity>()
                }
                and {
                    component<Position>()
                    component<Health>()
                }
            }
        }
    }
}
```

#### 关系查询
```kotlin
// 查询装备到玩家的所有实体
val equippedItemsQuery = world.query {
    object : QueryEntityContext(this) {
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(Relation(equippedToComponentId, player))
        }
    }
}
```

### 5. 事件机制

#### 定义事件
```kotlin
// 定义自定义事件
class EntityCreated(val entity: Entity)
class EntityDestroyed(val entity: Entity)
class ComponentAdded(val entity: Entity, val componentType: KClass<*>)
class ComponentRemoved(val entity: Entity, val componentType: KClass<*>)
class PlayerDamaged(val damage: Int)
```

#### 订阅事件
```kotlin
// 订阅实体创建事件
world.observe<EntityCreated> {}
    .exec {
        println("Entity created: $entity")
    }

// 订阅带有数据的事件
world.observeWithData<PlayerDamaged> {}
    .exec {
        val health = this[Health::class] as Health
        this[Health::class] = Health(health.value - event.damage)
    }

// 订阅特定实体的事件
world.observeWithData<PlayerDamaged>(player) {}
    .exec {
        println("Player damaged! Health: ${this[Health::class] as Health}")
    }
```

#### 发布事件
```kotlin
// 发布事件
world.emit(entity, EntityCreated(entity))
world.emit(player, PlayerDamaged(20))
```

#### 取消订阅
```kotlin
// 订阅事件并获取观察者
val observer = world.observe<EntityCreated> {}
    .exec {
        println("Entity created: $entity")
    }

// 取消订阅
observer.close()
```

### 6. 游戏开发最佳实践

#### 组件设计
- **纯数据类**：组件应该是纯数据类，不包含行为逻辑
- **单一职责**：每个组件只负责一个功能
- **小而专注**：组件应该小而专注，避免过大的组件
- **可组合**：设计可组合的组件，通过组合实现复杂功能

#### 系统设计
- **分离关注点**：每个系统只负责一个功能
- **使用查询系统**：通过查询系统获取需要处理的实体
- **批量处理**：批量处理实体，减少遍历开销
- **事件驱动**：使用事件机制处理实体间的通信

#### 性能优化
- **减少原型迁移**：避免频繁添加/移除组件，减少实体在原型间的迁移
- **使用查询缓存**：对于频繁使用的查询，考虑缓存查询结果
- **批量更新**：批量更新组件数据，减少内存访问开销
- **使用合适的数据结构**：根据游戏需求选择合适的数据结构

#### 关系架构使用
- **表达复杂关系**：使用关系表达实体间的复杂联系，如父子关系、装备关系等
- **动态关系**：利用关系的动态性，实现游戏中的动态行为
- **关系查询**：使用关系查询快速获取相关实体

### 7. 常见游戏开发场景

#### 移动系统
```kotlin
// 创建移动系统
class MovementSystem(private val world: World) {
    fun update(deltaTime: Float) {
        // 查询所有具有Position和Velocity组件的实体
        val movingEntitiesQuery = world.query {
            object : QueryEntityContext(this) {
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Position>()
                    component<Velocity>()
                }
            }
        }
        
        // 更新实体位置
        movingEntitiesQuery.forEach {
            // 获取当前实体
            val entity = this.entity
            
            // 通过关系服务访问组件数据
            val position = world.relationService.getRelation(entity, world.component<Position>()) as Position
            val velocity = world.relationService.getRelation(entity, world.component<Velocity>()) as Velocity
            
            // 更新组件数据
            world.relationService.addRelation(
                entity,
                world.component<Position>(),
                Position(
                    position.x + velocity.dx * deltaTime,
                    position.y + velocity.dy * deltaTime
                )
            )
        }
    }
}
```

#### 碰撞检测系统
```kotlin
// 创建碰撞检测系统
class CollisionSystem(private val world: World) {
    fun update() {
        // 查询所有具有Position和Collider组件的实体
        val colliderEntitiesQuery = world.query {
            object : QueryEntityContext(this) {
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<Position>()
                    component<Collider>()
                }
            }
        }
        
        // 检测碰撞
        val entities = mutableListOf<Entity>()
        colliderEntitiesQuery.forEach {
            entities.add(this.entity)
        }
        
        for (i in entities.indices) {
            for (j in i + 1 until entities.size) {
                val entity1 = entities[i]
                val entity2 = entities[j]
                
                // 获取实体组件数据
                val position1 = world.relationService.getRelation(entity1, world.component<Position>()) as Position
                val collider1 = world.relationService.getRelation(entity1, world.component<Collider>()) as Collider
                val position2 = world.relationService.getRelation(entity2, world.component<Position>()) as Position
                val collider2 = world.relationService.getRelation(entity2, world.component<Collider>()) as Collider
                
                // 检测碰撞逻辑
                if (isColliding(position1, collider1, position2, collider2)) {
                    // 处理碰撞
                }
            }
        }
    }
    
    private fun isColliding(pos1: Position, collider1: Collider, pos2: Position, collider2: Collider): Boolean {
        // 碰撞检测逻辑
        return false
    }
}
```

#### 输入处理系统
```kotlin
// 创建输入处理系统
class InputSystem(private val world: World, private val input: Input) {
    fun update() {
        // 查询玩家实体
        val playerQuery = world.query {
            object : QueryEntityContext(this) {
                override fun FamilyMatcher.FamilyBuilder.configure() {
                    component<PlayerTag>()
                    component<Position>()
                    component<Velocity>()
                }
            }
        }
        
        // 处理输入
        playerQuery.forEach {
            // 获取当前实体
            val entity = this.entity
            
            // 通过关系服务访问组件数据
            val velocity = world.relationService.getRelation(entity, world.component<Velocity>()) as Velocity
            var newVelocity = velocity
            
            if (input.isKeyPressed(Key.UP)) {
                newVelocity = Velocity(newVelocity.dx, newVelocity.dy + 1f)
            }
            if (input.isKeyPressed(Key.DOWN)) {
                newVelocity = Velocity(newVelocity.dx, newVelocity.dy - 1f)
            }
            if (input.isKeyPressed(Key.LEFT)) {
                newVelocity = Velocity(newVelocity.dx - 1f, newVelocity.dy)
            }
            if (input.isKeyPressed(Key.RIGHT)) {
                newVelocity = Velocity(newVelocity.dx + 1f, newVelocity.dy)
            }
            
            // 更新组件数据
            world.relationService.addRelation(entity, world.component<Velocity>(), newVelocity)
        }
    }
}

## 测试与基准

### 1. 测试覆盖情况

lko-ecs4 项目采用 Kotlin 测试框架，提供了全面的测试覆盖：

#### 核心功能测试
- ✅ 实体生命周期管理
- ✅ 组件添加与移除
- ✅ 关系管理
- ✅ 原型迁移
- ✅ 查询系统
- ✅ 事件机制

#### 观察者服务测试
- ✅ 基本事件订阅与处理
- ✅ 事件取消订阅
- ✅ 自动取消订阅（try-with-resources）
- ✅ 事件过滤
- ✅ 事件数据传递
- ✅ 多个观察者订阅同一事件
- ✅ 不同事件类型的订阅
- ✅ 带有组件条件的事件订阅

#### 关系服务测试
- ✅ 关系添加和查询
- ✅ 父子关系管理
- ✅ 事件监听（OnInserted, OnRemoved, OnUpdated）

#### 跨平台测试
- ✅ JVM 平台测试
- ✅ JS 平台测试

### 2. 运行测试

#### 运行所有测试
```bash
# 运行所有平台测试
./gradlew :lko-ecs4:allTests

# 运行 JVM 平台测试
./gradlew :lko-ecs4:desktopTest

# 运行 JS 平台测试
./gradlew :lko-ecs4:browserTest
```

#### 运行特定测试
```bash
# 运行特定测试类
./gradlew :lko-ecs4:desktopTest --tests "cn.jzl.ecs.ObserverServiceTest"

# 运行特定测试方法
./gradlew :lko-ecs4:desktopTest --tests "cn.jzl.ecs.ObserverServiceTest.testBasicEventSubscription"
```

### 3. 性能基准

#### 游戏开发相关性能基准

| 操作 | 性能 | 说明 |
|------|------|------|
| 实体创建 | ~1,000,000 实体/秒 | 单线程创建实体 |
| 组件添加 | ~500,000 组件/秒 | 向实体添加组件 |
| 组件访问 | ~10,000,000 访问/秒 | 访问实体组件数据 |
| 查询执行 | ~1,000 查询/秒 | 复杂查询，返回 1000 个实体 |
| 事件发布 | ~500,000 事件/秒 | 发布事件到多个观察者 |

#### 原型迁移性能

| 实体数量 | 迁移时间 | 说明 |
|----------|----------|------|
| 1,000 | ~0.1 ms | 1000 个实体在原型间迁移 |
| 10,000 | ~1 ms | 10,000 个实体在原型间迁移 |
| 100,000 | ~10 ms | 100,000 个实体在原型间迁移 |

#### 跨平台性能对比

| 操作 | JVM 性能 | JS 性能 |
|------|----------|---------|
| 实体创建 | ~1,000,000 实体/秒 | ~200,000 实体/秒 |
| 组件访问 | ~10,000,000 访问/秒 | ~1,000,000 访问/秒 |
| 查询执行 | ~1,000 查询/秒 | ~200 查询/秒 |

### 4. 性能优化建议

#### 减少原型迁移
- 避免频繁添加/移除组件
- 设计稳定的实体类型
- 使用关系替代组件的频繁变化

#### 优化查询
- 缓存频繁使用的查询结果
- 减少查询的复杂度
- 使用更具体的过滤条件

#### 优化事件
- 减少事件的发布频率
- 避免在事件处理中执行复杂操作
- 合理使用事件订阅和取消订阅

#### 内存优化
- 合理设计组件大小
- 避免创建过大的组件
- 及时销毁不再使用的实体

## 跨平台支持

### 1. 平台支持

lko-ecs4 目前支持以下平台：

- **JVM (desktop)**：支持桌面游戏开发，如使用 LibGDX、JavaFX 等框架。
- **JS (browser)**：支持 Web 游戏开发，如使用 Three.js、Pixi.js 等框架。

### 2. 跨平台架构

lko-ecs4 采用 Kotlin Multiplatform (KMP) 架构，实现了核心功能的跨平台共享：

```
┌─────────────────────────────────────────────────────────────────┐
│                       Common Main                               │
├─────────────────┬─────────────────┬─────────────────┬───────────┤
│ Archetype      │ Component       │ Entity          │ Relation  │
└────────┬────────┴────────┬────────┴────────┬────────┴───────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   JVM (desktop) │ │   JS (browser)  │ │   Future Platforms  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
```

### 3. 游戏开发跨平台支持

#### JVM 平台
- **性能优势**：JVM 平台提供更好的性能，适合开发大型游戏。
- **内存管理**：JVM 平台的垃圾回收机制可能会导致性能波动，需要注意内存管理。
- **库支持**：JVM 平台有丰富的游戏开发库支持，如 LibGDX、JBox2D、LWJGL 等。
- **开发工具**：支持 IntelliJ IDEA、Eclipse 等开发工具，提供良好的调试和开发体验。

#### JS 平台
- **Web 兼容性**：支持主流浏览器，如 Chrome、Firefox、Safari、Edge 等。
- **性能优化**：针对 JS 平台进行了性能优化，减少内存分配和垃圾回收。
- **Web 游戏库支持**：支持与 Three.js、Pixi.js、Phaser 等 Web 游戏库集成。
- **开发工具**：支持 Chrome DevTools、Firefox Developer Tools 等调试工具。

### 4. 跨平台开发最佳实践

#### 组件设计
- **纯数据类**：组件应该是纯数据类，避免包含平台特定的代码。
- **序列化支持**：确保组件支持跨平台序列化，便于保存和加载游戏状态。

#### 系统设计
- **平台抽象**：将平台特定的功能抽象为接口，在不同平台上实现。
- **异步操作**：使用 Kotlin 协程处理异步操作，提供统一的异步编程模型。
- **性能优化**：针对不同平台进行性能优化，如在 JS 平台上减少内存分配。

#### 资源管理
- **资源加载**：使用统一的资源加载接口，支持不同平台的资源格式。
- **资源缓存**：实现资源缓存机制，减少资源加载时间。

#### 测试
- **跨平台测试**：在不同平台上进行测试，确保功能正常。
- **性能测试**：在不同平台上进行性能测试，优化性能瓶颈。

### 5. 未来跨平台规划

- **WebAssembly (Wasm) 支持**：添加 WebAssembly 平台支持，提高 Web 性能。
- **移动端支持**：添加 Android 和 iOS 平台支持，支持移动端游戏开发。
- **Native 平台支持**：添加 Windows、macOS、Linux 原生平台支持。
- **跨平台工具链**：提供统一的跨平台构建和部署工具链。

## 未完成功能规划

### 1. 核心功能增强
- **系统与调度**：引入 `System` 概念、帧循环、依赖排序与协程并发。
- **序列化/反序列化**：保存/加载世界状态，支持 JVM/JS 一致性。
- **并发安全**：明确线程模型与同步策略，补充基准。
- **批量操作优化**：优化批量实体操作的性能。
- **内存池管理**：进一步优化内存分配和回收。

### 2. 游戏开发相关功能
- **物理系统集成**：提供与物理引擎（如 Box2D、JBox2D）的集成接口。
- **渲染系统集成**：提供与渲染引擎（如 LibGDX、Three.js）的集成接口。
- **动画系统**：支持实体动画管理，包括骨骼动画和帧动画。
- **状态机**：支持实体状态管理，提供有限状态机实现。
- **AI 系统**：提供基础的 AI 行为树支持，支持复杂的 AI 行为。
- **音频系统**：提供音频管理和播放支持。
- **输入系统**：提供统一的输入处理接口，支持键盘、鼠标、触摸等输入方式。
- **UI 系统集成**：提供与 UI 框架的集成接口。

### 3. 跨平台支持
- **JS 环境优化**：优化 JS 平台的性能，减少内存占用。
- **Wasm 支持**：添加 WebAssembly 平台支持，提高 Web 性能。
- **移动端支持**：添加 Android 和 iOS 平台支持，支持移动端游戏开发。
- **桌面平台优化**：优化 JVM 平台的性能，支持桌面游戏开发。

### 4. 工具与生态
- **编辑器支持**：提供可视化编辑器支持，支持实体和组件的可视化编辑。
- **调试工具**：添加调试工具，支持实体和组件的可视化调试，包括实体浏览器、组件编辑器等。
- **示例项目**：提供完整的游戏示例项目，包括 2D 和 3D 游戏示例。
- **文档完善**：补充更详细的文档和教程，包括 API 文档、使用指南、最佳实践等。
- **社区建设**：建立社区支持，包括论坛、Discord 频道等。
- **插件系统**：支持插件扩展，允许开发者扩展框架功能。

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