package cn.jzl.graph.shader.builder.property

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.FieldOutput

class Property : SingleOutputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Property", "Property") {

    override val output = createNodeOutput("output", "output", PrimitiveFieldTypes.FloatFieldType)

    override fun getOutputTypes(world: World, graph: GraphWithProperties, graphNode: GraphNode, inputs: List<PipelineNodeInput>): Map<String, String> {
        val fieldType = graphNode.payloads.getValue("fieldType") as FieldType<*>
        return mapOf(output.fieldId to fieldType.fieldType)
    }

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        val graphShaderPropertyProducer by world.instance<GraphShaderPropertyProducer>()
        val shaderFieldTypeResolver = graphType.shaderFieldTypeResolver
        val shaderPropertySource = graphShaderPropertyProducer.createProperty(world, graph, graphNode)
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val shaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(output.outputType)
            val fieldOutput = shaderFieldType.addProperty(graph, graphNode, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader, shaderPropertySource)
            blackboard[graphNode, output.output, shaderFieldType] = fieldOutput
        }
    }
}