package cn.jzl.graph.common.producer

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeInputSide
import cn.jzl.graph.GraphNodeOutputSide
import cn.jzl.graph.NodeConfiguration
import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeProducer
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.DefaultAcceptedTypePredicate
import cn.jzl.graph.common.field.DefaultOutputTypeFunction
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.SimpleAcceptedTypePredicate
import cn.jzl.graph.impl.*

abstract class AbstractPipelineNodeProducer<PN : PipelineNode, GT : GraphType<PN>>(
    name: String,
    type: String
) : PipelineNodeProducer<PN, GT> {
    private val menuNodeConfiguration = DefaultNodeConfiguration(name, type)
    final override val configuration: NodeConfiguration get() = menuNodeConfiguration

    override fun getOutputTypes(graph: GraphWithProperties, graphNode: GraphNode, inputs: List<PipelineNodeInput>): Map<String, String> {
        return configuration.nodeOutputs.associateBy({ it.fieldId }, { output ->
            output.determineFieldType(
                configuration,
                graphNode,
                inputs.groupBy({ it.input.fieldId }, { it.outputType.fieldType })
            )
        })
    }

    protected fun createNodeOutput(
        fieldId: String,
        fieldName: String,
        connectableFieldTypes: List<FieldType<*>>,
        acceptingMultiple: Boolean = false,
        side: GraphNodeOutputSide = GraphNodeOutputSide.Right,
        required: Boolean = false,
        outputTypeFunction: OutputTypeFunction = DefaultOutputTypeFunction
    ): NamedGraphNodeOutput {
        val output = DefaultGraphNodeOutput(
            fieldId,
            fieldName,
            connectableFieldTypes.map { it.fieldType },
            acceptingMultiple,
            side,
            required,
            outputTypeFunction
        )
        menuNodeConfiguration.addNodeOutput(output)
        return output
    }

    protected fun createNodeOutput(
        fieldId: String,
        fieldName: String,
        acceptingMultiple: Boolean = false,
        side: GraphNodeOutputSide = GraphNodeOutputSide.Right,
        required: Boolean = false,
        outputTypeFunction: OutputTypeFunction = DefaultOutputTypeFunction,
        vararg producedTypes: FieldType<*>
    ): NamedGraphNodeOutput = createNodeOutput(
        fieldId,
        fieldName,
        producedTypes.toList(),
        acceptingMultiple,
        side,
        required,
        outputTypeFunction
    )

    protected fun createNodeInput(
        fieldId: String,
        fieldName: String,
        acceptingMultiple: Boolean = false,
        side: GraphNodeInputSide = GraphNodeInputSide.Left,
        required: Boolean = false,
        acceptedTypePredicate: AcceptedTypePredicate = DefaultAcceptedTypePredicate
    ): NamedGraphNodeInput {
        val input = DefaultGraphNodeInput(fieldId, fieldName, acceptingMultiple, side, required, acceptedTypePredicate)
        menuNodeConfiguration.addNodeInput(input)
        return input
    }

    protected fun createNodeInput(
        fieldId: String,
        fieldName: String,
        acceptingMultiple: Boolean = false,
        side: GraphNodeInputSide = GraphNodeInputSide.Left,
        required: Boolean = false,
        vararg fieldTypes: FieldType<*>
    ): NamedGraphNodeInput {
        return createNodeInput(fieldId, fieldName, acceptingMultiple, side, required, SimpleAcceptedTypePredicate(fieldTypes))
    }
}