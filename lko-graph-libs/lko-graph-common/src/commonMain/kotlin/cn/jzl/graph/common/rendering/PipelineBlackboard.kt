package cn.jzl.graph.common.rendering

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.common.field.FieldType

interface PipelineBlackboard {
    operator fun <T> get(graphNode: GraphNode, output: GraphNodeOutput, fieldType: FieldType<T>): T
    operator fun <T> set(graphNode: GraphNode, output: GraphNodeOutput, fieldType: FieldType<T>, value: T)
}

