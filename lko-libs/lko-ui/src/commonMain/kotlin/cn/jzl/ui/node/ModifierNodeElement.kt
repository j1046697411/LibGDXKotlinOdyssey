package cn.jzl.ui.node

import cn.jzl.ecs.ComponentType
import cn.jzl.ui.modifier.Modifier

interface ModifierNodeElement<N : ModifierNode> : Modifier.Element {

    val nodeType: ComponentType<N>

    fun create(): N

    fun update(node: N)
}