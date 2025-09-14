package cn.jzl.shader

import cn.jzl.shader.Struct
import kotlin.reflect.KClass

interface StructConstructor<S : Struct<S>> {
    val structType: KClass<S>
    val structName: String get() = structType.simpleName ?: "Undefined"
    val factory: (ProgramScope.StatementScope, String) -> S
}