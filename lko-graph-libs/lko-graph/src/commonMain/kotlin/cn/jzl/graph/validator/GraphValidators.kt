package cn.jzl.graph.validator

fun GraphValidationResult.hasErrors(): Boolean {
    return errorNodes.isNotEmpty() || errorConnectors.isNotEmpty() || errorConnections.isNotEmpty()
}

fun GraphValidationResult.hasWarnings(): Boolean {
    return warningNodes.isNotEmpty() || warningConnectors.isNotEmpty() || warningConnections.isNotEmpty()
}