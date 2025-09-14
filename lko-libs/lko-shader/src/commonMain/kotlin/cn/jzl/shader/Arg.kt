package cn.jzl.shader

import cn.jzl.shader.Operand
import cn.jzl.shader.VarType

data class Arg<T : VarType>(override val name: String, override val type: T) : Operand.Variable<T>