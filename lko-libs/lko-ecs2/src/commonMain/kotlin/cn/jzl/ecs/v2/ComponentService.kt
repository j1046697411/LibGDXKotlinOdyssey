package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.datastructure.list.ObjectFastList

class ComponentService(@PublishedApi internal val world: World) {

    private val componentBits = ObjectFastList<BitSet?>()
    private val componentsHolders = ObjectFastList<ComponentsHolder<*>?>()

    fun componentBits(entity: Entity): BitSet {
        componentBits.ensureCapacity(entity.id + 1, null)
        return componentBits[entity.id] ?: run {
            val bits = BitSet.Companion()
            componentBits[entity.id] = bits
            bits
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <C> holder(componentType: ComponentType<C>): ComponentsHolder<C> {
        return holderOrNull(componentType.index) ?: run {
            val componentsHolder = if (componentType is EntityTag) {
                EntityTagComponentsHolderImpl(componentType)
            } else {
                ComponentsHolderImpl(world, componentType)
            }
            componentsHolders.ensureCapacity(componentType.index + 1, null)
            componentsHolders[componentType.index] = componentsHolder
            componentsHolder as ComponentsHolder<C>
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <C> holderOrNull(componentIndex: Int): ComponentsHolder<C>? {
        val holder = componentsHolders.getOrNull(componentIndex) ?: return null
        check(holder.componentType.index == componentIndex) {
            "Component index $componentIndex does not match holder index ${holder.componentType.index}"
        }
        return holder as? ComponentsHolder<C>
    }
}