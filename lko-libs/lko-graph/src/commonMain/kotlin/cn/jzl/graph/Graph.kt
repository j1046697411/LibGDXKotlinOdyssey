package cn.jzl.graph

interface Graph {
    val type: String
    val nodes: Sequence<GraphNode>
    val connections: Sequence<GraphConnection>
    val groups: Sequence<NodeGroup>

    fun getNodeById(nodeId: String): GraphNode?
}

