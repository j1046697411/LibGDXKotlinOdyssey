package cn.jzl.ecs

import cn.jzl.datastructure.list.IntFastList
import kotlin.jvm.JvmInline

@JvmInline
value class Entities(private val entities: IntFastList = IntFastList()) : Sequence<Entity> {

    val size: Int get() = entities.size

    fun isNotEmpty(): Boolean = entities.isNotEmpty()

    fun add(entity: Entity): Unit = entities.insertLast(entity.data)

    fun addAll(entities: Entities): Unit = this.entities.insertLastAll(entities.entities)

    fun remove(entity: Entity): Boolean = entities.remove(entity.data)

    operator fun set(index: Int, entity: Entity) {
        entities[index] = entity.data
    }

    fun removeAt(index: Int): Entity {
        require(index in entities.indices)
        return Entity(entities.removeAt(index))
    }

    operator fun get(index: Int): Entity {
        require(index in entities.indices)
        return Entity(entities[index])
    }

    override fun iterator(): Iterator<Entity> = entities.map { Entity(it) }.iterator()
}