# 宗门模拟游戏 - 系统集成指南

本文档指导其他模块如何集成和使用宗门系统的核心功能。

## 目录

1. [快速集成](#快速集成)
2. [依赖管理](#依赖管理)
3. [常见集成场景](#常见集成场景)
4. [扩展系统](#扩展系统)
5. [性能考虑](#性能考虑)

---

## 快速集成

### 步骤 1: 安装 Addon

```kotlin
import cn.jzl.sect.ecs.sectAddon

val world = world {
    install(sectAddon)  // 自动包含所有依赖的系统
}
```

### 步骤 2: 获取服务实例

```kotlin
import cn.jzl.di.instance
import cn.jzl.sect.ecs.SectService

val sectService = world.di.instance<SectService>()
```

### 步骤 3: 使用服务

```kotlin
// 创建宗门
val sect = sectService.createSect("飞仙派", leader) {}

// 添加成员
sectService.addMember(sect, disciple, MemberRole.DISCIPLE)

// 查询信息
val members = sectService.getMembers(sect)
```

---

## 依赖管理

### Addon 依赖关系

```
sectAddon
├── coreAddon (基础)
├── levelingAddon (升级系统)
│   ├── attributeService (属性系统)
│   └── core components
├── moneyAddon (货币系统)
│   └── coreAddon
├── characterAddon (角色系统)
│   └── levelingAddon
└── inventoryAddon (背包系统)
    ├── coreAddon
    └── itemAddon
```

### 安装最小化依赖

```kotlin
// 仅需要宗门系统
val world = world {
    install(sectAddon)  // 自动安装所有必需的 addon
}
```

### 检查依赖是否已安装

```kotlin
try {
    val sectService = world.di.instance<SectService>()
    println("宗门系统已初始化")
} catch (e: Exception) {
    println("宗门系统未初始化，检查 addon 是否已安装")
}
```

---

## 常见集成场景

### 场景 1: UI 系统集成

显示宗门信息到 UI

```kotlin
class SectUIController(private val world: World) {
    private val sectService = world.di.instance<SectService>()
    
    fun displaySectInfo(sectName: String) {
        val sect = sectService[sectName] ?: return
        
        world.entity(sect) {
            val named = it.getComponent<Named>()
            val reputation = it.getComponent<SectReputation>()
            val money = it.getComponent<Money>()
            
            println("宗门: ${named.name}")
            println("声望: ${reputation.value}")
            println("资金: ${money.value}")
        }
        
        // 显示成员列表
        sectService.getMembers(sect).forEach { (entity, data) ->
            world.entity(entity) {
                val name = it.getComponent<Named>()
                println("  - ${name.name} (${data.role})")
            }
        }
    }
}
```

### 场景 2: 任务系统集成

任务系统与宗门系统交互

```kotlin
class TaskSystem(private val world: World) {
    private val sectService = world.di.instance<SectService>()
    
    fun completeQuestReward(playerEntity: Entity, sect: Entity, reward: Int) {
        // 增加玩家贡献度
        sectService.addContribution(sect, playerEntity, reward)
        
        // 增加宗门声望
        sectService.addReputation(sect, reward / 10)
        
        // 增加宗门经验
        sectService.addExperience(sect, reward.toLong())
    }
}
```

### 场景 3: 经济系统集成

市场交易与货币系统

```kotlin
class MarketSystem(private val world: World) {
    private val moneyService = world.di.instance<MoneyService>()
    
    fun sellItem(seller: Entity, buyer: Entity, price: Int) {
        // 执行金钱转移
        moneyService.transferMoney(buyer, seller, price) {
            // 交易成功的回调逻辑
            println("交易成功：$price 金币")
        }
    }
}
```

### 场景 4: 事件系统集成

监听和处理系统事件

```kotlin
class EventAggregator(private val world: World) {
    
    fun setupEventListeners() {
        // 监听宗门升级
        world.observeService.observe<OnUpgradeEvent> { event ->
            println("实体升级: ${event.oldLevel} -> ${event.newLevel}")
            // 触发其他系统的更新
        }
        
        // 监听角色创建
        world.observeService.observe<OnCreatedCharacter> { event ->
            println("新角色创建")
            // 初始化角色相关数据
        }
    }
}
```

### 场景 5: 存档系统集成

序列化宗门数据用于存档

```kotlin
class SaveSystem(private val world: World) {
    private val sectService = world.di.instance<SectService>()
    
    data class SectSaveData(
        val name: String,
        val members: List<Pair<String, String>>,  // 成员名 -> 角色
        val reputation: Int,
        val money: Int
    )
    
    fun saveSect(sectName: String): SectSaveData {
        val sect = sectService[sectName] ?: return SectSaveData("", emptyList(), 0, 0)
        
        var reputation = 0
        var money = 0
        
        world.entity(sect) {
            reputation = it.getComponent<SectReputation>().value
            money = it.getComponent<Money>().value
        }
        
        val members = sectService.getMembers(sect).map { (entity, data) ->
            val name = world.entity(entity) {
                it.getComponent<Named>().name
            }
            name to data.role.name
        }.toList()
        
        return SectSaveData(sectName, members, reputation, money)
    }
    
    fun loadSect(data: SectSaveData) {
        val leader = world.entity {
            it.addComponent(Named("恢复的宗主"))
        }
        
        val sect = sectService.createSect(data.name, leader) {}
        
        world.entity(sect) {
            it.addComponent(SectReputation(data.reputation))
            it.addComponent(Money(data.money))
        }
        
        // 恢复成员（简化版本，实际需要查询玩家实体）
    }
}
```

---

## 扩展系统

### 创建自定义系统

```kotlin
class ReputationSystem(private val world: World) : EntityRelationContext(world) {
    private val sectService by world.di.instance<SectService>()
    
    // 触发事件时增加宗门声望
    fun onCombatVictory(sect: Entity, reputationGain: Int) {
        sectService.addReputation(sect, reputationGain)
    }
    
    // 声望达到某个值时触发特殊事件
    fun checkReputationMilestone(sect: Entity) {
        world.entity(sect) {
            val reputation = it.getComponent<SectReputation>()
            when {
                reputation.value >= 1000 -> println("宗门声名远扬！")
                reputation.value >= 500 -> println("宗门名声响亮！")
                reputation.value >= 100 -> println("宗门初有声望！")
            }
        }
    }
}
```

### 创建 Addon 扩展

```kotlin
val customReputationAddon = createAddon("custom-reputation") {
    install(sectAddon)
    injects {
        this bind singleton { new(::ReputationSystem) }
    }
    systems {
        // 注册自定义系统
    }
}
```

---

## 性能考虑

### 1. 缓存查询结果

```kotlin
// ❌ 不好：重复查询
fun displayMembers(sect: Entity) {
    repeat(100) {
        val members = sectService.getMembers(sect)  // 重复查询
    }
}

// ✅ 好：缓存结果
fun displayMembers(sect: Entity) {
    val members = sectService.getMembers(sect).toList()
    repeat(100) {
        // 使用缓存的结果
        members.forEach { println(it) }
    }
}
```

### 2. 批量操作

```kotlin
// ❌ 不好：逐个操作
fun addAllMembers(sect: Entity, players: List<Entity>) {
    players.forEach { player ->
        sectService.addMember(sect, player, MemberRole.DISCIPLE)
    }
}

// ✅ 好：在事务中操作
fun addAllMembers(sect: Entity, players: List<Entity>) {
    world.entity(sect) {
        players.forEach { player ->
            // 在单个 entity 块中添加所有成员
            sectService.addMember(sect, player, MemberRole.DISCIPLE)
        }
    }
}
```

### 3. 使用关系而非重复查询

```kotlin
// 对于频繁访问的关系，考虑缓存
class SectCache {
    private val memberCache = mutableMapOf<Entity, List<Entity>>()
    
    fun getMembersFromCache(sect: Entity, sectService: SectService): List<Entity> {
        return memberCache.getOrPut(sect) {
            sectService.getMembers(sect).map { it.first }.toList()
        }
    }
    
    fun invalidateCache(sect: Entity) {
        memberCache.remove(sect)
    }
}
```

### 4. 异步处理

```kotlin
// 对于耗时操作，考虑异步处理
suspend fun performLongOperation(sect: Entity) {
    withContext(Dispatchers.Default) {
        // 执行耗时操作
        val members = sectService.getMembers(sect).toList()
        // 处理数据...
    }
}
```

---

## 故障排除

### 问题 1: 找不到服务

```kotlin
// 错误
val sectService = world.di.instance<SectService>()
// java.util.NoSuchElementException

// 解决方案
val world = world {
    install(sectAddon)  // 确保安装了 addon
}
val sectService = world.di.instance<SectService>()
```

### 问题 2: 实体验证失败

```kotlin
// 错误
sectService.addMember(entity, player, MemberRole.DISCIPLE)
// IllegalArgumentException: 实体不是宗门

// 解决方案
if (entity.hasTag<Sect>()) {
    sectService.addMember(entity, player, MemberRole.DISCIPLE)
}
```

### 问题 3: 空指针异常

```kotlin
// 错误
val sect = sectService[sectName]
val reputation = world.entity(sect) { it.getComponent<SectReputation>() }

// 解决方案
val sect = sectService[sectName] ?: return
world.entity(sect) {
    val reputation = it.getComponent<SectReputation>()
    println(reputation.value)
}
```

---

## 最佳实践

1. **始终检查实体有效性**
   ```kotlin
   val sect = sectService[sectName] ?: return
   ```

2. **使用类型安全的组件访问**
   ```kotlin
   val money = entity.getComponent<Money?>() ?: Money(0)
   ```

3. **利用事件系统而非轮询**
   ```kotlin
   world.observeService.observe<OnUpgradeEvent> { event -> ... }
   ```

4. **缓存频繁查询的结果**
   ```kotlin
   val members = sectService.getMembers(sect).toList()
   ```

5. **使用关系管理实体之间的联系**
   ```kotlin
   sectService.getMembers(sect)
   ```

---

## 示例项目结构

```
project/
├── domain/
│   ├── models/           # 数据模型
│   └── services/         # 业务逻辑服务
├── ui/
│   ├── controllers/       # UI 控制器
│   └── views/            # 视图
├── systems/
│   ├── quest/            # 任务系统
│   ├── economy/          # 经济系统
│   └── progression/      # 进度系统
└── main/
    └── Main.kt           # 应用入口
```

---

## 常见集成检查清单

- [ ] 已安装 `sectAddon`
- [ ] 已获取服务实例
- [ ] 已验证实体类型
- [ ] 已处理空值
- [ ] 已实现事件监听
- [ ] 已测试集成功能
- [ ] 已优化性能（如有必要）
- [ ] 已添加错误处理

---

## 相关文档

- [API 快速参考](./API_QUICK_REFERENCE.md)
- [完整使用指南](./FEATURES_USAGE.md)
- [单元测试示例](../src/commonTest/kotlin/cn/jzl/sect/ecs/)

---

**版本**: 1.0.0  
**最后更新**: 2025-12-13

