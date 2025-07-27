package cn.jzl.graph.render.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.common.rendering.RenderGraphType
import cn.jzl.graph.render.node.EndRenderingPipelineNode
import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.render.field.RenderingPipelineType

class EndRenderingPipelineNodeProducer : AbstractPipelineNodeProducer<EndRenderingPipelineNode, RenderGraphType>("end", "end") {

    private val input = createNodeInput("input", "RenderingPipeline", required = true, )

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): EndRenderingPipelineNode {
        val renderingPipelineInput = inputs.firstOrNull{ it.input == input }
        requireNotNull(renderingPipelineInput) { "RenderingPipeline is null" }
        return object : EndRenderingPipelineNode {
            override fun getRenderingPipeline(blackboard: PipelineBlackboard): RenderingPipeline {
                return blackboard[renderingPipelineInput.fromGraphNode, renderingPipelineInput.fromOutput, RenderingPipelineType]
            }

            override fun executeNode(blackboard: PipelineBlackboard) {
            }
        }
    }
}