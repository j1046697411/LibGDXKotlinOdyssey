# 核心系统 API 快速参考

## 宗门系统 API

### SectService

```kotlin
// 创建宗门
createSect(name: String, leader: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity
createSect(named: Named, leader: Entity, block: EntityCreateContext.(Entity) -> Unit): Entity

// 查询宗门
operator fun get(name: String): Entity?

// 成员管理
addMember(sect: Entity, player: Entity, role: MemberRole): Unit
removeMember(sect: Entity, player: Entity): Unit
getMemberData(sect: Entity, player: Entity): MemberData?
getMembers(sect: Entity): Sequence<Pair<Entity, MemberData>>
changeMemberRole(sect: Entity, player: Entity, newRole: MemberRole): Unit
addContribution(sect: Entity, player: Entity, amount: Int): Unit

// 宗门属性
addReputation(sect: Entity, amount: Int): Unit
addExperience(sect: Entity, exp: Long): Unit

// 建筑
createAlchemyHall(sect: Entity, named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity
createLibrary(sect: Entity, named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity
```

### 数据类

```kotlin
data class MemberData(
    val player: Entity,
    val role: MemberRole,
    val contribution: Contribution
)

enum class MemberRole {
    LEADER,      // 宗主
    ELDER,       // 长老
    DISCIPLE     // 弟子
}

@JvmInline value class SectReputation(val value: Int)
@JvmInline value class Contribution(val value: Int)
```

---

## 角色系统 API

### CharacterService

```kotlin
// 创建角色
fun createCharacter(named: Named, block: EntityCreateContext.(Entity) -> Unit): Entity

// 查询角色
operator fun get(name: String): Entity?
```

---

## 升级系统 API

### LevelingService

```kotlin
// 创建可升级实体
fun upgradeable(
    context: EntityCreateContext,
    entity: Entity,
    formula: ExperienceFormula? = null
): Unit

// 添加经验
fun addExperience(entity: Entity, exp: Long): Unit

companion object {
    val ATTRIBUTE_LEVEL: Named       // 等级属性
    val ATTRIBUTE_EXPERIENCE: Named  // 经验属性
}
```

### 数据类

```kotlin
fun interface ExperienceFormula {
    fun getExperienceForLevel(level: Long): Long
}

data class OnUpgradeEvent(
    val oldLevel: Long,
    val newLevel: Long
)

@JvmInline value class AttributeValue(val value: Long)
```

---

## 货币系统 API

### MoneyService

```kotlin
// 转移货币
fun transferMoney(
    buyer: Entity,
    seller: Entity,
    money: Int,
    block: () -> Unit
): Unit
```

### 便捷函数

```kotlin
// 在 EntityCreateContext 中使用
fun hasEnoughMoney(entity: Entity, money: Int): Boolean
fun getMoney(entity: Entity): Int
fun increaseMoney(entity: Entity, money: Int): Unit
fun decreaseMoney(entity: Entity, money: Int): Unit
```

### 数据类

```kotlin
@JvmInline value class Money(val value: Int)
```

---

## 背包系统 API

### InventoryService

```kotlin
// 物品查询
fun getAllItems(owner: Entity): Sequence<Entity>
fun getItemCount(owner: Entity, itemPrefab: Entity): Int
fun hasEnoughItems(owner: Entity, itemPrefab: Entity, count: Int): Boolean

// 物品操作
fun addItem(owner: Entity, itemPrefab: Entity, count: Int): Unit
fun removeItem(owner: Entity, itemPrefab: Entity, count: Int): Unit
fun moveItem(from: Entity, to: Entity, itemPrefab: Entity, count: Int): Unit
```

---

## 常见组件

### 核心组件

```kotlin
// 命名组件 - 给实体命名
data class Named(val name: String)

// 标签组件
sealed class Sect              // 宗门标签
sealed class Character         // 角色标签
sealed class Building          // 建筑标签
sealed class AlchemyHall       // 炼丹房标签
sealed class Library           // 藏经阁标签
sealed class Upgradeable       // 可升级标签
```

---

## 最常用的操作模式

### 1. 创建并初始化实体

```kotlin
val entity = world.entity {
    it.addComponent(Named("对象名称"))
    it.addComponent(Money(100))
    // 更多初始化...
}
```

### 2. 获取和修改组件

```kotlin
world.entity(entity) {
    val money = it.getComponent<Money?>()
    money?.let {
        it.addComponent(Money(it.value + 50))
    }
}
```

### 3. 通过服务执行操作

```kotlin
val sectService = world.di.instance<SectService>()
sectService.addMember(sect, player, MemberRole.DISCIPLE)
```

### 4. 查询实体

```kotlin
val sect = sectService["宗门名称"]
val members = sectService.getMembers(sect)
```

### 5. 处理事件

```kotlin
world.observeService.observe<OnUpgradeEvent> { event ->
    println("升级: ${event.oldLevel} -> ${event.newLevel}")
}
```

---

## 组件操作方法

### 添加/修改组件

```kotlin
it.addComponent(ComponentData(...))  // 添加或覆盖
```

### 获取组件

```kotlin
it.getComponent<ComponentType>()      // 获取（类型不匹配异常）
it.getComponent<ComponentType?>()     // 安全获取（可为 null）
```

### 检查标签

```kotlin
it.hasTag<TagType>()                 // 检查是否有标签
it.addTag<TagType>()                 // 添加标签
```

### 关系操作

```kotlin
it.addRelation<RelationType>(target)                      // 添加关系
it.removeRelation<RelationType>(target)                   // 移除关系
entity.getRelation<RelationType?>(target)                 // 查询关系
entity.getRelationsWithData<DataType>()                   // 获取所有关系数据
```

---

## Addon 安装

```kotlin
val world = world {
    // 基础 addon
    install(coreAddon)
    
    // 游戏系统 addon
    install(levelingAddon)          // 升级系统
    install(moneyAddon)             // 货币系统
    install(characterAddon)         // 角色系统
    install(sectAddon)              // 宗门系统（包含上述所有）
    install(inventoryAddon)         // 背包系统
    install(itemAddon)              // 物品系统
    install(marketAddon)            // 市场系统
    
    // 自动依赖安装
    install(sectAddon)  // 会自动安装所有依赖的 addon
}
```

---

## 常见错误和解决方案

| 错误 | 原因 | 解决方案 |
|------|------|--------|
| NoSuchElementException | 组件不存在 | 使用 `getComponent<T?>()` 或检查存在 |
| IllegalArgumentException | 实体标签验证失败 | 确保实体类型正确 |
| ServiceNotFoundException | 服务未注册 | 检查 addon 是否已安装 |
| NullPointerException | 空引用 | 使用安全调用 `?.` |

---

## 调试技巧

### 检查实体组件

```kotlin
world.entity(entity) {
    println("组件: ${it.getComponent<Money?>()}")
    println("标签: ${it.hasTag<Sect>()}")
}
```

### 遍历所有成员

```kotlin
sectService.getMembers(sect).forEach { (entity, data) ->
    println("成员: ${entity.id}, 角色: ${data.role}")
}
```

### 获取服务实例

```kotlin
val service = world.di.instance<ServiceType>()
```

---

## 相关资源

- 详细文档: `docs/FEATURES_USAGE.md`
- 测试示例: `src/commonTest/kotlin/cn/jzl/sect/ecs/`
- 源代码: `src/commonMain/kotlin/cn/jzl/sect/ecs/`

---

**版本**: 1.0.0  
**最后更新**: 2025-12-13

