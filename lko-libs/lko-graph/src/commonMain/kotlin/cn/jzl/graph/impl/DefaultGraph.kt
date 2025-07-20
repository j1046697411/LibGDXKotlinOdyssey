package cn.jzl.graph.impl

import cn.jzl.graph.Graph
import cn.jzl.graph.GraphConnection
import cn.jzl.graph.GraphNode
import cn.jzl.graph.MutableGraph
import cn.jzl.graph.NodeGroup

open class DefaultGraph(override val type: String) : Graph, MutableGraph {

    private val graphNodes = hashMapOf<String, GraphNode>()
    private val graphConnections = arrayListOf<GraphConnection>()
    private val nodeGroups = arrayListOf<NodeGroup>()
    override val nodes: Sequence<GraphNode> = graphNodes.values.asSequence()
    override val connections: Sequence<GraphConnection> = graphConnections.asSequence()
    override val groups: Sequence<NodeGroup> = nodeGroups.asSequence()

    override fun getNodeById(nodeId: String): GraphNode? = graphNodes[nodeId]

    override fun addGraphNode(graphNode: GraphNode) {
        graphNodes.put(graphNode.id, graphNode)
    }

    override fun removeGraphNode(graphNode: GraphNode) {
        graphNodes.remove(graphNode.id, graphNode)
    }

    override fun addGraphConnection(graphConnection: GraphConnection) {
        graphConnections.add(graphConnection)
    }

    override fun removeGraphConnection(graphConnection: GraphConnection) {
        graphConnections.remove(graphConnection)
    }

    override fun addNodeGroup(nodeGroup: NodeGroup) {
        nodeGroups.add(nodeGroup)
    }

    override fun removeNodeGroup(nodeGroup: NodeGroup) {
        nodeGroups.remove(nodeGroup)
    }
}