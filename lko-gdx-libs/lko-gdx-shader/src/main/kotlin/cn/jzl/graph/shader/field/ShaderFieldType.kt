package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType

interface ShaderFieldType<FO : FieldOutput> : FieldType<FO> {
    val realFieldType: FieldType<*>
}