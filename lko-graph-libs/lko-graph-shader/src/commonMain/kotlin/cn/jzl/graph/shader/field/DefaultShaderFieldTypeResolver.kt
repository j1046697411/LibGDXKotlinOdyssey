package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType
import cn.jzl.shader.Operand
import cn.jzl.shader.VarType

class DefaultShaderFieldTypeResolver : ShaderFieldTypeResolver {

    private val shaderFieldTypes = mutableMapOf<FieldType<*>, ShaderFieldType<*, *>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : VarType, O : Operand<out T>> resolve(fieldType: FieldType<*>): ShaderFieldType<T, O> {
        return shaderFieldTypes[fieldType] as ShaderFieldType<T, O>
    }

    fun <T : VarType, O : Operand<out T>> register(shaderFieldType: ShaderFieldType<T, O>) {
        shaderFieldTypes[shaderFieldType.graphFieldType] = shaderFieldType
    }
}