package cn.jzl.ui.node

import cn.jzl.ui.node.coordinator.NodeCoordinator

abstract class ModifierNode : DelegatableNode {

    override val node: ModifierNode get() = this

    internal var coordinator: NodeCoordinator? = null
        private set

    fun updateCoordinator(coordinator: NodeCoordinator) {
        this.coordinator = coordinator
    }
}