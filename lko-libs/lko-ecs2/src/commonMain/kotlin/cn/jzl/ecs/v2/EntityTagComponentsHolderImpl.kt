package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

internal class EntityTagComponentsHolderImpl(override val componentType: EntityTag) : ComponentsHolder<Boolean> {
    private val entityTagBits = BitSet.Companion()
    override fun contains(entity: Entity): Boolean = entity.id in entityTagBits
    override fun get(entity: Entity): Boolean = entity.id in entityTagBits
    override fun set(entity: Entity, component: Boolean): Boolean? {
        val old = entity.id in entityTagBits
        entityTagBits[entity.id] = component
        return old
    }

    override fun getOrNull(entity: Entity): Boolean? = entity.id in entityTagBits
    override fun remove(entity: Entity): Boolean? {
        val oldEntityTag = entity.id in entityTagBits
        entityTagBits.clear(entity.id)
        return oldEntityTag
    }
}