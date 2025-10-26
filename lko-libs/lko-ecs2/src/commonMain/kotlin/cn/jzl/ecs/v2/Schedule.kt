package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.extract16
import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.insert16
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

@JvmInline
value class Schedule(private val data: Long) {
    val id: Int get() = data.low
    val version: Int get() = data.high.extract16(0)
    val priority: Int get() = data.high.extract16(16)

    companion object {
        operator fun invoke(id: Int, version: Int, priority: Int) = Schedule(
            Long.Companion.fromLowHigh(
                id,
                version.insert16(priority, 16)
            )
        )
    }
}