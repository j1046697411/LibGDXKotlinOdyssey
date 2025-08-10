package cn.jzl.graph.shader.builder.texture

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.render.field.TextureType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector4Type
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.*
import com.badlogic.gdx.graphics.Texture

class Sampler2D : AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Sampler2D", "Sampler2D", "Texture/Sampler2D") {
    private val texture = createNodeInput(
        "texture",
        "texture",
        required = true,
        fieldTypes = arrayOf(TextureType)
    )
    private val uv = createNodeInput(
        "uv",
        "uv",
        required = true,
        fieldTypes = arrayOf(Vector2Type)
    )

    private val color = createNodeOutput(
        "color",
        "color",
        required = true,
        producedTypes = arrayOf(Vector4Type)
    )

    private val r = createNodeOutput(
        "r",
        "r",
        producedTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    private val g = createNodeOutput(
        "g",
        "g",
        producedTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    private val b = createNodeOutput(
        "b",
        "b",
        producedTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    private val a = createNodeOutput(
        "a",
        "a",
        producedTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GraphShaderPipelineNode {
        val textureInput = inputs.firstOrNull { it.input == texture }
        val uvInput = inputs.firstOrNull { it.input == uv }
        checkNotNull(textureInput) { "Texture input is null" }
        checkNotNull(uvInput) { "UV input is null" }
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            check(fragmentShader) { "Sampler2D only support in fragment shader" }
            val textureFieldOutput = textureInput.let { blackboard[it.fromGraphNode, it.fromOutput, TextureShaderFieldType] }
            val uvFieldOutput = uvInput.let { blackboard[it.fromGraphNode, it.fromOutput, Vector2ShaderFieldType] }
            val u = getUVCalculation(textureFieldOutput.uWrap, "${uvFieldOutput.representation}.x")
            val v = getUVCalculation(textureFieldOutput.vWrap, "${uvFieldOutput.representation}.y")
            val uvRepresentation = "vec2($u, $v)"
            val colorName = "color_${graphNode.id}"
            fragmentShaderBuilder.addMainLine("// Sampler2D Node")
            val uv = "${textureFieldOutput.representation}.xy + ($uvRepresentation * ${textureFieldOutput.representation}.zw)"
            fragmentShaderBuilder.addMainLine("vec4 $colorName = texture(${textureFieldOutput.samplerRepresentation}, $uv);")
            blackboard[graphNode, color, Vector4ShaderFieldType] = DefaultFieldOutput(Vector4ShaderFieldType, colorName)
            blackboard[graphNode, r, FloatShaderFieldType] = DefaultFieldOutput(FloatShaderFieldType, "$colorName.r")
            blackboard[graphNode, g, FloatShaderFieldType] = DefaultFieldOutput(FloatShaderFieldType, "$colorName.g")
            blackboard[graphNode, b, FloatShaderFieldType] = DefaultFieldOutput(FloatShaderFieldType, "$colorName.b")
            blackboard[graphNode, a, FloatShaderFieldType] = DefaultFieldOutput(FloatShaderFieldType, "$colorName.a")
        }
    }

    private fun getUVCalculation(textureWrap: Texture.TextureWrap, representation: String): String {
        return when (textureWrap) {
            Texture.TextureWrap.ClampToEdge -> "clamp(${representation}, 0.0, 1.0)"
            Texture.TextureWrap.MirroredRepeat -> "1.0 - abs(mod(${representation}, 2.0) - 1.0)"
            Texture.TextureWrap.Repeat -> "fract(${representation})"
        }
    }
}