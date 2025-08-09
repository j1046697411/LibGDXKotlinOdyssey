package cn.jzl.graph.shader.builder.property

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties

interface GraphShaderPropertyProducer {
    fun createProperty(world: World, graph: GraphWithProperties, graphNode: GraphNode) : ShaderPropertySource
}

