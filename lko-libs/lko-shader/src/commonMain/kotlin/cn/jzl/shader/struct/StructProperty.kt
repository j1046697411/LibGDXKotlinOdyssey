package cn.jzl.shader.struct

import cn.jzl.shader.VarType
import cn.jzl.shader.operand.Variable
import cn.jzl.shader.operand.Operand
import kotlin.reflect.KProperty

interface StructProperty<T : VarType, S : VarType.Struct<S>> : Variable<T> {
    val struct: S
    val swizzle: Operand<T>

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Operand<T>
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Operand<T>)
}