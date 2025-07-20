package cn.jzl.graph.common.data

import cn.jzl.graph.MutableGraph
import cn.jzl.graph.impl.DefaultGraph

class DefaultGraphWithProperties private constructor(private val graph: MutableGraph) : GraphWithProperties, MutableGraph by graph {
    constructor(type: String) : this(DefaultGraph(type))


    private val graphProperties = mutableMapOf<String, GraphProperty>()
    override val properties: Sequence<GraphProperty> = graphProperties.values.asSequence()

    fun addGraphProperty(graphProperty: GraphProperty) {
        graphProperties[graphProperty.name] = graphProperty
    }
}