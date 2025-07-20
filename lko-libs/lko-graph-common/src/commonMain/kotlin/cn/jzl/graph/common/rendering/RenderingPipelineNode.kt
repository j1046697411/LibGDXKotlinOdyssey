package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.PipelineNode

fun interface RenderingPipelineNode : PipelineNode {

    fun beforeFrame(): Unit = Unit

    fun executeNode(blackboard: PipelineBlackboard)

    fun afterFrame(): Unit = Unit
}

