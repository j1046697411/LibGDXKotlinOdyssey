# API 合同: 建筑服务 (BuildingService)

**功能分支**: `001-sect-system-enhancement`
**创建时间**: 2025-12-13
**模块**: `lko-sect/ecs/building/`

## 概述

BuildingService 提供建筑的创建、升级和属性管理功能，复用 LevelingService 管理建筑等级。

---

## 服务接口

```kotlin
package cn.jzl.sect.ecs.building

class BuildingService(world: World) : EntityRelationContext(world) {

    // ==================== 建筑创建 ====================
    
    /**
     * 创建炼丹房
     * @param sect 宗门实体
     * @param named 建筑名称
     * @param block 额外配置
     * @return 建筑实体
     * @throws IllegalArgumentException 如果宗门不存在
     * @throws IllegalStateException 如果建筑类型未解锁
     * @throws IllegalStateException 如果建筑容量已满
     */
    @ECSDsl
    fun createAlchemyHall(
        sect: Entity,
        named: Named,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity
    
    /**
     * 创建藏经阁
     */
    @ECSDsl
    fun createLibrary(
        sect: Entity,
        named: Named,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity
    
    /**
     * 创建练功房
     */
    @ECSDsl
    fun createTrainingHall(
        sect: Entity,
        named: Named,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity
    
    /**
     * 创建藏宝阁
     */
    @ECSDsl
    fun createTreasureVault(
        sect: Entity,
        named: Named,
        block: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity

    // ==================== 建筑升级 ====================
    
    /**
     * 计算建筑升级所需资源
     * @param building 建筑实体
     * @return 资源消耗 (物品预制体 -> 数量)
     */
    fun calculateUpgradeCost(building: Entity): Map<Entity, Int>
    
    /**
     * 检查建筑是否可升级
     * @param building 建筑实体
     * @return 可升级返回 null，否则返回不可升级原因
     */
    fun canUpgrade(building: Entity): BuildingUpgradeError?
    
    /**
     * 升级建筑
     * @param building 建筑实体
     * @throws IllegalStateException 如果资源不足
     * @throws IllegalStateException 如果宗门等级不足
     */
    fun upgrade(building: Entity)

    // ==================== 查询 ====================
    
    /**
     * 获取宗门所有建筑
     * @param sect 宗门实体
     * @return 建筑实体序列
     */
    fun getBuildingsBySect(sect: Entity): Sequence<Entity>
    
    /**
     * 获取建筑等级
     * @param building 建筑实体
     * @return 当前等级
     */
    fun getBuildingLevel(building: Entity): Long
    
    /**
     * 获取建筑效率
     * @param building 建筑实体
     * @return 效率值
     */
    fun getBuildingEfficiency(building: Entity): Long
    
    /**
     * 获取建筑容量
     * @param building 建筑实体
     * @return 容量值
     */
    fun getBuildingCapacity(building: Entity): Long
    
    /**
     * 获取宗门已解锁的建筑类型
     * @param sect 宗门实体
     * @return 已解锁的建筑类型集合
     */
    fun getUnlockedBuildingTypes(sect: Entity): Set<BuildingType>
    
    /**
     * 获取宗门建筑容量
     * @param sect 宗门实体
     * @return 当前建筑数 / 最大建筑数
     */
    fun getBuildingCapacity(sect: Entity): Pair<Int, Int>

    // ==================== 配置 ====================
    
    companion object {
        /** 建筑类型定义 */
        val BUILDING_TYPES = mapOf(
            BuildingType.ALCHEMY_HALL to BuildingConfig(
                baseEfficiency = 100,
                baseCapacity = 10,
                baseCost = mapOf("灵石" to 500, "精铁" to 50),
                unlockLevel = 1
            ),
            BuildingType.LIBRARY to BuildingConfig(
                baseEfficiency = 100,
                baseCapacity = 20,
                baseCost = mapOf("灵石" to 800, "玉简" to 30),
                unlockLevel = 1
            ),
            BuildingType.TRAINING_HALL to BuildingConfig(
                baseEfficiency = 100,
                baseCapacity = 5,
                baseCost = mapOf("灵石" to 1000, "灵木" to 100),
                unlockLevel = 2
            ),
            BuildingType.TREASURE_VAULT to BuildingConfig(
                baseEfficiency = 100,
                baseCapacity = 50,
                baseCost = mapOf("灵石" to 2000, "灵金" to 50),
                unlockLevel = 3
            )
        )
    }
}
```

---

## 数据类型

```kotlin
enum class BuildingType {
    ALCHEMY_HALL,   // 炼丹房
    LIBRARY,        // 藏经阁
    TRAINING_HALL,  // 练功房
    TREASURE_VAULT  // 藏宝阁
}

data class BuildingConfig(
    val baseEfficiency: Long,           // 基础效率
    val baseCapacity: Long,             // 基础容量
    val baseCost: Map<String, Int>,     // 升级基础消耗
    val unlockLevel: Long               // 解锁所需宗门等级
)

sealed class BuildingUpgradeError {
    data class InsufficientResources(val missing: Map<Entity, Int>) : BuildingUpgradeError()
    data class SectLevelTooLow(val required: Long, val actual: Long) : BuildingUpgradeError()
    data class MaxLevelReached(val maxLevel: Long) : BuildingUpgradeError()
}
```

---

## 升级公式

建筑升级资源消耗采用指数增长公式：

```
实际消耗 = 基础消耗 × 1.5^当前等级
```

建筑属性提升公式：
```
效率 = 基础效率 × (1 + 等级 × 0.1)
容量 = 基础容量 × (1 + 等级 × 0.2)
```

---

## 事件发射

| 事件 | 触发时机 |
|------|----------|
| `OnBuildingUpgraded` | `upgrade` 成功后 |

---

## 使用示例

```kotlin
// 创建炼丹房
val alchemyHall = buildingService.createAlchemyHall(
    sect = sectEntity,
    named = Named("青云炼丹房")
) {}

// 检查升级条件
val error = buildingService.canUpgrade(alchemyHall)
if (error == null) {
    // 计算升级消耗
    val cost = buildingService.calculateUpgradeCost(alchemyHall)
    println("升级需要: $cost")
    
    // 执行升级
    buildingService.upgrade(alchemyHall)
}

// 查询建筑属性
val level = buildingService.getBuildingLevel(alchemyHall)
val efficiency = buildingService.getBuildingEfficiency(alchemyHall)
println("等级: $level, 效率: $efficiency")
```

