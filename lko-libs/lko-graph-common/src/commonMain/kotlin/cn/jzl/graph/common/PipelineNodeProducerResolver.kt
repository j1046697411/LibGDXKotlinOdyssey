package cn.jzl.graph.common

import cn.jzl.graph.GraphNode

interface PipelineNodeProducerResolver {
    fun <PN : PipelineNode, GT : GraphType<PN>> resolve(graphType: GT, graphNode: GraphNode): PipelineNodeProducer<PN, GT>
}

