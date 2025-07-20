package cn.jzl.graph.common

import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.common.field.FieldType

data class PipelineNodeOutput(
    val output: GraphNodeOutput,
    val outputType: FieldType<Any>
)