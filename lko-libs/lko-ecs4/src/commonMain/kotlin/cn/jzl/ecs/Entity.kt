package cn.jzl.ecs

import cn.jzl.datastructure.math.extract08
import cn.jzl.datastructure.math.extract24
import cn.jzl.datastructure.math.insert08
import kotlin.jvm.JvmInline

@JvmInline
value class Entity @PublishedApi internal constructor(val data: Int) {
    val id: Int get() = data.extract24(0)
    val version: Int get() = data.extract08(24)

    override fun toString(): String = "EntityId($id, $version)"

    companion object {
        val ENTITY_INVALID = Entity(-1)

        operator fun invoke(id: Int, version: Int): Entity = Entity(id.insert08(version, 24))
    }
}