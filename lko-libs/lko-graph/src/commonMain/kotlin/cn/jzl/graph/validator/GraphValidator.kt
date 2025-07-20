package cn.jzl.graph.validator

import cn.jzl.graph.Graph

interface GraphValidator {
    fun validateGraph(graph: Graph): GraphValidationResult

    fun validateSubGraph(graph: Graph, startNode: String): GraphValidationResult
}

