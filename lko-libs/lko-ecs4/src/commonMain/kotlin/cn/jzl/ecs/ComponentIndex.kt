package cn.jzl.ecs

import cn.jzl.datastructure.math.fromLowHigh
import cn.jzl.datastructure.math.high
import cn.jzl.datastructure.math.low
import kotlin.jvm.JvmInline

@JvmInline
value class ComponentIndex private constructor(val data: Long) {

    val prefabEntity: Entity get() = Entity(data.low)
    val index: Int get() = data.high

    companion object {
        operator fun invoke(
            prefabEntity: Entity, index: Int
        ): ComponentIndex = ComponentIndex(Long.Companion.fromLowHigh(prefabEntity.data, index))
    }
}