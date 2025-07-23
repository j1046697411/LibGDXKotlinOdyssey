package cn.jzl.graph.common

import cn.jzl.graph.GraphNode
import kotlin.reflect.KClass

class DefaultPipelineNodeProducerRegistry : PipelineNodeProducerRegistry, PipelineNodeProducerResolver {
    private val producers = hashMapOf<KClass<*>, MutableMap<String, PipelineNodeProducer<*, *>>>()

    override fun <PN : PipelineNode, GT : GraphType<PN>> register(
        type: KClass<GT>,
        producer: PipelineNodeProducer<PN, GT>
    ) {
        val producers = this.producers.getOrPut(type) { hashMapOf() }
        producers[producer.configuration.type] = producer
    }

    @Suppress("UNCHECKED_CAST")
    override fun <PN : PipelineNode, GT : GraphType<PN>> resolve(
        graphType: GT,
        graphNode: GraphNode
    ): PipelineNodeProducer<PN, GT> {
        val producer = producers.asSequence()
            .filter { it.key.isInstance(graphType) }
            .mapNotNull { (_, value) -> value[graphNode.type] }
            .firstOrNull()
        checkNotNull(producer) { "Cannot find producer for graph node: $graphNode" }
        return producer as PipelineNodeProducer<PN, GT>
    }
}