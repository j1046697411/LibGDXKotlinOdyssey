package cn.jzl.shader.struct

import cn.jzl.shader.VarType
import cn.jzl.shader.operand.Operand

fun interface StructConstructor<S : VarType.Struct<S>> {
    operator fun invoke(vararg properties: Operand<*>): StructDelegate<S>
}

