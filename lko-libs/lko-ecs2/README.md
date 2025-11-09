# lko-ecs2

基于Kotlin的高性能Entity Component System (ECS)框架，为游戏开发提供灵活、高效的实体组件管理系统，特别适合需要高性能和可扩展性的游戏开发场景。

## 项目概述

lko-ecs2是一个功能完备的ECS框架，采用了现代Kotlin语言特性和最佳实践，提供了简洁而强大的API和高性能的实现。该框架专为游戏开发设计，特别关注性能优化和开发体验。

### 核心特性

- **实体-组件分离**：实体仅作为标识符，组件包含数据，系统处理逻辑，实现关注点分离
- **强大的实体查询系统**：Family机制支持灵活的实体过滤，提供高效的实体集合管理和自动更新
- **高效的任务调度系统**：基于依赖关系和优先级的任务执行，支持协程和异步操作
- **声明式API设计**：使用Kotlin DSL提供流畅的开发体验，减少样板代码
- **协程集成**：与Kotlin协程深度集成，提供现代化的异步任务处理能力
- **依赖注入**：内置依赖注入机制，实现组件间的松耦合
- **类型安全**：充分利用Kotlin的类型系统，提供编译时错误检查
- **高性能设计**：使用优化的数据结构（如ObjectFastList、IntFastList、BitSet），减少内存分配和GC压力
- **实体ID重用**：实现实体ID重用机制，避免内存碎片
- **组件访问控制**：提供读写访问权限声明，避免数据竞争
- **多平台支持**：支持JVM、JavaScript等多平台开发

## 核心概念

### Entity（实体）

实体是游戏世界中的基本单位，在lko-ecs2中，实体只是一个带有唯一ID和版本号的标识符（value class），本身不包含任何数据或行为。这种设计使得实体管理非常轻量高效。

### Component（组件）

组件是纯数据的容器，通过附加到实体上赋予其实质意义。**推荐使用不可变组件**，这有助于提高代码的可预测性、线程安全性和性能优化。

框架支持两种主要的组件类型：
- **普通组件**：可以是任何Kotlin类，无需特殊继承关系
- **实现Component接口的组件**：自动获得onAttach和onDetach生命周期方法
- **标签组件（EntityTag）**：特殊的轻量级组件，用于标记实体的二进制状态

### World（世界）

World是ECS系统的核心容器和入口点，管理所有实体、组件和服务。它提供了创建实体、管理组件和调度任务的统一接口，并负责协调各个服务之间的交互。

### Family（家族）

家族用于定义实体查询规则，支持基于组件的实体过滤，提供高效的实体集合管理。家族会自动监听实体变化并更新成员，确保查询结果始终反映最新的实体状态。

### Schedule（调度）

调度系统负责管理和执行游戏循环中的各种任务，支持基于依赖关系和优先级的任务排序和执行。它提供了withLoop和withTask两种主要的任务定义方式，满足不同的任务执行需求。

## 快速开始

### 创建World

```kotlin
import cn.jzl.ecs.v2.*

// 创建一个基本的ECS世界
val world = world {}

// 自定义配置的ECS世界（指定初始容量）
val customWorld = world {
    // 设置初始实体容量为2048（默认1024）
    constant(TAG_CAPACITY, 2048)
    
    // 注册自定义服务和组件
    bind singleton { MyCustomService(di) }
}
```

### 定义组件

```kotlin
// 使用不可变属性的组件定义（推荐）
data class PositionComponent(val x: Float = 0f, val y: Float = 0f) : Component<PositionComponent>() {
    override val type: ComponentType<PositionComponent> get() = PositionComponent
    companion object : ComponentType<PositionComponent>()
    
    // 可选的生命周期方法
    override fun World.onAttach(entity: Entity) {
        // 组件附加到实体时执行的逻辑
        println("Position component attached to entity ${entity.id}")
    }
    
    override fun World.onDetach(entity: Entity) {
        // 组件从实体分离时执行的逻辑
        println("Position component detached from entity ${entity.id}")
    }
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
    println("玩家位置: (${playerPosition.x}, ${playerPosition.y})")
    println("玩家生命值: ${playerHealth.current}/${playerHealth.max}")
    
    // 移除组件
    it -= VelocityComponent // 移除实现Component接口的组件
    it -= HealthDataComponentType // 移除普通类组件
}

// 检查实体是否活跃
if (world.isActive(player)) {
    println("玩家实体处于活跃状态")
}

// 链式操作需要使用configure
val bullet = world.create()
world.configure(bullet) {
    it += PositionComponent(0f, 0f)
    it += VelocityComponent(0f, -10f)
    it[IntComponentType] = 20 // 伤害值
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

// 移除实体
world.remove(enemy)

// 监听实体生命周期事件
world.entityService.onEntityCreate.add { entity ->
    println("实体创建: ${entity.id}")
}

world.entityService.onEntityUpdate.add { entity ->
    println("实体更新: ${entity.id}")
}

world.entityService.onEntityDestroy.add { entity ->
    println("实体销毁: ${entity.id}")
}
```

### 使用Family查询实体

```kotlin
// 定义家族：包含Position和Velocity组件的所有实体
val movingEntities = world.family {
    all(PositionComponent, VelocityComponent)
}

// 更复杂的家族查询：包含Position组件但不包含EnemyTag的实体
val friendlyEntities = world.family {
    all(PositionComponent)
    none(EnemyTag)
}

// 高级查询：包含Position组件，并且至少包含PlayerTag或EnemyTag中的一个
val gameObjects = world.family {
    all(PositionComponent)
    any(PlayerTag, EnemyTag)
}

// 监听实体加入家族
movingEntities.onEntityInserted.add { entity ->
    println("实体 ${entity.id} 加入到移动实体集合")
}

// 监听实体移除家族
movingEntities.onEntityRemoved.add { entity ->
    println("实体 ${entity.id} 从移动实体集合移除")
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

// 获取家族中的实体数量
println("移动实体数量: ${movingEntities.entities.size}")

// 检查实体是否在家族中
if (player in movingEntities.entities) {
    println("玩家在移动实体集合中")
}
```

### 创建任务调度

```kotlin
// 直接通过world创建调度器
val physicsSystem = world.schedule("physics", ScheduleTaskPriority.HIGH) {
    // 声明组件的读写访问权限
    PositionComponent.write
    VelocityComponent.read
    
    // 访问特定家族的实体
    val movingEntities = family {
        all(PositionComponent, VelocityComponent)
    }
    
    println("物理系统初始化完成")
    
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
val renderSystem = world.schedule("render") {
    // 声明组件的只读访问权限
    PositionComponent.read
    
    // 设置依赖关系，确保在物理系统之后执行
    dependsOn(physicsSystem)
    
    // 使用family查询具有PositionComponent的实体
    val renderableEntities = family {
        all(PositionComponent)
    }
    
    println("渲染系统初始化完成")
    
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
// val deltaTime = Duration.milliseconds(16) // 假设60FPS，约16ms每帧
// world.update(deltaTime)
```kotlin
// 在游戏主循环中更新世界（会自动更新所有调度器）
// 示例游戏循环伪代码
fun gameLoop() {
    while (gameRunning) {
        val startTime = System.nanoTime()
        
        // 更新世界状态
        world.update(Duration.milliseconds(deltaTime))
        
        // 渲染逻辑...
        
        // 计算下一帧的时间增量
        val elapsedTime = System.nanoTime() - startTime
        deltaTime = max(16L, 16666666L - elapsedTime) / 1000000.0 // 限制在60FPS
    }
}
```

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
    println("任务演示调度器初始化完成")
    
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
        
        if (world.isActive(player)) {
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
    
    // 递归任务示例：创建一个每秒更新一次的计时器
    fun createTimer(count: Int = 0) {
        withTask {
            delay(1.seconds)
            println("计时器: $count 秒")
            // 递归创建下一个计时器任务
            if (count < 10) {
                createTimer(count + 1)
            }
        }
    }
    
    // 启动计时器
    createTimer()
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
    
    // 游戏开始10秒后生成一波敌人
    withTask {
        delay(10.seconds)
        println("生成敌人波次!")
        // 生成多个敌人
        for (i in 0 until 5) {
            create {
                it += PositionComponent((i * 50 + 100).toFloat(), 50f)
                it += VelocityComponent(0f, 1f)
                it += EnemyTag
            }
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
    HealthDataComponentType.read
    
    // 系统实现...
}
```

### 任务依赖关系

可以显式设置任务之间的依赖关系，确保任务按正确的顺序执行：

```kotlin
val physicsSystem = world.schedule("physics") { /* ... */ }
val aiSystem = world.schedule("ai") { /* ... */ }
val renderSystem = world.schedule("render") { /* ... */ }

// 设置依赖链：physics -> ai -> render
aiSystem.dependsOn(physicsSystem)
renderSystem.dependsOn(aiSystem)

// 也可以在调度器内部声明依赖
world.schedule("inputSystem") {
    // 此调度器依赖于physicsSystem
    dependsOn(physicsSystem)
    
    // 系统实现...
}
```

### 实体生命周期

可以监听实体的创建、更新和销毁事件，实现系统间的自动通知和响应：

```kotlin
world.entityService.onEntityCreate.add { entity ->
    println("实体创建: ${entity.id}")
}

world.entityService.onEntityUpdate.add { entity ->
    println("实体更新: ${entity.id}")
}

world.entityService.onEntityDestroy.add { entity ->
    println("实体销毁: ${entity.id}")
}
```

### 调度器生命周期管理

可以检查和管理调度器的活跃状态：

```kotlin
val gameSystem = world.schedule("game") { /* ... */ }

// 检查调度器是否活跃
if (world.isActive(gameSystem)) {
    println("游戏系统正在运行")
}

// 在游戏暂停时暂停调度器
// (注意：当前版本需要通过调度器内部实现暂停逻辑)
world.schedule("gameManager") {
    var isPaused = false
    
    withTask {
        // 监听暂停事件
        while (true) {
            // 检测暂停输入或条件
            if (isPauseRequested && !isPaused) {
                isPaused = true
                println("游戏暂停")
            } else if (!isPauseRequested && isPaused) {
                isPaused = false
                println("游戏继续")
            }
            delay(50.milliseconds)
        }
    }
    
    withLoop {
        // 只有在非暂停状态下执行游戏逻辑
        if (!isPaused) {
            // 游戏逻辑更新
        }
    }
}
```

## 架构说明

### 核心组件

- **EntityStore**：实体存储和管理，负责实体ID和版本管理
- **EntityService**：实体创建、配置和生命周期管理，提供实体的CRUD操作
- **ComponentService**：组件系统的核心服务，负责组件的添加、获取和移除
- **FamilyService**：实体查询和过滤服务，管理实体家族和自动更新
- **ScheduleService**：任务调度和执行服务，管理调度器生命周期和对象池
- **ScheduleDispatcher**：任务依赖分析和执行调度器，处理任务的实际执行
- **FrameCoroutineScheduler**：基于帧的协程调度器，优化任务执行性能
- **EntityUpdateContext**：提供安全的实体修改机制，确保实体状态一致性

### 数据流

1. 实体通过EntityService创建，并由EntityStore存储
2. 组件通过ComponentService附加到实体，更新实体的组件位集
3. FamilyService监听实体变更事件，自动评估和更新相关家族的成员
4. ScheduleService创建和管理调度器，使用对象池优化资源利用
5. ScheduleDispatcher根据依赖关系和优先级对任务进行拓扑排序和调度执行
6. 任务通过EntityUpdateContext安全地访问和修改实体组件，确保数据一致性

## 性能优化

lko-ecs2框架内置了多项性能优化措施，使其适合高性能游戏开发：

- **高效数据结构**：使用ObjectFastList和IntFastList进行高效的数据存储和遍历，比标准集合更轻量
- **位操作优化**：采用BitSet实现快速的组件位掩码操作和实体匹配
- **实体ID重用**：实现实体ID重用机制，减少内存碎片和GC压力
- **对象池化**：调度器实例池化，减少内存分配和回收
- **拓扑排序**：基于拓扑排序的任务调度，确保正确的执行顺序和最小化等待时间
- **批处理操作**：支持批处理实体操作，减少调度开销
- **协程优化**：与Kotlin协程深度集成，提供高效的异步任务处理
- **延迟执行**：使用EntityUpdateContext延迟执行实体修改，确保一致性和性能
- **按需更新**：家族机制只在实体发生变化时更新，避免不必要的计算

## 最佳实践

- **使用不可变组件**：优先使用data class定义不可变组件，提高线程安全性和代码可维护性
- **合理设计Family查询**：避免过于复杂的查询条件，确保高效的实体匹配
- **明确组件访问权限**：在调度器中明确声明组件的读写权限，有助于检测潜在的并发问题
- **正确使用依赖关系**：合理设置调度器之间的依赖关系，确保正确的执行顺序
- **避免过度使用标签组件**：对于复杂状态，考虑使用枚举或整数组件而不是多个标签组件
- **使用withTask而非递归**：对于需要多次执行的任务，优先使用withLoop或调度多个withTask
- **在调度器内部使用作用域方法**：使用调度器作用域内的configure和family方法，而不是world实例方法
- **适当设置初始容量**：根据预期的实体数量调整TAG_CAPACITY，避免频繁扩容

## 注意事项

- 组件类型必须定义一个名为`Companion`的伴随对象，继承自`ComponentType`
- 对于普通类组件（不实现Component接口的类），必须先定义对应的ComponentType
- 实体组件的访问和修改必须在configure闭包内进行，确保线程安全和数据一致性
- 任务系统中的访问权限声明对于避免数据竞争很重要，特别是在多线程环境下
- 在调度器内部，应使用作用域内的configure方法而不是world.configure
- 大量实体和组件时，合理设计Family查询可以显著提高性能
- 实体ID是可重用的，但版本号会递增，确保即使ID重用也能区分不同的实体实例
- 避免在实体组件中存储对其他实体的直接引用，应使用实体ID进行关联

## 测试

项目包含完整的单元测试套件，可以通过以下命令运行：

```bash
./gradlew test
```

## 许可证

[在此添加许可证信息]
