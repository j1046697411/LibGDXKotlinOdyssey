package cn.jzl.shader.operand

import cn.jzl.shader.VarType

interface Operator<T : VarType> : Operand<T> {
    data class UnaryOperator<T : VarType>(
        val symbol: String,
        val right: Operand<T>
    ) : AbstractOperand<T>(right.type), Operator<T>

    data class BinaryOperator<T : VarType>(
        val left: Operand<T>,
        val symbol: String,
        val right: Operand<T>
    ) : AbstractOperand<T>(left.type), Operator<T>

    data class TernaryOperator<T : VarType>(
        val condition: Operand<*>,
        val left: Operand<T>,
        val right: Operand<T>
    ) : AbstractOperand<T>(left.type), Operator<T>
}