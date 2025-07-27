package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType

interface ShaderFieldTypeResolver {
    fun <FO : FieldOutput> resolve(fieldType: FieldType<*>): ShaderFieldType<FO>
}