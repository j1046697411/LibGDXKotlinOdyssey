package cn.jzl.graph

interface MutableGraph : Graph {

    override val nodes: Sequence<GraphNode>
    override val connections: Sequence<GraphConnection>
    override val groups: Sequence<NodeGroup>

    override fun getNodeById(nodeId: String): GraphNode?

    fun addGraphNode(graphNode: GraphNode)
    fun removeGraphNode(graphNode: GraphNode)

    fun addGraphConnection(graphConnection: GraphConnection)
    fun removeGraphConnection(graphConnection: GraphConnection)

    fun addNodeGroup(nodeGroup: NodeGroup)
    fun removeNodeGroup(nodeGroup: NodeGroup)
}