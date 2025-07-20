package cn.jzl.graph.common.rendering

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeOutput
import cn.jzl.graph.common.field.FieldType

internal class DefaultPipelineBlackboard : PipelineBlackboard {

    private val blackboards = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(
        graphNode: GraphNode,
        output: GraphNodeOutput,
        fieldType: FieldType<T>
    ): T = blackboards["${graphNode.id}-${output.fieldId}"] as T

    override fun <T> set(graphNode: GraphNode, output: GraphNodeOutput, fieldType: FieldType<T>, value: T) {
        blackboards["${graphNode.id}-${output.fieldId}"] = value
    }
}