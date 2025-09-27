package cn.jzl.graph.shader.builder.texture

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.field.GraphShaderPipelineNode
import cn.jzl.graph.shader.field.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.textureGrad

object TextureGrad : SingleOutputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("TextureGrad", "textureGrad", "Texture/TextureGrad") {
    private val first = createNodeInput("texture", "Texture", required = true)
    private val second = createNodeInput("uv", "UV", required = true)
    private val third = createNodeInput("dPdx", "dPdx", required = true)
    private val fourth = createNodeInput("dPdy", "dPdy", required = true)

    override val output = createNodeOutput("grad", "Grad")

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        val first = inputs.getOrNull(0)
        val second = inputs.getOrNull(1)
        val third = inputs.getOrNull(2)
        val fourth = inputs.getOrNull(3)
        check(this.first.required && first != null) { "Texture input is required" }
        check(this.second.required && second != null) { "UV input is required" }
        check(this.third.required && third != null) { "dPdx input is required" }
        check(this.fourth.required && fourth != null) { "dPdy input is required" }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { textureGrad(graphType, graphNode, blackboard, first, second, third, fourth, output) }
            } else {
                vertexShader { textureGrad(graphType, graphNode, blackboard, first, second, third, fourth, output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.textureGrad(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        texture: PipelineNodeInput,
        uv: PipelineNodeInput,
        dPdx: PipelineNodeInput,
        dPdy: PipelineNodeInput,
        output: PipelineNodeOutput
    ) {
        val grad = textureGrad(
            graphType.operand(blackboard, texture),
            graphType.operand(blackboard, uv),
            graphType.operand(blackboard, dPdx),
            graphType.operand(blackboard, dPdy)
        )
        val gradNode by grad.property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, gradNode)
    }

}