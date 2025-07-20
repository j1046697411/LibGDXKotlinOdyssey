package cn.jzl.graph.impl

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.NodeConfiguration

fun interface OutputTypeFunction {
    fun evaluate(
        configuration: NodeConfiguration,
        graphNode: GraphNode,
        output: GraphNodeOutput,
        inputs: Map<String, List<String>>
    ): String

    companion object : OutputTypeFunction {
        override fun evaluate(configuration: NodeConfiguration, graphNode: GraphNode, output: GraphNodeOutput, inputs: Map<String, List<String>>): String {
            return output.connectableFieldTypes.first()
        }
    }
}