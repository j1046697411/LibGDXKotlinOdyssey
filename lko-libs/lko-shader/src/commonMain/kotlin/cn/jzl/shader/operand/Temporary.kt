package cn.jzl.shader.operand

import cn.jzl.shader.VarType

data class Temporary<T : VarType>(override val name: String, override val type: T) : Variable<T>