package cn.jzl.sect.ecs.building

import cn.jzl.sect.ecs.Building

/**
 * 练功房
 * 提升弟子修炼效率
 */
sealed class TrainingHall : Building()

/**
 * 藏宝阁
 * 存储珍贵物品，提供额外存储空间
 */
sealed class TreasureVault : Building()

/**
 * 建筑类型组件
 */
@JvmInline
value class BuildingTypeComponent(val type: BuildingType)

/**
 * 建筑效率组件
 */
@JvmInline
value class BuildingEfficiency(val value: Float) {
    companion object {
        val DEFAULT = BuildingEfficiency(1.0f)
    }
}

/**
 * 建筑容量组件
 */
@JvmInline
value class BuildingCapacity(val value: Int) {
    companion object {
        val DEFAULT = BuildingCapacity(100)
    }
}

