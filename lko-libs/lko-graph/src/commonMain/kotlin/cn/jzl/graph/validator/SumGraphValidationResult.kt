package cn.jzl.graph.validator

@JvmInline
value class SumGraphValidationResult(
    private val defaultGraphValidationResult: DefaultGraphValidationResult = DefaultGraphValidationResult()
) : GraphValidationResult by defaultGraphValidationResult {

    fun addGraphValidationResult(result: GraphValidationResult) {
        defaultGraphValidationResult.errorNodes.addAll(result.errorNodes)
        defaultGraphValidationResult.warningNodes.addAll(result.warningNodes)
        defaultGraphValidationResult.errorConnections.addAll(result.errorConnections)
        defaultGraphValidationResult.warningConnectors.addAll(result.warningConnectors)
        defaultGraphValidationResult.errorConnectors.addAll(result.errorConnectors)
        defaultGraphValidationResult.warningConnectors.addAll(result.warningConnectors)
    }
}
