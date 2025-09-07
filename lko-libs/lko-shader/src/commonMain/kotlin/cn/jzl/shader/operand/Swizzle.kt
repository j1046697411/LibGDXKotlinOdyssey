package cn.jzl.shader.operand

import cn.jzl.shader.VarType

data class Swizzle<T : VarType>(val left: Operand<*>, val swizzle: String, override val type: T) : Operand<T>