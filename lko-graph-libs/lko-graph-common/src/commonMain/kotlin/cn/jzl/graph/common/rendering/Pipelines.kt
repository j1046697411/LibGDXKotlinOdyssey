package cn.jzl.graph.common.rendering

import cn.jzl.di.*
import cn.jzl.graph.common.*
import cn.jzl.graph.common.calculator.DefaultGeneralCalculator
import cn.jzl.graph.common.field.DefaultFieldTypeRegistry
import org.kodein.type.generic

internal const val TAG_PIPELINE_REGISTRY = "tag_pipeline_registry"

inline fun <reified PN : PipelineNode, reified GT : GraphType<in PN>> PipelineNodeProducerRegistry.register(
    producer: PipelineNodeProducer<PN, GT>
) = this.register(GT::class, producer)

fun pipelineModule(): DIModule<Any> = module(generic<Any>()) {
    this bind singleton { new(::GraphPipelineService) }
    this bind singleton { new(::DefaultPipelinePlugin) }
    this bind prototype { new(::DefaultPipelineBlackboard) }

    this bind singleton { new(::DefaultFieldTypeRegistry) }
    this bind singleton { new(::DefaultPipelineNodeProducerRegistry) }
    this bind singleton { new(::DefaultGeneralCalculator) }
    this bind singleton { new(::DefaultGraphTypeRegistry)  }
    this bind singleton(TAG_PIPELINE_REGISTRY) { new(::DefaultPipelineRegistry) }
    this bind singleton { new(::DefaultGraphPipelineRecipe) }
}
