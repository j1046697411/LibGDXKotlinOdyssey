package cn.jzl.graph.shader.builder.property

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import com.badlogic.gdx.graphics.Texture

class Property : SingleOutputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("FloatProperty", "Float property") {

    override val output = createNodeOutput("output", "output", PrimitiveFieldTypes.FloatFieldType)

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

interface GraphShaderPropertyProducer {
    val shaderFieldType: ShaderFieldType<out FieldOutput>
    fun createProperty(world: World, graph: GraphWithProperties, graphNode: GraphNode) : ShaderPropertySource
}

interface ShaderPropertySource {
    val shaderFieldType: ShaderFieldType<out FieldOutput>
    val propertyIndex: Int
    val propertyName: String
    fun getPropertyName(index: Int): String
    val propertyLocation: PropertyLocation
    val attributeFunction: String?
}

interface TextureShaderPropertySource : ShaderPropertySource {
    val minFilter: Texture.TextureFilter
    val magFilter: Texture.TextureFilter
}

enum class PropertyLocation {
    Attribute,
    Uniform,
    GlobalUniform
}