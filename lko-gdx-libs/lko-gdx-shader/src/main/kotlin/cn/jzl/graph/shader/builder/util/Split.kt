package cn.jzl.graph.shader.builder.util

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector3Type
import cn.jzl.graph.render.field.Vector4Type
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode

class Split : AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Split", "Split") {
    private val input = createNodeInput("input", "input", required = true)
    private val x = createNodeOutput("x", "X")
    private val y = createNodeOutput("y", "Y")
    private val z = createNodeOutput("z", "Z")
    private val w = createNodeOutput("w", "W")
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
        val input = inputs.firstOrNull { it.input == input }
        check(input != null) { "input is required" }
        val x = outputs[x.fieldId]
        val y = outputs[y.fieldId]
        val z = outputs[z.fieldId]
        val w = outputs[w.fieldId]
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val inputFieldOutput = input.let { shaderFieldTypeResolver.resolve<FieldOutput>(input.outputType) }
                .let { blackboard[input.fromGraphNode, input.fromOutput, it] }
            val dimensions = when (input.outputType) {
                PrimitiveFieldTypes.FloatFieldType -> 1
                Vector2Type -> 2
                Vector3Type -> 3
                ColorType, Vector4Type -> 4
                else -> throw IllegalArgumentException("input.outputType must be FloatType Vector2Type, Vector3Type, Vector4Type or ColorType")
            }

            if (x != null) {
                val xValue = "${inputFieldOutput.representation}.x"
                val xType = shaderFieldTypeResolver.resolve<FieldOutput>(x.outputType)
                blackboard[graphNode, x.output, xType] = createFieldOutput(xType, xValue)
            }
            if (y != null) {
                val yValue = if (dimensions >= 2) "${inputFieldOutput.representation}.y" else "0.0"
                val yType = shaderFieldTypeResolver.resolve<FieldOutput>(y.outputType)
                blackboard[graphNode, y.output, yType] = createFieldOutput(yType, yValue)
            }
            if (z != null) {
                val zValue = if (dimensions >= 3) "${inputFieldOutput.representation}.z" else "0.0"
                val zType = shaderFieldTypeResolver.resolve<FieldOutput>(z.outputType)
                blackboard[graphNode, z.output, zType] = createFieldOutput(zType, zValue)
            }
            if (w != null) {
                val wValue = if (dimensions >= 4) "${inputFieldOutput.representation}.w" else "0.0"
                val wType = shaderFieldTypeResolver.resolve<FieldOutput>(w.outputType)
                blackboard[graphNode, w.output, wType] = createFieldOutput(wType, wValue)
            }
        }
    }

    private fun createFieldOutput(
        shaderFieldType: ShaderFieldType<out FieldOutput>,
        representation: String
    ): FieldOutput = DefaultFieldOutput(shaderFieldType, representation)
}