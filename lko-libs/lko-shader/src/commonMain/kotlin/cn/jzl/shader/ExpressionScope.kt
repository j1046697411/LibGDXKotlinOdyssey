package cn.jzl.shader

interface ExpressionScope : VarTypeAccessor, PrimitiveScope, VectorScope {

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.eq(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "==", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.ne(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "!=", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.lt(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "<", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.gt(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, ">", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.le(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "<=", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.ge(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, ">=", other, type.resultType)
    }

    fun Operand<out VarType.BooleanVector>.all(): Operand<VarType.Boolean> {
        return Operand.SystemFunction(this@ExpressionScope, "all", bool, listOf(this))
    }

    fun Operand<out VarType.BooleanVector>.any(): Operand<VarType.Boolean> {
        return Operand.SystemFunction(this@ExpressionScope, "any", bool, listOf(this))
    }

    fun Operand<out VarType.BooleanVector>.not(): Operand<VarType.BooleanVector> {
        return Operand.SystemFunction(this@ExpressionScope, "not", type, listOf(this))
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