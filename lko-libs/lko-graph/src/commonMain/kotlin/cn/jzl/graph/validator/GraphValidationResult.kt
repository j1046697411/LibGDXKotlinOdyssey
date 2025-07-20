package cn.jzl.graph.validator

import cn.jzl.graph.GraphConnection
import cn.jzl.graph.GraphNode

interface GraphValidationResult {
    val errorNodes: Set<GraphNode>
    val warningNodes: Set<GraphNode>
    val errorConnections: Set<GraphConnection>
    val warningConnections: Set<GraphConnection>
    val errorConnectors: Set<NodeConnector>
    val warningConnectors: Set<NodeConnector>
}

