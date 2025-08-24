package cn.jzl.ui.node

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ui.MeasurePolicy

internal data class MeasurePolicyComponent(var measurePolicy: MeasurePolicy) : Component<MeasurePolicyComponent> {
    override val type: ComponentType<MeasurePolicyComponent> get() = MeasurePolicyComponent

    companion object : ComponentType<MeasurePolicyComponent>()
}