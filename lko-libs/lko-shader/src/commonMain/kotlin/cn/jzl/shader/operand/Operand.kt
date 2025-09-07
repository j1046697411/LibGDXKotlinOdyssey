package cn.jzl.shader.operand

import cn.jzl.shader.VarType

interface Operand<T : VarType> {
    val type: T

    companion object Void : Operand<VarType.Void> {
        override val type: VarType.Void = VarType.Void
    }
}

