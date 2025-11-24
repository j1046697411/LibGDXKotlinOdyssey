package cn.jzl.ecs

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

@JvmInline
value class Relation @PublishedApi internal constructor(@PublishedApi internal val data: Long) {
    val kind: ComponentId get() = Entity(data.low)
    val target: Entity get() = Entity(data.high)

    operator fun compareTo(other: Relation): Int = data.compareTo(other.data)

    override fun toString(): String = "Relation(kind = $kind, target = $target)"

    companion object {
        operator fun invoke(kind: ComponentId, target: Entity): Relation {
            return Relation(Long.Companion.fromLowHigh(kind.data, target.data))
        }
    }
}