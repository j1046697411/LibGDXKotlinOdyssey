package cn.jzl.graph.common.field

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.NodeConfiguration
import cn.jzl.graph.impl.OutputTypeFunction

internal object DefaultOutputTypeFunction : OutputTypeFunction {
    override fun evaluate(
        configuration: NodeConfiguration,
        graphNode: GraphNode,
        output: GraphNodeOutput,
        inputs: Map<String, List<String>>
    ): String {
        return output.connectableFieldTypes.firstOrNull() ?: PrimitiveFieldTypes.values().first().fieldType
    }
}