package cn.jzl.ui.node

import androidx.compose.runtime.AbstractApplier
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ecs.configure
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.unit.Density


class ComposeUiNodeSetters(private val world: World) {
    val modifierSetter: Entity.(Modifier) -> Unit = { modifier ->
        world.configure(this) {
            it[ComposeUiModifierComponent].modifier = modifier
            it += ComposeUiNodeSystem.ModifierUpdateTag
        }
    }

    val measurePolicySetter: Entity.(MeasurePolicy) -> Unit = { measurePolicy ->
        world.configure(this) {
            it[ComposeUiMeasurePolicyComponent].measurePolicy = measurePolicy
        }
    }

    val densitySetter: Entity.(Density) -> Unit = { density ->
        world.configure(this) {
            it[ComposeUiDensityComponent].density = density
            it += ComposeUiNodeSystem.DensityUpdateTag
        }
    }
}

internal class ComposeUiNodeApplier(
    world: World,
    root: Entity
) : AbstractApplier<Entity>(root) {

    private val hierarchySystem by world.instance<HierarchySystem>()

    override fun insertBottomUp(index: Int, instance: Entity) {
        hierarchySystem.insert(current, index, instance)
    }

    override fun insertTopDown(index: Int, instance: Entity) {
    }

    override fun move(from: Int, to: Int, count: Int) {
        hierarchySystem.move(current, from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        hierarchySystem.remove(current, index, count)
    }

    override fun onClear() {
    }
}
