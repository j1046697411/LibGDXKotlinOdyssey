package cn.jzl.shader

import kotlin.reflect.KClass

interface StructConstructor<S : Struct<S>> {
    val structType: KClass<S>
    val structName: String get() = structType.simpleName ?: "Undefined"
    val factory: (StatementScope, String) -> S
}