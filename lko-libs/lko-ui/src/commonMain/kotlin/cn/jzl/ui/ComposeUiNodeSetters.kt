package cn.jzl.ui

import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ecs.configure
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.DensityComponent
import cn.jzl.ui.node.MeasurePolicyComponent
import cn.jzl.ui.node.ModifierComponent
import cn.jzl.ui.node.ModifierUpdateTag
import cn.jzl.ui.unit.Density

internal class ComposeUiNodeSetters(private val world: World) {

    val modifierSetter = { entity: Entity, value: Modifier ->
        world.configure(entity) {
            it[ModifierComponent].modifier = value
            it += ModifierUpdateTag
        }
    }
    val measurePolicySetter = { entity: Entity, value: MeasurePolicy ->
        world.configure(entity) { it[MeasurePolicyComponent].measurePolicy = value }
    }
    val densitySetter = { entity: Entity, value: Density ->
        world.configure(entity) { it[DensityComponent].density = value }
    }
}