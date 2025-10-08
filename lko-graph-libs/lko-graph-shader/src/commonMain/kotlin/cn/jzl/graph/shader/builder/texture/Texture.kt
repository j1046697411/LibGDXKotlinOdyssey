package cn.jzl.graph.shader.builder.texture

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.TripleInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.texture

object Texture : TripleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Texture", "texture", "Texture/Texture") {

    override val first = createNodeInput("texture", "Texture", required = true)
    override val second = createNodeInput("uv", "UV", required = true)
    override val third = createNodeInput("bias", "Bias", required = false)
    override val output = createNodeOutput("color", "Color")

    override fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        third: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.first.required && first != null) { "Texture input is required" }
        check(this.second.required && second != null) { "UV input is required" }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { texture(graphType, graphNode, blackboard, first, second, third, output) }
            } else {
                vertexShader { texture(graphType, graphNode, blackboard, first, second, third, output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.texture(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        texture: PipelineNodeInput,
        uv: PipelineNodeInput,
        bias: PipelineNodeInput?,
        output: PipelineNodeOutput
    ) {
        val color = if (bias != null) texture(
            graphType.operand(blackboard, texture),
            graphType.operand(blackboard, uv),
            graphType.operand(blackboard, bias)
        ) else texture(
            graphType.operand(blackboard, texture),
            graphType.operand(blackboard, uv)
        )
        val colorNode by color.property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, colorNode)
    }
}