package cn.jzl.shader.struct

import cn.jzl.shader.VarType
import cn.jzl.shader.operand.Func
import cn.jzl.shader.operand.Operand

data class StructConstructorFunc<S : VarType.Struct<S>>(
    override val name: String,
    override val params: List<Operand<*>>,
    override val type: S
) : Func<S>