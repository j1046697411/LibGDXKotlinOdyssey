package cn.jzl.graph.render.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.render.RenderGraphType
import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.render.field.RenderingPipelineType
import cn.jzl.graph.render.node.EndRenderingPipelineNode

class EndRenderingPipelineNodeProducer :
    AbstractPipelineNodeProducer<EndRenderingPipelineNode, RenderGraphType>("end", "end", "Render/EndRendering") {

    private val input = createNodeInput("input", "RenderingPipeline", required = true)

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): EndRenderingPipelineNode {
        val renderingPipelineInput = inputs.firstOrNull { it.input == input }
        requireNotNull(renderingPipelineInput) { "RenderingPipeline is null" }
        return object : EndRenderingPipelineNode {
            override fun begin() = Unit

            override fun end()  = Unit

            override fun getRenderingPipeline(blackboard: PipelineBlackboard): RenderingPipeline {
                return blackboard[renderingPipelineInput.fromGraphNode, renderingPipelineInput.fromOutput, RenderingPipelineType]
            }

            override fun executeNode(blackboard: PipelineBlackboard) {
            }
        }
    }
}