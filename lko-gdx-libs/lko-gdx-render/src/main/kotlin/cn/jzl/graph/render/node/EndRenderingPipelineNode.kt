package cn.jzl.graph.render.node

import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.render.RenderingPipeline

interface EndRenderingPipelineNode : RenderingPipelineNode {
    fun getRenderingPipeline(blackboard: PipelineBlackboard): RenderingPipeline
}