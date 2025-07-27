package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.PrimitiveFieldTypes

internal object FloatShaderFieldType : ShaderFieldType<FieldOutput> {
    override val realFieldType = PrimitiveFieldTypes.FloatFieldType
    override val fieldType: String = "float"
    override fun accepts(value: Any): Boolean = value is Float
}