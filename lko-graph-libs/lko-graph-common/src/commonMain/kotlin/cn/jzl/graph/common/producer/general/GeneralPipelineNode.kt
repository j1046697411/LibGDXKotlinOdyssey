package cn.jzl.graph.common.producer.general

import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.rendering.PipelineBlackboard

fun interface GeneralPipelineNode : PipelineNode {
    fun executeNode(blackboard: PipelineBlackboard)
}