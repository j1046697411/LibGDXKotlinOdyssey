package cn.jzl.graph.common.config

import cn.jzl.graph.common.time.TimeProvider
import kotlin.reflect.KClass

class DefaultGraphPipelineConfiguration(
    override val timeProvider: TimeProvider,
    override val propertyContainer: MutablePropertyContainer = DefaultPropertyContainer()
) : GraphPipelineConfiguration {

    private val configs = mutableMapOf<KClass<out GraphConfiguration>, GraphConfiguration>()

    fun <C : GraphConfiguration> setConfiguration(type: KClass<C>, configuration: C) {
        configs[type] = configuration
    }

    @Suppress("UNCHECKED_CAST")
    override fun <C : GraphConfiguration> getConfiguration(type: KClass<C>): C {
        return configs[type] as C
    }
}