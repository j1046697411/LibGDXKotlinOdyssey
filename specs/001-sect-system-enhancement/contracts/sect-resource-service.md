# API 合同: 宗门资源服务 (SectResourceService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/sect/`

## 概述

SectResourceService 是对 InventoryService 的封装，专门用于宗门资源管理，提供更直观的 API。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.sect

class SectResourceService(world: World) : EntityRelationContext(world) {

    // ==================== 资源存取 ====================
    
    /**
     * 向宗门仓库存入资源
     * @param sect 宗门实体
     * @param itemPrefab 资源类型（物品预制体）
     * @param amount 存入数量
     * @throws IllegalArgumentException 如果数量小于等于0
     */
    fun deposit(sect: Entity, itemPrefab: Entity, amount: Int)
    
    /**
     * 从宗门仓库取出资源
     * @param sect 宗门实体
     * @param itemPrefab 资源类型（物品预制体）
     * @param amount 取出数量
     * @throws IllegalStateException 如果资源不足
     */
    fun withdraw(sect: Entity, itemPrefab: Entity, amount: Int)
    
    /**
     * 批量存入资源
     * @param sect 宗门实体
     * @param resources 资源映射（物品预制体 -> 数量）
     */
    fun depositAll(sect: Entity, resources: Map<Entity, Int>)
    
    /**
     * 批量取出资源
     * @param sect 宗门实体
     * @param resources 资源映射（物品预制体 -> 数量）
     * @throws IllegalStateException 如果任一资源不足
     */
    fun withdrawAll(sect: Entity, resources: Map<Entity, Int>)

    // ==================== 资源转移 ====================
    
    /**
     * 从成员背包转移资源到宗门仓库
     * @param sect 宗门实体
     * @param member 成员实体
     * @param itemPrefab 资源类型
     * @param amount 转移数量
     * @throws IllegalStateException 如果成员资源不足
     */
    fun transferFromMember(sect: Entity, member: Entity, itemPrefab: Entity, amount: Int)
    
    /**
     * 从宗门仓库转移资源到成员背包
     * @param sect 宗门实体
     * @param member 成员实体
     * @param itemPrefab 资源类型
     * @param amount 转移数量
     * @throws IllegalStateException 如果宗门资源不足
     */
    fun transferToMember(sect: Entity, member: Entity, itemPrefab: Entity, amount: Int)

    // ==================== 查询 ====================
    
    /**
     * 获取宗门某资源的数量
     * @param sect 宗门实体
     * @param itemPrefab 资源类型
     * @return 资源数量
     */
    fun getResourceAmount(sect: Entity, itemPrefab: Entity): Int
    
    /**
     * 获取宗门所有资源
     * @param sect 宗门实体
     * @return 资源映射（物品预制体 -> 数量）
     */
    fun getAllResources(sect: Entity): Map<Entity, Int>
    
    /**
     * 检查宗门是否有足够的资源
     * @param sect 宗门实体
     * @param itemPrefab 资源类型
     * @param amount 需要的数量
     * @return 是否足够
     */
    fun hasEnoughResource(sect: Entity, itemPrefab: Entity, amount: Int): Boolean
    
    /**
     * 批量检查资源是否足够
     * @param sect 宗门实体
     * @param resources 资源映射（物品预制体 -> 数量）
     * @return 是否全部足够
     */
    fun hasEnoughResources(sect: Entity, resources: Map<Entity, Int>): Boolean
    
    /**
     * 获取缺少的资源
     * @param sect 宗门实体
     * @param resources 需要的资源（物品预制体 -> 数量）
     * @return 缺少的资源（物品预制体 -> 缺少数量），全部足够则返回空 Map
     */
    fun getMissingResources(sect: Entity, resources: Map<Entity, Int>): Map<Entity, Int>
}
```

---

## 资源类型预制体

系统预定义以下资源类型物品预制体：

| 预制体名称 | 描述 | 可堆叠 |
|------------|------|--------|
| 灵石 | 通用货币 | ✓ |
| 低级灵草 | 基础炼丹材料 | ✓ |
| 中级灵草 | 进阶炼丹材料 | ✓ |
| 高级灵草 | 高级炼丹材料 | ✓ |
| 养气丹 | 基础丹药 | ✓ |
| 筑基丹 | 进阶丹药 | ✓ |
| 精铁 | 基础锻造材料 | ✓ |
| 玉简 | 藏经阁材料 | ✓ |
| 灵木 | 建筑材料 | ✓ |

```kotlin
// 资源预制体注册（在 sectResourceAddon 中）
entities {
    val itemService by world.di.instance<ItemService>()
    
    itemService.itemPrefab(Named("灵石")) {
        it.addComponent(Description("通用货币，修仙界的硬通货"))
        it.addComponent(UnitPrice(1))
        it.addTag<Stackable>()
    }
    
    itemService.itemPrefab(Named("低级灵草")) {
        it.addComponent(Description("蕴含灵气的草药，可用于炼制基础丹药"))
        it.addComponent(UnitPrice(10))
        it.addTag<Stackable>()
    }
    
    // ... 其他资源预制体
}
```

---

## 使用示例

```kotlin
// 获取资源预制体
val lingshiPrefab = itemService["灵石"]!!
val lingcaoPrefab = itemService["低级灵草"]!!

// 存入资源
sectResourceService.deposit(sect, lingshiPrefab, 1000)
println("存入 1000 灵石")

// 查询资源
val lingshiAmount = sectResourceService.getResourceAmount(sect, lingshiPrefab)
println("当前灵石: $lingshiAmount")

// 检查资源是否足够
val upgradeCost = mapOf(
    lingshiPrefab to 500,
    lingcaoPrefab to 50
)
if (sectResourceService.hasEnoughResources(sect, upgradeCost)) {
    sectResourceService.withdrawAll(sect, upgradeCost)
    println("升级消耗资源成功")
} else {
    val missing = sectResourceService.getMissingResources(sect, upgradeCost)
    println("资源不足: $missing")
}

// 从成员转移资源到宗门
sectResourceService.transferFromMember(sect, member, lingcaoPrefab, 10)
println("成员上缴 10 低级灵草")

// 查看所有资源
val allResources = sectResourceService.getAllResources(sect)
allResources.forEach { (prefab, amount) ->
    val name = prefab.getComponent<Named>()
    println("${name.name}: $amount")
}
```

---

## 与 InventoryService 的关系

SectResourceService 内部委托给 InventoryService：

```kotlin
class SectResourceService(world: World) : EntityRelationContext(world) {
    
    private val inventoryService by world.di.instance<InventoryService>()
    
    fun deposit(sect: Entity, itemPrefab: Entity, amount: Int) {
        inventoryService.addItem(sect, itemPrefab, amount)
    }
    
    fun withdraw(sect: Entity, itemPrefab: Entity, amount: Int) {
        inventoryService.removeItem(sect, itemPrefab, amount)
    }
    
    fun getResourceAmount(sect: Entity, itemPrefab: Entity): Int {
        return inventoryService.getItemCount(sect, itemPrefab)
    }
    
    // ... 其他方法类似
}
```

这种设计的优点：
1. **语义清晰**：deposit/withdraw 比 addItem/removeItem 更符合资源管理场景
2. **复用逻辑**：底层复用 InventoryService 的物品管理能力
3. **可扩展**：未来可添加资源特有的逻辑（如资源上限、资源产出等）

