package cn.jzl.shader.operand

import cn.jzl.shader.VarType

sealed class AbstractOperand<T : VarType>(override val type: T) : Operand<T>