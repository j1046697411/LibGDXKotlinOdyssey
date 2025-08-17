package cn.jzl.ui.compose

import androidx.compose.runtime.AbstractApplier
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ui.ecs.HierarchySystem

class LayerNodeApplier(
    world: World,
    composeSystem: UIComposeSystem
) : AbstractApplier<Entity>(composeSystem.createRootNode()) {

    private val hierarchySystem by world.instance<HierarchySystem>()

    override fun insertTopDown(index: Int, instance: Entity) {
        hierarchySystem.insert(current, index, instance)
        println("LayerNodeApplier: insertTopDown $index, instance = ${instance.id}, current = ${current.id}")
    }

    override fun insertBottomUp(index: Int, instance: Entity) {
    }

    override fun remove(index: Int, count: Int) {
        hierarchySystem.remove(current, index, count)
        println("LayerNodeApplier: remove $index, count = $count, current = ${current.id}")
    }

    override fun move(from: Int, to: Int, count: Int) {
        hierarchySystem.move(current, from, to, count)
        println("LayerNodeApplier: move $from, $to, count = $count, current = ${current.id}")
    }

    override fun onClear() {
        println("LayerNodeApplier: onClear, current = ${current.id}")
    }
}