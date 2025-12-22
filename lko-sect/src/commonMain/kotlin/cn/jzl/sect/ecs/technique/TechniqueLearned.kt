package cn.jzl.sect.ecs.technique

import cn.jzl.ecs.Entity

/**
 * 功法学习记录
 * 作为弟子与功法之间的关系数据
 */
data class TechniqueLearned(
    val technique: Entity,      // 功法实体
    val learnTime: Long,        // 学习时间戳
    val proficiency: Int = 0,   // 熟练度
    val isEquipped: Boolean = false // 是否装备
) {
    companion object {
        const val MAX_PROFICIENCY = 100
    }
}

/**
 * 藏经阁收录关系
 * 表示功法被收录在某个藏经阁中
 */
data class LibraryContains(
    val technique: Entity,
    val addTime: Long
)

