package cn.jzl.graph.shader.builder.property

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType

interface GraphShaderPropertyProducer {
    val shaderFieldType: ShaderFieldType<out FieldOutput>
    fun createProperty(world: World, graph: GraphWithProperties, graphNode: GraphNode) : ShaderPropertySource
}