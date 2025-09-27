package cn.jzl.graph.common

interface PipelineNodeProducerContainer {
    fun <PN : PipelineNode, GT : GraphType<PN>> getProducers(graphType: GT): Sequence<PipelineNodeProducer<PN, GT>>
}