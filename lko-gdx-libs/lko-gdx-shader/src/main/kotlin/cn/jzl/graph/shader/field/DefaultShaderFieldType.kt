package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType

internal class DefaultShaderFieldType<FO : FieldOutput>(
    override val realFieldType: FieldType<*>
) : ShaderFieldType<FO> {
    override val fieldType: String get() = realFieldType.fieldType

    override fun accepts(value: Any): Boolean = realFieldType.accepts(value)
}

