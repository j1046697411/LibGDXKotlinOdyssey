package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

/**
 * 表示一个调度器，用于管理ECS系统中的任务调度
 *
 * @property data 内部存储的64位数据，包含调度器的ID和版本信息
 */
@JvmInline
value class Schedule internal constructor(private val data: Long) {
    /**
     * 调度器的唯一标识符
     */
    val id: Int get() = data.low

    /**
     * 调度器的版本号，用于版本控制
     */
    val version: Int get() = data.high

    override fun toString(): String = "Schedule(id = $id, version = $version)"

    companion object {
        operator fun invoke(id: Int, version: Int = 0): Schedule = Schedule(Long.Companion.fromLowHigh(id, version))
    }
}