package cn.jzl.graph.impl

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeOutputSide
import cn.jzl.graph.NodeConfiguration

data class DefaultGraphNodeOutput(
    override val fieldId: String,
    override val fieldName: String,
    override val connectableFieldTypes: List<String>,
    override val acceptingMultiple: Boolean = false,
    override val side: GraphNodeOutputSide = GraphNodeOutputSide.Right,
    override val required: Boolean = false,
    private val outputTypeFunction: OutputTypeFunction
) : NamedGraphNodeOutput {
    override fun determineFieldType(
        configuration: NodeConfiguration,
        graphNode: GraphNode,
        inputs: Map<String, List<String>>
    ): String = outputTypeFunction.evaluate(configuration, graphNode,this, inputs)
}