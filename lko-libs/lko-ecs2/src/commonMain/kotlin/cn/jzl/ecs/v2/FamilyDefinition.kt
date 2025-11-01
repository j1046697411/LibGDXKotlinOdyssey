package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

class FamilyDefinition {

    internal val allBits = BitSet()
    internal val anyBits = BitSet()
    internal val noneBits = BitSet()

    fun all(vararg componentTypes: ComponentType<*>): FamilyDefinition = allBits.setComponentTypes(componentTypes)
    fun any(vararg componentTypes: ComponentType<*>): FamilyDefinition = anyBits.setComponentTypes(componentTypes)
    fun none(vararg componentTypes: ComponentType<*>): FamilyDefinition = noneBits.setComponentTypes(componentTypes)

    fun BitSet.setComponentTypes(componentTypes: Array<out ComponentType<*>>): FamilyDefinition {
        if (componentTypes.isEmpty()) return this@FamilyDefinition
        componentTypes.forEach { set(it.index) }
        return this@FamilyDefinition
    }

    internal fun Family.checkEntity(entity: Entity): Boolean {
        val componentBits = entity.componentBits
        if (allBits.isNotEmpty() && allBits !in componentBits) return false
        if (anyBits.isNotEmpty() && !anyBits.intersects(componentBits)) return false
        if (noneBits.isNotEmpty() && noneBits in componentBits) return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FamilyDefinition

        if (allBits != other.allBits) return false
        if (anyBits != other.anyBits) return false
        if (noneBits != other.noneBits) return false

        return true
    }

    override fun hashCode(): Int {
        var result = allBits.hashCode()
        result = 31 * result + anyBits.hashCode()
        result = 31 * result + noneBits.hashCode()
        return result
    }
}