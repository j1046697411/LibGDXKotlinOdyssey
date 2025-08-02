package cn.jzl.graph.render

import cn.jzl.graph.common.producer.general.GeneralPipelineNode
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.render.node.EndRenderingPipelineNode
import com.badlogic.gdx.utils.Disposable

internal class GraphPreparedRenderingPipeline(
    private val pipelineNodes: Sequence<GeneralPipelineNode>
) : PreparedRenderingPipeline {

    private val endRenderingPipelineNode by lazy { pipelineNodes.filterIsInstance<EndRenderingPipelineNode>().last() }

    override fun begin() {
        pipelineNodes.filterIsInstance<RenderingPipelineNode>().forEach { it.begin() }
    }

    override fun execute(blackboard: PipelineBlackboard): RenderingPipeline {
        pipelineNodes.forEach { it.executeNode(blackboard) }
        return endRenderingPipelineNode.getRenderingPipeline(blackboard)
    }

    override fun end() {
        pipelineNodes.filterIsInstance<RenderingPipelineNode>().forEach { it.end() }
    }

    override fun dispose() {
        pipelineNodes.filterIsInstance<Disposable>().forEach { it.dispose() }
    }
}