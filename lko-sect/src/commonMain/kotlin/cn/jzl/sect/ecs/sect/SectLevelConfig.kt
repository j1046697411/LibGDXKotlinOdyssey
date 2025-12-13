package cn.jzl.sect.ecs.sect

import cn.jzl.sect.ecs.building.BuildingType

/**
 * 宗门等级配置
 * 定义每个等级的解锁内容和奖励
 */
object SectLevelConfig {

    /**
     * 获取指定等级的解锁配置
     */
    fun getUnlocksForLevel(level: Long): SectLevelUnlocks = when (level) {
        1L -> SectLevelUnlocks(
            newBuildingTypes = setOf(BuildingType.ALCHEMY_HALL, BuildingType.LIBRARY),
            additionalMemberSlots = 0,
            additionalBuildingSlots = 2
        )
        2L -> SectLevelUnlocks(
            newBuildingTypes = setOf(BuildingType.TRAINING_HALL),
            additionalMemberSlots = 10,
            additionalBuildingSlots = 2
        )
        3L -> SectLevelUnlocks(
            newBuildingTypes = setOf(BuildingType.TREASURE_VAULT),
            additionalMemberSlots = 10,
            additionalBuildingSlots = 2
        )
        4L -> SectLevelUnlocks(
            additionalMemberSlots = 15,
            additionalBuildingSlots = 3
        )
        5L -> SectLevelUnlocks(
            additionalMemberSlots = 20,
            additionalBuildingSlots = 3
        )
        else -> SectLevelUnlocks(
            additionalMemberSlots = 10,
            additionalBuildingSlots = 2
        )
    }

    /**
     * 获取指定等级所需经验值
     */
    fun getRequiredExperience(level: Long): Long {
        // 经验公式: 100 * level^2
        return 100L * level * level
    }

    /**
     * 获取累计到指定等级所需的总经验
     */
    fun getTotalExperienceForLevel(level: Long): Long {
        var total = 0L
        for (l in 1 until level) {
            total += getRequiredExperience(l)
        }
        return total
    }

    /**
     * 获取指定等级可用的建筑类型
     */
    fun getAvailableBuildingTypes(level: Long): Set<BuildingType> {
        val result = mutableSetOf<BuildingType>()
        for (l in 1..level) {
            result.addAll(getUnlocksForLevel(l).newBuildingTypes)
        }
        return result
    }

    /**
     * 获取指定等级的最大成员数
     */
    fun getMaxMemberCount(level: Long): Int {
        val baseCount = 30 // 基础成员数
        var additional = 0
        for (l in 2..level) {
            additional += getUnlocksForLevel(l).additionalMemberSlots
        }
        return baseCount + additional
    }

    /**
     * 获取指定等级的最大建筑槽位
     */
    fun getMaxBuildingSlots(level: Long): Int {
        var total = 0
        for (l in 1..level) {
            total += getUnlocksForLevel(l).additionalBuildingSlots
        }
        return total
    }
}

