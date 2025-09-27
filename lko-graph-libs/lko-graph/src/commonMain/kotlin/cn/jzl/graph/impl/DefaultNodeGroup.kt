package cn.jzl.graph.impl

import cn.jzl.graph.NodeGroup

data class DefaultNodeGroup(
    override val name: String,
    private val nodeIds: Set<String>
) : NodeGroup {
    override fun iterator(): Iterator<String> = nodeIds.iterator()
}