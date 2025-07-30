package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.producer.general.GeneralPipelineNode

fun interface RenderingPipelineNode : GeneralPipelineNode {

    fun beforeFrame(): Unit = Unit

    override fun executeNode(blackboard: PipelineBlackboard)

    fun afterFrame(): Unit = Unit
}

