package cn.jzl.graph.common.producer.general.provided

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldTypeResolver
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.common.producer.general.GeneralGraphType
import cn.jzl.graph.common.producer.general.GeneralPipelineNode

class Property : SingleOutputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>("property", "property", "Provided/Property") {

    override val output = createNodeOutput(
        fieldId = "output",
        fieldName = "output"
    )

    override fun getOutputTypes(world: World, graph: GraphWithProperties, graphNode: GraphNode, inputs: List<PipelineNodeInput>): Map<String, String> {
        return mapOf(output.fieldId to graphNode.payloads.getValue(KEY_PROPERTY_TYPE).toString())
    }

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        val fieldTypeResolver by world.instance<FieldTypeResolver>()
        val fieldType = fieldTypeResolver.resolve(graphNode.payloads.getValue(KEY_PROPERTY_TYPE).toString())
        val propertyKey = fieldType.createPropertyKey(graphNode.payloads.getValue(KEY_PROPERTY_NAME).toString())
        return GeneralPipelineNode { blackboard ->
            val property = if (DEFAULT_VALUE in graphNode.payloads) {
                configuration.propertyContainer.getOrNull(propertyKey) ?: graphNode.payloads.getValue(DEFAULT_VALUE)
            } else {
                configuration.propertyContainer[propertyKey]
            }
            check(fieldType.accepts(property)) { "Property $propertyKey not found" }
            blackboard[graphNode, output.output, output.outputType] = property
        }
    }

    companion object {
        const val KEY_PROPERTY_TYPE = "propertyType"
        const val KEY_PROPERTY_NAME = "propertyName"
        const val DEFAULT_VALUE = "defaultValue"
    }
}