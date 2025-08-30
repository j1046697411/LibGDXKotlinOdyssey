package cn.jzl.ui.ecs

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity

internal class Hierarchy(val parent: Entity) : Component<Hierarchy> {

    override val type: ComponentType<Hierarchy> get() = Hierarchy

    companion object : ComponentType<Hierarchy>()
}