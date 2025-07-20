package cn.jzl.graph.common.rendering

import cn.jzl.di.*
import org.kodein.type.generic

fun pipelineModule() : DIModule<Any> = module(generic<Any>()) {
    this bind singleton { new(::GraphPipelineService) }
    this bind singleton { new(::DefaultPipelinePlugin) }
    this bind prototype { new(::DefaultPipelineBlackboard) }

    this bind singleton { new(::DefaultPipelineRegistry) }
}
