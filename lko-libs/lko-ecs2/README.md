# lko-ecs2

基于Kotlin的高性能Entity Component System (ECS)框架，为游戏开发提供灵活、高效的实体组件管理系统。

## 项目概述

lko-ecs2是一个轻量级但功能强大的ECS框架，采用了现代Kotlin语言特性，提供了简洁的API和高性能的实现。该框架特别适合于游戏开发中的实体管理、组件系统和任务调度。

### 核心特性

- **实体-组件分离**：实体仅作为标识符，组件包含数据和逻辑
- **强大的实体查询系统**：Family机制支持灵活的实体过滤
- **高效的任务调度系统**：基于依赖关系和优先级的任务执行
- **声明式API设计**：使用Kotlin DSL提供流畅的开发体验
- **协程支持**：与Kotlin协程集成，提供异步任务处理能力
- **依赖注入**：内置简单的依赖注入机制
- **类型安全**：充分利用Kotlin的类型系统

## 核心概念

### Entity（实体）

实体是游戏世界中的基本单位，在lko-ecs2中，实体只是一个带有唯一ID和版本号的标识符，本身不包含任何数据或行为。

### Component（组件）

组件是数据和行为的集合，通过附加到实体上赋予其实质意义。**推荐使用不可变组件**，这有助于提高代码的可预测性、线程安全性和性能优化。框架区分普通组件和标签组件（EntityTag）。

- 支持使用任意现有普通类作为组件，无需特殊继承关系
- 若组件实现了Component接口，将自动获得onAttach和onDetach生命周期方法，用于处理组件绑定和移除的逻辑

### World（世界）

世界是ECS系统的容器，管理所有实体、组件和服务。

### Family（家族）

家族用于定义实体查询规则，支持基于组件的实体过滤，提供高效的实体集合管理。

### Schedule（调度）

调度系统负责管理和执行游戏循环中的各种任务，支持基于依赖关系和优先级的任务排序和执行。

## 快速开始

### 创建World

```kotlin
import cn.jzl.ecs.v2.*

val world = world {}
```


### 定义组件

```kotlin
// 使用不可变属性的组件定义（推荐）
data class PositionComponent(val x: Float = 0f, val y: Float = 0f) : Component<PositionComponent>() {
    override val type: ComponentType<PositionComponent> get() = PositionComponent
    companion object : ComponentType<PositionComponent>()
}

data class VelocityComponent(val dx: Float = 0f, val dy: Float = 0f) : Component<VelocityComponent>() {
    override val type: ComponentType<VelocityComponent> get() = VelocityComponent
    companion object : ComponentType<VelocityComponent>()
}

// 定义标签组件
data object PlayerTag : EntityTag()
data object EnemyTag : EntityTag()

// 为已有类定义组件类型（适用于基本类型、标准库类等）
data object IntComponentType : ComponentType<Int>()
data object StringComponentType : ComponentType<String>()

// 使用现有普通类作为组件（无需实现Component接口）
data class HealthData(val current: Float, val max: Float)
data class ScoreData(val points: Int)

// 为已有类定义组件类型（适用于普通数据类）
data object HealthDataComponentType : ComponentType<HealthData>()
data object ScoreDataComponentType : ComponentType<ScoreData>()

// 使用示例
val enemy = world.create {
    // 可以直接使用普通数据类作为组件
    it[HealthDataComponentType] = HealthData(100f, 100f)
    it[ScoreDataComponentType] = ScoreData(50) // 击杀此敌人可获得50分
    it += PositionComponent(50f, 50f) // 同时也可以使用实现Component接口的组件
    it += EnemyTag // 添加标签组件
    
    // 使用基本类型作为组件（需要先定义对应的ComponentType）
    it[IntComponentType] = 100 // 设置生命值为100
    it[StringComponentType] = "enemy-type-1" // 设置敌人类型标识
}
```

### 创建和配置实体

```kotlin
// 方法1: 使用构建器创建实体并添加组件
val player = world.create {
    // 使用 += 操作符添加实现Component接口的组件或标签组件
    it += PositionComponent(10f, 20f)
    it += VelocityComponent(1f, 0f)
    it += PlayerTag
    
    // 使用索引操作符添加已有类组件
    it[HealthDataComponentType] = HealthData(100f, 100f)
    it[IntComponentType] = 50 // 等级
    it[StringComponentType] = "player-1"
}

// 方法2: 先创建实体，再通过configure添加组件
val enemy = world.create()
world.configure(enemy) {
    it += PositionComponent(100f, 50f)
    it += VelocityComponent(-0.5f, 0f)
    it[HealthDataComponentType] = HealthData(50f, 50f)
    it[IntComponentType] = 10 // 敌人等级
}

// 方法3: 使用configure配置现有实体
world.configure(player) {
    // 更新不可变组件（创建并替换）
    it[PositionComponent] = PositionComponent(30f, 40f)
    
    // 对于data class组件，可以使用copy方法创建更新后的实例
    val currentVelocity = it[VelocityComponent]
    it[VelocityComponent] = currentVelocity.copy(dx = 2f) // 只修改x方向速度
    
    // 更新已有类组件
    it[IntComponentType] = 51 // 升级
}

// 检查和获取组件必须在configure块内
world.configure(player) {
    // 检查组件是否存在
    if (PositionComponent in it) {
        println("玩家有位置组件")
    }
    
    if (HealthDataComponentType in it) {
        println("玩家有健康数据组件")
    }
    
    // 获取组件
    val playerPosition = it[PositionComponent] // 获取实现Component接口的组件
    val playerHealth = it[HealthDataComponentType] // 获取普通类组件
    
    // 移除组件
    it -= VelocityComponent // 移除实现Component接口的组件
    it -= HealthDataComponentType // 移除普通类组件
}
// 链式操作需要使用configure
val bullet = world.create()
world.configure(bullet) {
    it += PositionComponent(0f, 0f)
    it += VelocityComponent(0f, -10f)
    it[IntComponentType] = 20 // 伤害值
    
    // 检查实体是否激活
    if (it.active) {
        println("子弹实体已激活")
    }
}

// 批量创建实体示例
for (i in 1..5) {
    world.create { 
        it += PositionComponent(i * 10f, 0f)
        it += VelocityComponent(0f, 1f)
        it[IntComponentType] = i // 设置ID
    }
}

// 批量创建多个相似实体
for (i in 0 until 5) {
    world.create {
        it += PositionComponent(i * 30f, 200f)
        it += VelocityComponent(0f, -1f)
        it[IntComponentType] = 5 // 每个敌人的伤害值
    }
}
```

### 使用Family查询实体

```kotlin
// 定义家族：包含Position和Velocity组件的所有实体
val movingEntities = world.family {
    all(PositionComponent, VelocityComponent)
}

// 监听实体加入家族
movingEntities.onEntityInserted.add { entity ->
    println("Entity ${entity.id} added to moving entities")
}

// 监听实体移除家族
movingEntities.onEntityRemoved.add { entity ->
    println("Entity ${entity.id} removed from moving entities")
}

// 遍历家族中的实体
for (entity in movingEntities.entities) {
    world.configure(entity) {
        val position = it[PositionComponent]
        val velocity = it[VelocityComponent]
        // 更新位置
        it[PositionComponent] = PositionComponent(position.x + velocity.dx, position.y + velocity.dy)
    }
}
```

### 创建任务调度

```kotlin
// 直接通过world创建调度器
world.schedule("physics") {
    // 声明组件的读写访问权限
    PositionComponent.write
    VelocityComponent.read
    
    // 访问特定家族的实体
    val movingEntities = family {
        all(PositionComponent, VelocityComponent)
    }
    
    // 使用withLoop创建循环任务
    withLoop {
        // 实现物理更新逻辑
        for (entity in movingEntities.entities) {
            configure(entity) {
                val position = it[PositionComponent]
                val velocity = it[VelocityComponent]

                              // 创建更新后的位置组件并替换旧组件
                it[PositionComponent] = PositionComponent(
                    x = position.x + velocity.dx,
                    y = position.y + velocity.dy
                )
            }
        }
    }
}

// 分别创建渲染系统调度器（不嵌套）
world.schedule("render") {
    // 声明组件的只读访问权限
    PositionComponent.read
    
    // 使用family查询具有PositionComponent的实体
    val renderableEntities = family {
        all(PositionComponent)
    }
    
    // 使用withLoop创建循环渲染任务
    withLoop {
        // 遍历需要渲染的实体
        for (entity in renderableEntities.entities) {
            configure(entity) {
                // 在configure闭包内安全访问组件
                val position = it[PositionComponent]
                // 这里是渲染逻辑
                // renderSystem.draw(entity, position.x, position.y)
            }
        }
    }
}
// 在游戏主循环中更新世界（会自动更新所有调度器）
// world.update(Duration.milliseconds(16))
> ```
### 使用withTask安排任务

> 注意：使用以下示例代码时，需要导入相应的类：
> ```kotlin
> import kotlin.time.Duration.Companion.milliseconds
> import kotlin.time.Duration.Companion.seconds
> import cn.jzl.ecs.v2.ScheduleTaskPriority
> ```

withTask用于安排一次性任务或延迟任务，而withLoop用于创建循环执行的任务。

```kotlin
// 创建一个调度器用于演示withTask的不同用法
world.schedule("taskDemo") {
    println("调度器初始化完成")
    
    // 基本用法：在下一帧执行任务
    withTask {
        println("这个任务将在下一帧执行")
    }
    
    // 设置任务优先级
    withTask(ScheduleTaskPriority.HIGH) {
        println("高优先级任务将优先执行")
    }
    
    // 使用延迟任务
    withTask {
        // 延迟500毫秒后执行
        delay(500.milliseconds)
        println("延迟500毫秒后执行的任务")
    }
    
    // 组合使用withTask和configure访问实体组件
    withTask {
        val playerId = 1 // 假设我们有一个ID为1的玩家实体
        val player = world.entityService[playerId]
        
        if (player.active) {
            configure(player) {
                if (HealthDataComponentType in it) {
                    val health = it[HealthDataComponentType]
                    println("玩家当前生命值: ${health.current}/${health.max}")
                    
                    // 更新生命值
                    it[HealthDataComponentType] = HealthData(health.current - 10, health.max)
                }
            }
        }
    }
}

// 可以使用withTask创建一次性任务，而不需要持续循环
world.schedule("gameStart") {
    println("游戏开始")
    
    // 游戏开始3秒后显示提示信息
    withTask {
        delay(3.seconds)
        println("提示: 按空格键跳跃")
    }
    
    // 游戏开始5秒后生成敌人
    withTask {
        delay(5.seconds)
        // 生成敌人的代码
        create {
            it += PositionComponent(100f, 100f)
            it += VelocityComponent(-1f, 0f)
            it += EnemyTag
        }
    }
}

// 在游戏主循环中更新所有调度器
// world.update(Duration.milliseconds(16)) // 假设60FPS，约16ms每帧
```

## 高级特性

### 组件读写访问控制

在调度系统中，可以显式声明组件的读写访问权限，框架会自动检测和解决潜在的访问冲突。这确保了多线程环境下的数据安全性：

```kotlin
world.schedule("updateSystem") {
    // 声明写访问权限
    PositionComponent.write
    
    // 声明读访问权限
    VelocityComponent.read
    
    // 系统实现...
}
```

### 任务依赖关系

可以显式设置任务之间的依赖关系，确保任务按正确的顺序执行：

```kotlin
val physicsSystem = world.schedule("physics") { /* ... */ }
val renderSystem = world.schedule("render") { /* ... */ }

// 设置renderSystem依赖于physicsSystem
renderSystem.dependsOn(physicsSystem)
```


### 实体生命周期

可以监听实体的创建、更新和销毁事件，实现系统间的自动通知和响应：

```kotlin
world.entityService.onEntityCreate.add { entity ->
    println("Entity created: ${entity.id}")
}

world.entityService.onEntityUpdate.add { entity ->
    println("Entity updated: ${entity.id}")
}

world.entityService.onEntityDestroy.add { entity ->
    println("Entity destroyed: ${entity.id}")
}
```


## 架构说明

### 核心组件

- **EntityStore**：实体存储和管理
- **EntityService**：实体创建、配置和生命周期管理
- **ComponentService**：组件系统的核心服务，负责组件的添加、获取和移除
- **FamilyService**：实体查询和过滤服务，管理实体家族
- **ScheduleService**：任务调度和执行服务，管理调度器生命周期
- **ScheduleDispatcher**：任务依赖分析和执行调度器

### 数据流

1. 实体通过EntityService创建，并由EntityStore存储
2. 组件通过ComponentService附加到实体
3. FamilyService监听实体变更，自动维护满足条件的实体集合
4. ScheduleService创建和管理调度器，ScheduleDispatcher根据依赖关系和优先级调度任务执行
5. 任务通过EntityUpdateContext安全地访问和修改实体组件

## 性能优化

- 使用ObjectFastList和IntFastList进行高效的数据存储和遍历
- 采用BitSet实现快速的组件位掩码操作和实体匹配
- 实现实体ID重用机制，减少内存碎片
- 基于拓扑排序的任务调度，确保正确的执行顺序
- 支持批处理操作，减少调度开销

## 注意事项

- 组件类型必须定义一个名为`Companion`的伴随对象，继承自`ComponentType`
- 对于普通类组件，必须先定义对应的ComponentType
- 实体组件的访问和修改必须在configure闭包内进行
- 任务系统中的访问权限声明对于避免数据竞争很重要
- 在调度器内部，应使用作用域内的configure方法而不是world.configure
- 大量实体和组件时，合理设计Family查询可以提高性能

## 测试

项目包含完整的单元测试套件，可以通过以下命令运行：

```bash
./gradlew test
```

## 许可证

[在此添加许可证信息]
