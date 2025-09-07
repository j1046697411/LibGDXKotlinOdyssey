package cn.jzl.shader.struct

import cn.jzl.shader.VarType
import kotlin.reflect.KProperty

fun interface StructDelegate<S : VarType.Struct<S>> {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): S
}