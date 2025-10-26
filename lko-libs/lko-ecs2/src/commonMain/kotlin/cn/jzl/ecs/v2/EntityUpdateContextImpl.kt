package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.di.DIProvider

internal class EntityUpdateContextImpl(override val world: World) : EntityUpdateContext {

    override val Entity.componentBits: BitSet get() = world.componentService.componentBits(this)
    override val Entity.active: Boolean get() = this in world.entityService

    override fun Entity.minusAssign(componentType: ComponentType<*>) {
        componentBits.clear(componentType.index)
        world.componentService.holderOrNull<Any>(componentType.index)?.remove(this)
    }

    override fun <C : Component<C>> Entity.minusAssign(component: Component<C>) {
        componentBits.clear(component.type.index)
        world.componentService.holderOrNull<C>(component.type.index)?.remove(this)
    }

    override fun <C : Any> Entity.set(componentType: ComponentType<C>, component: C): C? {
        componentBits.set(componentType.index)
        return world.componentService.holder(componentType).set(this, component)
    }

    override fun <C : Any> Entity.getOrPut(componentType: ComponentType<C>, provider: DIProvider<C>): C {
        val holder = world.componentService.holder(componentType)
        return holder.getOrNull(this) ?: run {
            val component = provider()
            holder[this] = component
            componentBits.set(componentType.index)
            component
        }
    }

    override fun Entity.contains(componentType: ComponentType<*>): Boolean {
        return componentType.index in componentBits
    }

    override fun <C : Any> Entity.get(componentType: ComponentType<C>): C {
        return world.componentService.holder(componentType)[this]
    }

    override fun <C : Any> Entity.getOrNull(componentType: ComponentType<C>): C? {
        return world.componentService.holderOrNull<C>(componentType.index)?.getOrNull(this)
    }
}