package cn.jzl.sect.ecs.building

import cn.jzl.ecs.Entity

/**
 * 建筑基础消耗组件
 * 存储建筑1级时的建造/升级消耗
 */
data class BuildingBaseCost(
    val spiritStones: Int = 100,               // 基础灵石消耗
    val materials: Map<Entity, Int> = emptyMap() // 其他材料消耗
)

/**
 * 建筑升级错误
 */
sealed class BuildingUpgradeError {
    /**
     * 资源不足
     */
    data class InsufficientResources(
        val missing: Map<Entity, Int>
    ) : BuildingUpgradeError()

    /**
     * 宗门等级不足
     */
    data class SectLevelTooLow(
        val required: Long,
        val current: Long
    ) : BuildingUpgradeError()

    /**
     * 已达到最大等级
     */
    data class MaxLevelReached(
        val currentLevel: Long,
        val maxLevel: Long
    ) : BuildingUpgradeError()

    /**
     * 建筑正在升级中
     */
    data object UpgradeInProgress : BuildingUpgradeError()

    /**
     * 权限不足
     */
    data object PermissionDenied : BuildingUpgradeError()
}

/**
 * 计算升级消耗
 * @param baseCost 基础消耗
 * @param currentLevel 当前等级
 * @param multiplier 消耗倍率（默认1.5）
 * @return 升级所需资源
 */
fun calculateUpgradeCost(
    baseCost: BuildingBaseCost,
    currentLevel: Long,
    multiplier: Float = 1.5f
): Map<Entity, Int> {
    val factor = calculateUpgradeFactor(multiplier, currentLevel)
    val result = mutableMapOf<Entity, Int>()

    // 计算材料消耗
    baseCost.materials.forEach { (material, baseAmount) ->
        result[material] = (baseAmount * factor).toInt()
    }

    return result
}

/**
 * 计算升级所需灵石
 */
fun calculateUpgradeSpiritStoneCost(
    baseCost: BuildingBaseCost,
    currentLevel: Long,
    multiplier: Float = 1.5f
): Int {
    val factor = calculateUpgradeFactor(multiplier, currentLevel)
    return (baseCost.spiritStones * factor).toInt()
}

/**
 * 计算倍率因子
 */
private fun calculateUpgradeFactor(multiplier: Float, currentLevel: Long): Float {
    var factor = 1.0f
    repeat((currentLevel - 1).toInt()) { factor *= multiplier }
    return factor
}

