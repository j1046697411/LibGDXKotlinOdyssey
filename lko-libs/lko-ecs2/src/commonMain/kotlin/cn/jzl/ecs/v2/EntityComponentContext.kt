package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

interface EntityComponentContext {
    val world: World

    val Entity.componentBits: BitSet
    val Entity.active: Boolean

    operator fun Entity.contains(componentType: ComponentType<*>): Boolean

    operator fun <C : Any> Entity.get(componentType: ComponentType<C>): C

    fun <C : Any> Entity.getOrNull(componentType: ComponentType<C>): C?
}