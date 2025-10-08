package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType
import cn.jzl.shader.Operand
import cn.jzl.shader.VarType

interface ShaderFieldType<T : VarType, O : Operand<out T>> : FieldType<O> {
    val varType: T
    val graphFieldType: FieldType<*>
}