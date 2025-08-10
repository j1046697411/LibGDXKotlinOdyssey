package cn.jzl.graph.shader.builder.util

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector3Type
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode

class Merge : AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Merge", "Merge", "Util/Merge") {
    private val x = createNodeInput("x", "X")
    private val y = createNodeInput("y", "Y")
    private val z = createNodeInput("z", "Z")
    private val w = createNodeInput("w", "W")

    private val v2 = createNodeOutput("v2", "Vector2", producedTypes = arrayOf(Vector2Type))
    private val v3 = createNodeOutput("v3", "Vector3", producedTypes = arrayOf(Vector3Type))
    private val color = createNodeOutput("color", "Color", producedTypes = arrayOf(ColorType))
    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GraphShaderPipelineNode {
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        val x = inputs.firstOrNull { it.input == x }
        val y = inputs.firstOrNull { it.input == y }
        val z = inputs.firstOrNull { it.input == z }
        val w = inputs.firstOrNull { it.input == w }
        val v2 = outputs[v2.fieldId]
        val v3 = outputs[v3.fieldId]
        val color = outputs[color.fieldId]
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val xFieldOutput = x?.let { shaderFieldTypeResolver.resolve<FieldOutput>(x.outputType) }
                ?.let { blackboard[x.fromGraphNode, x.fromOutput, it] }
            val yFieldOutput = y?.let { shaderFieldTypeResolver.resolve<FieldOutput>(y.outputType) }
                ?.let { blackboard[y.fromGraphNode, y.fromOutput, it] }
            val zFieldOutput = z?.let { shaderFieldTypeResolver.resolve<FieldOutput>(z.outputType) }
                ?.let { blackboard[z.fromGraphNode, z.fromOutput, it] }
            val wFieldOutput = w?.let { shaderFieldTypeResolver.resolve<FieldOutput>(w.outputType) }
                ?.let { blackboard[w.fromGraphNode, w.fromOutput, it] }
            val xValue = xFieldOutput?.representation ?: "1.0"
            val yValue = yFieldOutput?.representation ?: "1.0"
            val zValue = zFieldOutput?.representation ?: "1.0"
            val wValue = wFieldOutput?.representation ?: "1.0"
            if (v2 != null) {
                val v2Value = "vec2($xValue, $yValue)"
                val v2Type = shaderFieldTypeResolver.resolve<FieldOutput>(v2.outputType)
                blackboard[graphNode, v2.output, v2Type] = createFieldOutput(v2Type, v2Value)
            }
            if (v3 != null) {
                val v3Value = "vec3($xValue, $yValue, $zValue)"
                val v3Type = shaderFieldTypeResolver.resolve<FieldOutput>(v3.outputType)
                blackboard[graphNode, v3.output, v3Type] = createFieldOutput(v3Type, v3Value)
            }
            if (color != null) {
                val colorValue = "vec4($xValue, $yValue, $zValue, $wValue)"
                val colorType = shaderFieldTypeResolver.resolve<FieldOutput>(color.outputType)
                blackboard[graphNode, color.output, colorType] = createFieldOutput(colorType, colorValue)
            }
        }
    }

    private fun createFieldOutput(
        shaderFieldType: ShaderFieldType<out FieldOutput>,
        representation: String
    ): FieldOutput = DefaultFieldOutput(shaderFieldType, representation)
}

