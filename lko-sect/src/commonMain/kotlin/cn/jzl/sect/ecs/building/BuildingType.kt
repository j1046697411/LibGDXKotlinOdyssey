package cn.jzl.sect.ecs.building

import cn.jzl.ecs.Entity

/**
 * 建筑类型枚举
 */
enum class BuildingType {
    ALCHEMY_HALL,    // 炼丹房
    LIBRARY,         // 藏经阁
    TRAINING_HALL,   // 练功房
    TREASURE_VAULT   // 藏宝阁
}

/**
 * 建筑配置
 * 定义建筑的基础属性和升级参数
 */
data class BuildingConfig(
    val type: BuildingType,
    val baseCost: Map<Entity, Int>,           // 基础建造消耗
    val baseEfficiency: Float = 1.0f,          // 基础效率
    val baseCapacity: Int = 100,               // 基础容量
    val costMultiplier: Float = 1.5f,          // 升级消耗倍率
    val efficiencyPerLevel: Float = 0.1f,      // 每级效率增加
    val capacityPerLevel: Int = 50,            // 每级容量增加
    val requiredSectLevel: Long = 1L           // 建造所需宗门等级
)

/**
 * 建筑等级解锁配置
 */
data class BuildingUnlockConfig(
    val buildingType: BuildingType,
    val requiredSectLevel: Long
)

