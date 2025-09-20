package cn.jzl.shader

import kotlin.reflect.KProperty

interface Property<V : VarType, O> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): O
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: O)
}