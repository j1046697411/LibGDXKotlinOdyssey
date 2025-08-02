package cn.jzl.graph.render

import cn.jzl.graph.common.producer.general.GeneralPipelineNode
import cn.jzl.graph.common.rendering.PipelineBlackboard

fun interface RenderingPipelineNode : GeneralPipelineNode {

    fun begin(): Unit = Unit

    override fun executeNode(blackboard: PipelineBlackboard)

    fun end(): Unit = Unit
}

