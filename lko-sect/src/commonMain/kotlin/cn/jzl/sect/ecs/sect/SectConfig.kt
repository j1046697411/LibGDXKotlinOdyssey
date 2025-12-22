package cn.jzl.sect.ecs.sect

/**
 * 宗门配置
 * 存储宗门的各种配置参数
 */
data class SectConfig(
    val maxConcurrentTasks: Int = 5,            // 弟子最大并发任务数
    val taskTimeoutMultiplier: Float = 1.0f,    // 任务超时倍率
    val contributionBonusRate: Float = 1.0f,    // 贡献度加成倍率
    val welfareBonusRate: Float = 1.0f,         // 福利加成倍率
    val maxBuildingCount: Int = 10,             // 最大建筑数量
    val buildingSlotPerLevel: Int = 2           // 每级增加的建筑槽位
) {
    /**
     * 获取当前等级可用的建筑槽位数
     */
    fun getBuildingSlots(sectLevel: Long): Int =
        maxBuildingCount.coerceAtMost((buildingSlotPerLevel * sectLevel).toInt())
}

