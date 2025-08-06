package cn.jzl.graph.shader.builder.property

import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType

interface ShaderPropertySource {
    val shaderFieldType: ShaderFieldType<out FieldOutput>
    val propertyIndex: Int
    val propertyName: String
    fun getPropertyName(index: Int): String
    val propertyLocation: PropertyLocation
    val attributeFunction: String?
}