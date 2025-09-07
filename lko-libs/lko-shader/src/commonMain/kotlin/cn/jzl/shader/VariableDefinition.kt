package cn.jzl.shader

import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Variable

data class VariableDefinition<T : VarType>(val variable: Variable<T>, val value: Operand<T>? = null) : Statement