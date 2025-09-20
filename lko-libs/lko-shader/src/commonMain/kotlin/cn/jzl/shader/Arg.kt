package cn.jzl.shader

data class Arg<T : VarType>(override val name: String, override val type: T) : Operand.Variable<T>