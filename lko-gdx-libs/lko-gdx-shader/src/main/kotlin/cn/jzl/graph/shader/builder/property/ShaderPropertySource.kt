package cn.jzl.graph.shader.builder.property

import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType

interface ShaderPropertySource {
    val shaderFieldType: ShaderFieldType<out FieldOutput>
    val propertyName: String
    val propertyLocation: PropertyLocation
    val attributeFunction: String?
}