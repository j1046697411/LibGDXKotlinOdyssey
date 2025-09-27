package cn.jzl.graph.common.config

import cn.jzl.graph.common.time.TimeProvider
import kotlin.reflect.KClass

interface GraphPipelineConfiguration {

    val timeProvider: TimeProvider

    val propertyContainer: MutablePropertyContainer

    fun <C : GraphConfiguration> getConfiguration(type: KClass<C>): C
}

