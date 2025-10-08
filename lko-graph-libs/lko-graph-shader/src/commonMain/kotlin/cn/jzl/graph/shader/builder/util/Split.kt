package cn.jzl.graph.shader.builder.util

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType

object Split : AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("split", "split", "util/split") {

    private val input = createNodeInput("input", "Input")
    private val x = createNodeInput("x", "X")
    private val y = createNodeInput("y", "Y")
    private val z = createNodeInput("z", "Z")
    private val w = createNodeInput("w", "W")

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GraphShaderPipelineNode {
        val inputInput = inputs.firstOrNull { it.input == input }
        checkNotNull(inputInput) { "Input input is required" }
        val xOutput = outputs[x.fieldId]
        val yOutput = outputs[y.fieldId]
        val zOutput = outputs[z.fieldId]
        val wOutput = outputs[w.fieldId]
        return GraphShaderPipelineNode { blackboard: PipelineBlackboard, fragmentShader: Boolean ->
            if (fragmentShader) {
                fragmentShader { split(graphType, graphNode, blackboard, inputInput, xOutput, yOutput, zOutput, wOutput) }
            } else {
                vertexShader { split(graphType, graphNode, blackboard, inputInput, xOutput, yOutput, zOutput, wOutput) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun ProgramScope.ShaderScope.split(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        inputInput: PipelineNodeInput,
        xOutput: PipelineNodeOutput?,
        yOutput: PipelineNodeOutput?,
        zOutput: PipelineNodeOutput?,
        wOutput: PipelineNodeOutput?,
    ) {
        val input: Operand<VarType.Vector<*, *, *, *>> = graphType.operand(blackboard, inputInput)
        if (xOutput != null && input.type is VarType.Vector2<*, *, *, *>) {
            val vector2 = input as Operand<VarType.Vector2<VarType, *, *, *>>
            graphType.output(graphNode, blackboard, xOutput, vector2.x)
        }
        if (yOutput != null && input.type is VarType.Vector2<*, *, *, *>) {
            val vector2 = input as Operand<VarType.Vector2<VarType, *, *, *>>
            graphType.output(graphNode, blackboard, yOutput, vector2.y)
        }
        if (zOutput != null && input.type is VarType.Vector3<*, *, *, *>) {
            val vector3 = input as Operand<VarType.Vector3<VarType, *, *, *>>
            graphType.output(graphNode, blackboard, zOutput, vector3.z)
        }
        if (wOutput != null && input.type is VarType.Vector4<*, *, *, *>) {
            val vector4 = input as Operand<VarType.Vector4<VarType, *, *, *>>
            graphType.output(graphNode, blackboard, wOutput, vector4.w)
        }
    }
}