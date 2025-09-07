package cn.jzl.shader.operand

import cn.jzl.shader.VarType

interface Func<T : VarType> : Operand<T> {
    val name: String
    val params: List<Operand<*>>
}