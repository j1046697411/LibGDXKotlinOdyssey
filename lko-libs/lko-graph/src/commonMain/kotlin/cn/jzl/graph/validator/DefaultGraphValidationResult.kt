package cn.jzl.graph.validator

import cn.jzl.graph.GraphConnection
import cn.jzl.graph.GraphNode

data class DefaultGraphValidationResult(
    override val errorNodes: MutableSet<GraphNode> = hashSetOf(),
    override val warningNodes: MutableSet<GraphNode> = hashSetOf(),
    override val errorConnections: MutableSet<GraphConnection> = hashSetOf(),
    override val warningConnections: MutableSet<GraphConnection> = hashSetOf(),
    override val errorConnectors: MutableSet<NodeConnector> = hashSetOf(),
    override val warningConnectors: MutableSet<NodeConnector> = hashSetOf()
) : GraphValidationResult