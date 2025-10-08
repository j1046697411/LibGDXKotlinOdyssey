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
import cn.jzl.shader.ProgramScope

object Merge : AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("merge", "merge", "util/merge") {
    private val x = createNodeInput("x", "X")
    private val y = createNodeInput("y", "Y")
    private val z = createNodeInput("z", "Z")
    private val w = createNodeInput("w", "W")

    private val v2 = createNodeOutput("v2", "V2")
    private val v3 = createNodeOutput("v3", "V3")
    private val color = createNodeOutput("color", "Color")


    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GraphShaderPipelineNode {
        val xInput = inputs.firstOrNull { it.input == x }
        val yInput = inputs.firstOrNull { it.input == y }
        val zInput = inputs.firstOrNull { it.input == z }
        val wInput = inputs.firstOrNull { it.input == w }

        val v2Output = outputs[v2.fieldId]
        val v3Output = outputs[v3.fieldId]
        val colorOutput = outputs[color.fieldId]
        return GraphShaderPipelineNode { blackboard: PipelineBlackboard, fragmentShader: Boolean ->
            if (fragmentShader) {
                fragmentShader { merge(graphType, graphNode, blackboard, xInput, yInput, zInput, wInput, v2Output, v3Output, colorOutput) }
            } else {
                vertexShader { merge(graphType, graphNode, blackboard, xInput, yInput, zInput, wInput, v2Output, v3Output, colorOutput) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.merge(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        xInput: PipelineNodeInput?,
        yInput: PipelineNodeInput?,
        zInput: PipelineNodeInput?,
        wInput: PipelineNodeInput?,
        v2Output: PipelineNodeOutput?,
        v3Output: PipelineNodeOutput?,
        colorOutput: PipelineNodeOutput?,
    ) {
        val x = xInput?.let { graphType.operand(blackboard, it) } ?: 0f.lit
        val y = yInput?.let { graphType.operand(blackboard, it) } ?: 0f.lit
        val z = zInput?.let { graphType.operand(blackboard, it) } ?: 0f.lit
        val w = wInput?.let { graphType.operand(blackboard, it) } ?: 0f.lit
        if (v2Output != null) {
            graphType.output(graphNode, blackboard, v2Output, vec2(x, y))
        }
        if (v3Output != null) {
            graphType.output(graphNode, blackboard, v3Output, vec3(x, y, z))
        }
        if (colorOutput != null) {
            graphType.output(graphNode, blackboard, colorOutput, vec4(x, y, z, w))
        }
    }
}