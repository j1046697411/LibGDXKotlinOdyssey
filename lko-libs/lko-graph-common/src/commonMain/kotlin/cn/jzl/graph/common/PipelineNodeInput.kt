package cn.jzl.graph.common

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeInput
import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.common.field.FieldType

data class PipelineNodeInput(
    val fromGraphNode: GraphNode,
    val fromOutput: GraphNodeOutput,
    val input: GraphNodeInput,
    val outputType: FieldType<*>
)

