package cn.jzl.shader.struct

import cn.jzl.shader.VarType
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

fun interface StructPropertyDelegate<T : VarType, S : VarType.Struct<S>> : PropertyDelegateProvider<S, StructProperty<T, S>> {
    override fun provideDelegate(thisRef: S, property: KProperty<*>): StructProperty<T, S>
}