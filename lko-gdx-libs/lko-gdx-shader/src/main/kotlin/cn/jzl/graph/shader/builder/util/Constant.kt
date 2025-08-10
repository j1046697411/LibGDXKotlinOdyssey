package cn.jzl.graph.shader.builder.util

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4

class Constant : SingleOutputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Constant", "Constant", "Util/Constant") {

    override val output = createNodeOutput("output", "Constant")

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        val shaderFieldTypeResolver = graphType.shaderFieldTypeResolver
        val constant = graphNode.payloads[KEY_CONSTANT]
        check(constant != null) { "Constant node must have a constant value" }
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
            val shaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(output.outputType)
            val representation = when (constant) {
                is Number -> constant.toString()
                is Vector2 -> "vec2(${constant.x}, ${constant.y})"
                is Vector3 -> "vec3(${constant.x}, ${constant.y}, ${constant.z})"
                is Vector4 -> "vec4(${constant.x}, ${constant.y}, ${constant.z}, ${constant.w})"
                is Color -> "vec4(${constant.r}, ${constant.g}, ${constant.b}, ${constant.a})"
                is Matrix4 -> "mat4(${constant.values[Matrix4.M00]}, ${constant.values[Matrix4.M01]}, ${constant.values[Matrix4.M02]}, ${constant.values[Matrix4.M03]}, " +
                        "${constant.values[Matrix4.M10]}, ${constant.values[Matrix4.M11]}, ${constant.values[Matrix4.M12]}, ${constant.values[Matrix4.M13]}, " +
                        "${constant.values[Matrix4.M20]}, ${constant.values[Matrix4.M21]}, ${constant.values[Matrix4.M22]}, ${constant.values[Matrix4.M23]}, " +
                        "${constant.values[Matrix4.M30]}, ${constant.values[Matrix4.M31]}, ${constant.values[Matrix4.M32]}, ${constant.values[Matrix4.M33]})"

                else -> throw IllegalArgumentException("Constant node must have a constant value")
            }

            val name = "constant_${graphNode.id}"
            commonShaderBuilder.addMainLine("// constant node")
            commonShaderBuilder.addMainLine("${shaderFieldType.fieldType} $name = $representation;")
            val fieldOutput = DefaultFieldOutput(shaderFieldType, name)
            blackboard[graphNode, output.output, shaderFieldType] = fieldOutput
        }
    }

    companion object {
        const val KEY_CONSTANT = "constant"
    }
}