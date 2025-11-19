@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList

data class Table(val entityType: EntityType) : Sequence<Entity> {
    @PublishedApi
    internal val entities = Entities()

    @PublishedApi
    internal val entityIdBits = BitSet.Companion()

    @PublishedApi
    internal val componentArrays by lazy { Array(entityType.size) { ObjectFastList<Any>() } }

    val size: Int get() = entities.size

    operator fun contains(entity: Entity): Boolean = entityIdBits[entity.id]

    @PublishedApi
    internal inline fun check(entityIndex: Int, componentIndex: Int) {
        require(entityIndex in 0 until entities.size) { "entityIndex $entityIndex is out of bounds" }
        require(componentIndex in componentArrays.indices) { "componentIndex $componentIndex is out of bounds" }
    }

    inline fun insert(entity: Entity, provider: Relation.(Int) -> Any): Int {
        val size = entities.size
        entities.add(entity)
        entityIdBits.set(entity.id)
        if (entityType.isNotEmpty()) {
            entityType.forEachIndexed { index, relation ->
                componentArrays[index].insertLast(relation.provider(index))
            }
        }
        return size
    }

    inline operator fun get(entityIndex: Int): Entity = entities[entityIndex]

    inline operator fun get(entityIndex: Int, componentIndex: Int): Any {
        check(entityIndex, componentIndex)
        return componentArrays[componentIndex][entityIndex]
    }

    inline operator fun set(entityIndex: Int, componentIndex: Int, value: Any) {
        check(entityIndex, componentIndex)
        componentArrays[componentIndex][entityIndex] = value
    }

    inline fun remove(entityIndex: Int) {
        val entityId = entities.removeAt(entityIndex)
        entityIdBits.clear(entityId.id)
        if (entityType.isNotEmpty()) {
            componentArrays.forEach { it.removeAt(entityIndex) }
        }
    }

    override fun iterator(): Iterator<Entity> = entities.iterator()
}