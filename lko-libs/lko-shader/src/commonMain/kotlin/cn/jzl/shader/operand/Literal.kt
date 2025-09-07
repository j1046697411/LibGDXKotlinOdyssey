package cn.jzl.shader.operand

import cn.jzl.shader.VarType

interface Literal<V, T : VarType> : Operand<T> {
    val value: V

    data class IntLiteral(override val value: Int) : AbstractOperand<VarType.Integer>(VarType.Integer), Literal<Int, VarType.Integer>
    data class FloatLiteral(override val value: Float) : AbstractOperand<VarType.Float>(VarType.Float), Literal<Float, VarType.Float>
    data class BooleanLiteral(override val value: Boolean) : AbstractOperand<VarType.Bool>(VarType.Bool), Literal<Boolean, VarType.Bool>
}