package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet
import cn.jzl.di.DIProvider

internal class EntityUpdateContextImpl(override val world: World) : EntityUpdateContext {

    override val Entity.componentBits: BitSet get() = world.componentService.componentBits(this)
    override val Entity.active: Boolean get() = this in world.entityService

    override fun Entity.minusAssign(componentType: ComponentWriteAccesses<*>) {
        componentBits.clear(componentType.type.index)
        world.componentService.holderOrNull<Any>(componentType.type.index)?.remove(this)
    }

    override fun <C : Any> Entity.set(componentType: ComponentWriteAccesses<C>, component: C): C? {
        componentBits.set(componentType.type.index)
        return world.componentService.holder(componentType.type).set(this, component)
    }

    override fun Entity.plusAssign(tag: ComponentWriteAccesses<Boolean>) {
        componentBits.set(tag.type.index)
        world.componentService.holder(tag.type)[this] = true
    }

    override fun <C : Any> Entity.getOrPut(componentType: ComponentWriteAccesses<C>, provider: DIProvider<C>): C {
        val holder = world.componentService.holder(componentType.type)
        return holder.getOrNull(this) ?: run {
            val component = provider()
            holder[this] = component
            componentBits.set(componentType.type.index)
            component
        }
    }

    override fun Entity.contains(componentType: ComponentReadAccesses<*>): Boolean {
        return componentType.type.index in componentBits
    }

    override fun <C : Any> Entity.get(componentType: ComponentReadAccesses<C>): C {
        return world.componentService.holder(componentType.type)[this]
    }

    override fun <C : Any> Entity.getOrNull(componentType: ComponentReadAccesses<C>): C? {
        return world.componentService.holderOrNull<C>(componentType.type.index)?.getOrNull(this)
    }
}