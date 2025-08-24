package cn.jzl.ui.node

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ui.unit.Density

internal data class DensityComponent(var density: Density) : Component<DensityComponent> {
    override val type: ComponentType<DensityComponent> get() = DensityComponent

    companion object : ComponentType<DensityComponent>()
}