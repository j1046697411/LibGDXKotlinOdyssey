package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType

internal class Vector4ShaderFieldType(override val realFieldType: FieldType<*>) : ShaderFieldType<FieldOutput> {
    override val fieldType: String = "vec4"
    override fun accepts(value: Any): Boolean = realFieldType.accepts(value)
}