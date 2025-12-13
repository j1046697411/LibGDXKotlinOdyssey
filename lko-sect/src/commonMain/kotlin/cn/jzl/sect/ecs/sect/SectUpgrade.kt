package cn.jzl.sect.ecs.sect

import cn.jzl.ecs.Entity
import cn.jzl.sect.ecs.building.BuildingType

/**
 * 宗门升级事件
 */
data class OnSectLevelUp(
    val sect: Entity,
    val oldLevel: Long,
    val newLevel: Long,
    val unlocks: SectLevelUnlocks
)

/**
 * 宗门等级解锁内容
 */
data class SectLevelUnlocks(
    val newBuildingTypes: Set<BuildingType> = emptySet(),  // 新解锁的建筑类型
    val additionalMemberSlots: Int = 0,                     // 增加的成员配额
    val additionalBuildingSlots: Int = 0,                   // 增加的建筑槽位
    val bonusResources: Map<Entity, Int> = emptyMap()       // 奖励资源
)

/**
 * 宗门升级奖励配置
 */
data class SectUpgradeReward(
    val level: Long,
    val unlocks: SectLevelUnlocks
)

