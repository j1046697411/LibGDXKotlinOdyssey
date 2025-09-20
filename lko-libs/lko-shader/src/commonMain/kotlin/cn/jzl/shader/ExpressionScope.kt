package cn.jzl.shader

interface ExpressionScope : VarTypeAccessor {

    infix fun <T : VarType.Comparable> Operand<T>.eq(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, "==", other, bool)
    }

    infix fun <T : VarType.Comparable> Operand<T>.ne(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, "!=", other, bool)
    }

    infix fun <T : VarType.Comparable> Operand<T>.lt(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, "<", other, bool)
    }

    infix fun <T : VarType.Comparable> Operand<T>.gt(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, ">", other, bool)
    }

    infix fun <T : VarType.Comparable> Operand<T>.le(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, "<=", other, bool)
    }

    infix fun <T : VarType.Comparable> Operand<T>.ge(other: Operand<T>): Operand<VarType.Boolean> {
        return Operand.Operator.BinaryOperator(this, ">=", other, bool)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.plus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "+", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.minus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "-", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.times(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "*", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.div(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "/", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.rem(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "%", other, type)
    }
}