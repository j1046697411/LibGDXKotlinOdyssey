# 宗门模拟游戏 - 核心系统使用文档

## 目录

1. [宗门系统 (Sect System)](#宗门系统)
2. [角色系统 (Character System)](#角色系统)
3. [升级系统 (Leveling System)](#升级系统)
4. [货币系统 (Money System)](#货币系统)
5. [物品系统 (Item System)](#物品系统)
6. [背包系统 (Inventory System)](#背包系统)
7. [市场系统 (Market System)](#市场系统)
8. [完整示例](#完整示例)

---

## 宗门系统

### 概述

宗门系统管理宗门的创建、成员管理、声望、建筑等功能。

### 核心类

- **SectService**: 宗门服务，提供所有宗门相关操作
- **Sect**: 标签组件，标识一个实体是宗门
- **SectReputation**: 宗门声望值
- **MemberData**: 成员数据（角色、贡献度）
- **MemberRole**: 成员角色枚举（宗主、长老、弟子）

### 基本使用

#### 创建宗门

```kotlin
val sectAddon = createAddon("sect") { 
    install(coreAddon)
    // ... 其他安装
}

val world = world {
    install(sectAddon)
}

val sectService = world.di.instance<SectService>()

// 创建宗主
val leader = world.entity {
    it.addComponent(Named("玄天道长"))
}

// 创建宗门
val sect = sectService.createSect("飞仙派", leader) {
    // 可选：添加初始化逻辑
}
```

#### 通过名称查询宗门

```kotlin
// 查询已创建的宗门
val sect = sectService["飞仙派"]
if (sect != null) {
    println("找到宗门")
} else {
    println("宗门不存在")
}
```

#### 管理成员

```kotlin
// 创建弟子
val disciple = world.entity {
    it.addComponent(Named("弟子1"))
}

// 添加成员
sectService.addMember(sect, disciple, MemberRole.DISCIPLE)

// 获取成员数据
val memberData = sectService.getMemberData(sect, disciple)
println("角色: ${memberData?.role}")
println("贡献度: ${memberData?.contribution?.value}")

// 改变成员角色
sectService.changeMemberRole(sect, disciple, MemberRole.ELDER)

// 增加贡献度
sectService.addContribution(sect, disciple, 100)

// 获取所有成员
val members = sectService.getMembers(sect)
members.forEach { (entity, data) ->
    println("成员ID: ${entity.id}, 角色: ${data.role}")
}

// 移除成员
sectService.removeMember(sect, disciple)
```

#### 管理宗门属性

```kotlin
// 增加声望
sectService.addReputation(sect, 100)

// 查看声望
world.entity(sect) {
    val reputation = it.getComponent<SectReputation>()
    println("宗门声望: ${reputation.value}")
}

// 增加宗门经验值（用于宗门升级）
sectService.addExperience(sect, 500)
```

#### 创建建筑

```kotlin
// 创建炼丹房
val alchemyHall = sectService.createAlchemyHall(sect, Named("长生殿")) {
    // 可选：添加初始化逻辑
}

// 创建藏经阁
val library = sectService.createLibrary(sect, Named("太初阁")) {
    // 可选：添加初始化逻辑
}

// 验证建筑
world.entity(alchemyHall) {
    val isBuilding = it.hasTag<Building>()
    val isAlchemy = it.hasTag<AlchemyHall>()
    println("是建筑: $isBuilding, 是炼丹房: $isAlchemy")
}
```

---

## 角色系统

### 概述

角色系统管理游戏中可玩角色的创建和生命周期。

### 核心类

- **CharacterService**: 角色服务
- **Character**: 标签组件，标识一个实体是角色
- **OnCreatedCharacter**: 角色创建完成事件

### 基本使用

#### 创建角色

```kotlin
val characterAddon = createAddon("Character") {
    install(levelingAddon)
    // ... 其他安装
}

val world = world {
    install(characterAddon)
}

val characterService = world.di.instance<CharacterService>()

// 创建角色
val character = characterService.createCharacter(Named("李白")) {
    // 可选：添加初始化逻辑
    // 例如：添加初始属性
}
```

#### 查询角色

```kotlin
// 通过名称查询
val character = characterService["李白"]
if (character != null) {
    println("找到角色")
}

// 验证角色属性
world.entity(character) {
    val isCharacter = it.hasTag<Character>()
    val isUpgradeable = it.hasTag<Upgradeable>()
    println("是角色: $isCharacter, 可升级: $isUpgradeable")
}
```

---

## 升级系统

### 概述

升级系统管理实体的经验值、等级和升级逻辑。任何添加 `Upgradeable` 标签的实体都可以获得经验并升级。

### 核心类

- **LevelingService**: 升级服务
- **Upgradeable**: 标签组件，标识实体可升级
- **OnUpgradeEvent**: 升级事件（包含旧等级和新等级）
- **ExperienceFormula**: 经验公式（可自定义）

### 基本使用

#### 创建可升级实体

```kotlin
val levelingService = world.di.instance<LevelingService>()
val attributeService = world.di.instance<AttributeService>()

val entity = world.entity {
    it.addComponent(Named("测试对象"))
    levelingService.upgradeable(this, it)
}

// 获取属性
val attributeLevel = attributeService.attribute(LevelingService.ATTRIBUTE_LEVEL)
val attributeExperience = attributeService.attribute(LevelingService.ATTRIBUTE_EXPERIENCE)

// 查看初始等级和经验
world.entity(entity) {
    val level = entity.getRelation<AttributeValue?>(attributeLevel)
    val exp = entity.getRelation<AttributeValue?>(attributeExperience)
    println("初始等级: ${level?.value}, 初始经验: ${exp?.value}")
}
```

#### 添加经验和升级

```kotlin
// 添加经验
levelingService.addExperience(entity, 100)

// 查看升级后的等级
world.entity(entity) {
    val level = entity.getRelation<AttributeValue?>(attributeLevel)
    val exp = entity.getRelation<AttributeValue?>(attributeExperience)
    println("当前等级: ${level?.value}, 剩余经验: ${exp?.value}")
}
```

#### 自定义经验公式

```kotlin
// 默认公式：每级需要 100 经验
val defaultFormula = ExperienceFormula { 100 }

// 自定义公式：每级需要 level * 50 经验
val customFormula = ExperienceFormula { level -> level * 50 }

val entity = world.entity {
    it.addComponent(Named("自定义升级对象"))
    levelingService.upgradeable(this, it, customFormula)
}

// 现在使用自定义公式
levelingService.addExperience(entity, 50)  // 升级到2级
```

#### 监听升级事件

```kotlin
// 订阅升级事件
world.observeService.observe<OnUpgradeEvent> { event ->
    println("升级事件: ${event.oldLevel} -> ${event.newLevel}")
}
```

---

## 货币系统

### 概述

货币系统管理实体的金钱和交易功能。

### 核心类

- **MoneyService**: 货币服务
- **Money**: 金钱组件

### 基本使用

#### 添加金钱

```kotlin
val entity = world.entity {
    it.addComponent(Money(100))
}

// 查看金钱
world.entity(entity) {
    val money = it.getComponent<Money>()
    println("金钱: ${money.value}")
}
```

#### 货币转移

```kotlin
val buyer = world.entity {
    it.addComponent(Money(500))
}

val seller = world.entity {
    it.addComponent(Money(100))
}

val moneyService = world.di.instance<MoneyService>()

// 转移货币
moneyService.transferMoney(buyer, seller, 200) {
    // 可以在这里添加额外的业务逻辑
    println("交易完成")
}

// 验证转移结果
world.entity(buyer) {
    val buyerMoney = it.getComponent<Money>()
    println("买家剩余金钱: ${buyerMoney.value}")  // 300
}

world.entity(seller) {
    val sellerMoney = it.getComponent<Money>()
    println("卖家现有金钱: ${sellerMoney.value}")  // 300
}
```

#### 便捷函数

```kotlin
// 检查是否有足够金钱
val hasEnough = world.entity(entity) {
    hasEnoughMoney(entity, 50)
}

// 获取金钱数量
val amount = world.entity(entity) {
    getMoney(entity)
}

// 增加金钱
world.entity(entity) {
    increaseMoney(entity, 100)
}

// 减少金钱
world.entity(entity) {
    decreaseMoney(entity, 50)
}
```

---

## 物品系统

### 概述

物品系统定义和管理游戏中的物品类型和属性。

### 核心类

- **ItemService**: 物品服务
- **Item**: 物品标签
- **ItemData**: 物品数据（名称、描述、价格等）

### 基本使用

#### 创建物品模板

```kotlin
val itemService = world.di.instance<ItemService>()

// 创建物品预制体
val sword = itemService.createItemPrefab(Named("长剑")) {
    // 可选：添加物品属性
}
```

---

## 背包系统

### 概述

背包系统管理实体拥有的物品及其数量。

### 核心类

- **InventoryService**: 背包服务
- **ItemCount**: 物品计数组件

### 基本使用

#### 添加物品到背包

```kotlin
val inventoryService = world.di.instance<InventoryService>()

val player = world.entity {
    it.addComponent(Named("玩家1"))
}

// 添加物品
inventoryService.addItem(player, sword, 1)
```

#### 查询背包

```kotlin
// 获取特定物品数量
val swordCount = inventoryService.getItemCount(player, sword)
println("长剑数量: $swordCount")

// 检查是否有足够物品
val hasEnough = inventoryService.hasEnoughItems(player, sword, 1)

// 获取所有物品
val allItems = inventoryService.getAllItems(player)
allItems.forEach { itemEntity ->
    println("物品ID: $itemEntity")
}
```

#### 移除物品

```kotlin
// 移除物品
inventoryService.removeItem(player, sword, 1)
```

---

## 市场系统

### 概述

市场系统管理物品买卖功能。

### 基本使用

```kotlin
val marketService = world.di.instance<MarketService>()

// 在市场上出售物品
val listing = marketService.sellItem(seller, sword, 1, price = 500)

// 购买物品
marketService.buyItem(buyer, sword, seller, quantity = 1)
```

---

## 完整示例

### 创建一个完整的游戏场景

```kotlin
// 初始化世界
val world = world {
    install(sectAddon)  // 包含 characterAddon, levelingAddon, moneyAddon 等
}

val sectService = world.di.instance<SectService>()
val characterService = world.di.instance<CharacterService>()
val levelingService = world.di.instance<LevelingService>()
val inventoryService = world.di.instance<InventoryService>()

// === 创建宗门 ===
val leader = world.entity {
    it.addComponent(Named("宗主"))
    it.addComponent(Money(10000))
}

val sect = sectService.createSect("飞仙派", leader) {}

// === 创建弟子 ===
val disciple = world.entity {
    it.addComponent(Named("弟子1"))
    it.addComponent(Money(1000))
    // 设置为可升级
}

characterService.createCharacter(Named("李白")) {}

// === 管理宗门 ===
sectService.addMember(sect, disciple, MemberRole.DISCIPLE)

// === 建筑建设 ===
val alchemyHall = sectService.createAlchemyHall(sect, Named("长生殿")) {}

// === 经验和升级 ===
levelingService.addExperience(sect, 500)  // 宗门升级
levelingService.addExperience(leader, 1000)  // 宗主升级

// === 货币交易 ===
val seller = world.entity {
    it.addComponent(Named("商人"))
    it.addComponent(Money(5000))
}

val moneyService = world.di.instance<MoneyService>()
moneyService.transferMoney(disciple, seller, 100) {
    println("交易成功")
}

// === 物品管理 ===
inventoryService.addItem(disciple, sword, 10)
val swordCount = inventoryService.getItemCount(disciple, sword)
println("弟子持有长剑: $swordCount")

// === 查询信息 ===
sectService.getMembers(sect).forEach { (entity, data) ->
    val name = entity.getComponent<Named>()?.name
    println("成员: $name, 角色: ${data.role}")
}
```

---

## 常见用法模式

### 模式1：创建并配置实体

```kotlin
val entity = world.entity {
    it.addComponent(Named("我的对象"))
    it.addComponent(Money(100))
    levelingService.upgradeable(this, it)
}
```

### 模式2：查询和修改实体

```kotlin
world.entity(entity) {
    val money = it.getComponent<Money>()
    it.addComponent(money.copy(value = money.value + 50))
}
```

### 模式3：处理关系

```kotlin
// 添加关系
sectService.addMember(sect, player, MemberRole.DISCIPLE)

// 查询关系
val memberData = sectService.getMemberData(sect, player)

// 获取所有关系
sectService.getMembers(sect)
```

### 模式4：事件监听

```kotlin
// 监听升级事件
world.observeService.observe<OnUpgradeEvent> { event ->
    println("升级: ${event.oldLevel} -> ${event.newLevel}")
}

// 监听角色创建事件
world.observeService.observe<OnCreatedCharacter> { event ->
    println("新角色创建: $event")
}
```

---

## 依赖关系

系统之间的依赖关系如下：

```
coreAddon (基础)
├─ levelingAddon (升级系统)
├─ moneyAddon (货币系统)
│  └─ moneyService
├─ characterAddon (角色系统)
│  └─ levelingAddon
├─ sectAddon (宗门系统)
│  ├─ coreAddon
│  ├─ levelingAddon
│  ├─ moneyAddon
│  └─ characterAddon
└─ inventoryAddon (背包系统)
   ├─ coreAddon
   └─ itemAddon
```

在安装时，确保依赖的 addon 已经被安装。

---

## 最佳实践

1. **始终验证实体存在**: 在操作实体之前检查其是否存在
2. **使用命名实体**: 使用 `Named` 组件给重要实体命名，便于查询
3. **处理 null 值**: 组件可能不存在，使用 `?:` 提供默认值
4. **事件驱动**: 使用事件系统而不是直接回调
5. **关系管理**: 使用关系而不是直接引用来保持灵活性

---

## 故障排除

### 问题1：找不到服务实例

**原因**: 未正确安装 addon

**解决方案**:
```kotlin
val world = world {
    install(sectAddon)  // 确保安装了包含所需服务的 addon
}
```

### 问题2：实体标签验证失败

**原因**: 实体不是正确的类型

**解决方案**:
```kotlin
// 在操作前检查标签
if (!entity.hasTag<Sect>()) {
    error("实体不是宗门")
}
```

### 问题3：组件不存在

**原因**: 实体没有该组件

**解决方案**:
```kotlin
// 使用安全调用
val money = entity.getComponent<Money?>()
if (money != null) {
    println("金钱: ${money.value}")
}
```

---

## 相关测试

查看以下测试文件了解更多使用示例：

- `SectServiceTest.kt` - 宗门系统测试
- `CharacterServiceTest.kt` - 角色系统测试
- `LevelingServiceTest.kt` - 升级系统测试
- `MoneyServiceTest.kt` - 货币系统测试

---

**文档版本**: 1.0.0  
**更新日期**: 2025-12-13  
**维护者**: 项目团队

