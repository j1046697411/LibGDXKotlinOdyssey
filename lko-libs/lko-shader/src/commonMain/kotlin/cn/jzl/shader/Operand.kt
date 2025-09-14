package cn.jzl.shader

import cn.jzl.shader.VarType

interface Operand<T : VarType> {
    val type: T

    abstract class AbstractOperand<T : VarType>(override val type: T) : Operand<T>

    data object Void : AbstractOperand<VarType.Void>(VarType.Void)

    sealed interface Literal<V, T : VarType> : Operand<T> {
        val value: V

        data class IntLiteral(override val value: Int) : AbstractOperand<VarType.Integer>(VarType.Integer), Literal<Int, VarType.Integer>
        data class FloatLiteral(override val value: Float) : AbstractOperand<VarType.Float>(VarType.Float), Literal<Float, VarType.Float>
        data class BooleanLiteral(override val value: Boolean) : AbstractOperand<VarType.Boolean>(VarType.Boolean), Literal<Boolean, VarType.Boolean>
    }

    interface Variable<T : VarType> : Operand<T> {
        val name: String
    }

    data class TemporaryVariable<T : VarType>(override val name: String, override val type: T) : Variable<T>

    data class Swizzle<T : VarType>(val left: Operand<*>, val swizzle: String, override val type: T) : Operand<T>
    sealed interface Function<T : VarType> : Operand<T> {
        val name: String
        val args: List<Operand<*>>
    }

    data class SystemFunction<T : VarType>(
        override val name: String,
        override val type: T,
        override val args: List<Operand<*>>
    ) : Function<T>

    sealed interface Operator<T : VarType> : Operand<T> {
        data class UnaryOperator<T : VarType>(val symbol: String, val right: Operand<*>, override val type: T) : Operand<T>, Operator<T>
        data class BinaryOperator<T : VarType>(val left: Operand<*>, val symbol: String, val right: Operand<*>, override val type: T) : Operand<T>, Operator<T>
        data class TernaryOperator<T : VarType>(val condition: Operand<*>, val left: Operand<*>, val right: Operand<*>, override val type: T) : Operand<T>, Operator<T>
    }
}