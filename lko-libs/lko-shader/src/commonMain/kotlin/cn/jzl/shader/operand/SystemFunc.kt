package cn.jzl.shader.operand

import cn.jzl.shader.VarType

data class SystemFunc<T : VarType>(override val name: String, override val params: List<Operand<*>>, override val type: T) : Func<T>