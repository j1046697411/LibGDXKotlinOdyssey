package cn.jzl.graph.shader.field

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.FragmentShaderBuilder
import cn.jzl.graph.shader.core.VertexShaderBuilder

interface ShaderFieldType<FO : FieldOutput> : FieldType<FO> {
    val realFieldType: FieldType<*>
    val numberOfComponents: Int

    override fun accepts(value: Any): Boolean {
        return realFieldType.accepts(value)
    }

    fun addProperty(
        graph: GraphWithProperties,
        graphNode: GraphNode,
        vertexShaderBuilder: VertexShaderBuilder,
        fragmentShaderBuilder: FragmentShaderBuilder,
        fragmentShader: Boolean,
        propertySource: ShaderPropertySource
    ): FieldOutput
}