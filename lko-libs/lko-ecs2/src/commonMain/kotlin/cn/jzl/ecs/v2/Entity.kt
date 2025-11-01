package cn.jzl.ecs.v2

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

@JvmInline
value class Entity private constructor(@PublishedApi internal val data: Long) {
    val id: Int get() = data.low
    val version: Int get() = data.high

    override fun toString(): String = "Entity(id=$id, version=$version)"

    companion object {
        val NONE = Entity(-1)
        operator fun invoke(id: Int, version: Int = 0) = Entity(Long.Companion.fromLowHigh(id, version))
    }
}