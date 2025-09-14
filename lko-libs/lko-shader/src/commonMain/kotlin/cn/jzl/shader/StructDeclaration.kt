package cn.jzl.shader

import cn.jzl.shader.Struct
import kotlin.reflect.KClass

data class StructDeclaration<S : Struct<S>>(
    val structType: KClass<S>,
    val struct: S,
    val constructor: StructConstructor<S>,
    val defaultProperties: List<Operand<*>>
)