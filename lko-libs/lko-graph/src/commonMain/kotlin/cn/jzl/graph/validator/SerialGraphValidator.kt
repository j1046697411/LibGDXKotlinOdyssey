package cn.jzl.graph.validator

import cn.jzl.graph.Graph

class SerialGraphValidator(private val graphValidators: Collection<GraphValidator>) : GraphValidator {
    override fun validateGraph(graph: Graph): GraphValidationResult {
        val graphValidationResult = SumGraphValidationResult()
        for (graphValidator in graphValidators) {
            graphValidationResult.addGraphValidationResult(graphValidator.validateGraph(graph))
            if (graphValidationResult.hasErrors()) return graphValidationResult
        }
        return graphValidationResult
    }

    override fun validateSubGraph(graph: Graph, startNode: String): GraphValidationResult {
        val graphValidationResult = SumGraphValidationResult()
        for (graphValidator in graphValidators) {
            graphValidationResult.addGraphValidationResult(graphValidator.validateSubGraph(graph, startNode))
            if (graphValidationResult.hasErrors()) return graphValidationResult
        }
        return graphValidationResult
    }

}