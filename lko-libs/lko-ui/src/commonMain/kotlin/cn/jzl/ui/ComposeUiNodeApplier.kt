package cn.jzl.ui

import androidx.compose.runtime.AbstractApplier
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ui.ecs.HierarchySystem
import ktx.log.logger

class ComposeUiNodeApplier(world: World, root: Entity) : AbstractApplier<Entity>(root) {

    private val hierarchySystem by world.instance<HierarchySystem>()

    override fun insertBottomUp(index: Int, instance: Entity) {
        hierarchySystem.insert(current, index, instance)
        log.debug { "$current insertBottomUp: $index, $instance" }
    }

    override fun insertTopDown(index: Int, instance: Entity) {
    }

    override fun move(from: Int, to: Int, count: Int) {
        hierarchySystem.move(current, from, to, count)
        log.debug { "$current move: $from, $to, $count" }
    }

    override fun remove(index: Int, count: Int) {
        hierarchySystem.remove(current, index, count)
        log.debug { "$current remove: $index, $count" }
    }

    override fun onClear() {
        log.debug { "$current onClear" }
        hierarchySystem.clear(root)
    }

    companion object {
        private val log = logger<ComposeUiNodeApplier>()
    }
}