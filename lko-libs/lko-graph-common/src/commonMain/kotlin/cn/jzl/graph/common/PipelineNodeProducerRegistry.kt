package cn.jzl.graph.common

import kotlin.reflect.KClass

interface PipelineNodeProducerRegistry {
    fun <PN : PipelineNode, GT : GraphType<in PN>> register(type: KClass<GT>, producer: PipelineNodeProducer<PN, GT>)
}
