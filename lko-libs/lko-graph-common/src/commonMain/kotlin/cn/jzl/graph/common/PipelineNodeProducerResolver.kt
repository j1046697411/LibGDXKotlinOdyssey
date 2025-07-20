package cn.jzl.graph.common

import cn.jzl.graph.GraphNode

fun interface PipelineNodeProducerResolver<PN : PipelineNode> {
    fun resolve(graphNode: GraphNode): PipelineNodeProducer<PN>
}