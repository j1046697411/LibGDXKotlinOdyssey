package cn.jzl.ecs.v2

import cn.jzl.datastructure.BitSet

interface EntityComponentContext {
    val world: World

    val Entity.componentBits: BitSet
    val Entity.active: Boolean

    operator fun Entity.contains(componentType: ComponentReadAccesses<*>): Boolean

    operator fun <C : Any> Entity.get(componentType: ComponentReadAccesses<C>): C

    fun <C : Any> Entity.getOrNull(componentType: ComponentReadAccesses<C>): C?
}