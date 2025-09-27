package cn.jzl.shader

import cn.jzl.shader.ProgramScope.FunctionScope
import kotlin.reflect.KProperty

fun <R : VarType> ExpressionScope.func(
    name: String,
    vararg args: Operand<*>,
    block: FunctionScope<R>.() -> Unit
): Operand<R> {
    val functionDeclaration = func(name, block)
    if (functionDeclaration.args.size != args.size) {
        throw IllegalArgumentException("Function $name argument count mismatch")
    }
    val checkResult = functionDeclaration.args.zip(args) { arg, operand ->
        arg.variable.type == operand.type
    }
    if (!checkResult.all { it }) {
        throw IllegalArgumentException("Function $name argument type mismatch")
    }
    return func(name, block)(*args)
}

interface ExpressionScope : VarTypeAccessor, PrimitiveScope, VectorScope {

    val <T : VarType> PrecisionDeclaration<T>.instance: Operand<T>

    fun <R : VarType> func(name: String, block: FunctionScope<R>.() -> Unit): FunctionDeclaration<R>

    operator fun <T : VarType> FunctionDeclaration<T>.invoke(vararg arguments: Operand<*>): Operand<T> {
        return Operand.SystemFunction(this@ExpressionScope, name, returnType, arguments.toList())
    }

    fun <T : VarType> Operand<T>.define(name: String, precision: Precision = Precision.Default): PrecisionDeclaration<T> {
        return PrecisionDeclaration(name, type, TypeModifier.Define, precision, -1, this)
    }

    fun <T : VarType> Operand<T>.constant(name: String, precision: Precision = Precision.Default): PrecisionDeclaration<T> {
        return PrecisionDeclaration(name, type, TypeModifier.Const, precision, -1, this)
    }

    operator fun <T : VarType> PrecisionDeclaration<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>>

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

    infix operator fun <T : VarType.FloatType> Operand<T>.plus(other: Float): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "+", other.lit, type)
    }

    infix operator fun <T : VarType.FloatType> Operand<T>.minus(other: Float): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "-", other.lit, type)
    }

    infix operator fun <T : VarType.FloatType> Operand<T>.times(other: Float): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "*", other.lit, type)
    }

    infix operator fun <T : VarType.FloatType> Operand<T>.div(other: Float): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "/", other.lit, type)
    }

    infix operator fun <T : VarType.FloatType> Operand<T>.rem(other: Float): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "%", other.lit, type)
    }

    infix operator fun <T : VarType.FloatType> Float.plus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "+", other, other.type)
    }

    infix operator fun <T : VarType.FloatType> Float.minus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "-", other, other.type)
    }

    infix operator fun <T : VarType.FloatType> Float.times(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "*", other, other.type)
    }

    infix operator fun <T : VarType.FloatType> Float.div(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "/", other, other.type)
    }

    infix operator fun <T : VarType.FloatType> Float.rem(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "%", other, other.type)
    }

    infix operator fun <T : VarType.IntegerType> Operand<T>.plus(other: Int): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "+", other.lit, type)
    }

    infix operator fun <T : VarType.IntegerType> Operand<T>.minus(other: Int): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "-", other.lit, type)
    }

    infix operator fun <T : VarType.IntegerType> Operand<T>.times(other: Int): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "*", other.lit, type)
    }

    infix operator fun <T : VarType.IntegerType> Operand<T>.div(other: Int): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "/", other.lit, type)
    }

    infix operator fun <T : VarType.IntegerType> Operand<T>.rem(other: Int): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "%", other.lit, type)
    }

    infix operator fun <T : VarType.IntegerType> Int.plus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "+", other, other.type)
    }

    infix operator fun <T : VarType.IntegerType> Int.minus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "-", other, other.type)
    }

    infix operator fun <T : VarType.IntegerType> Int.times(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "*", other, other.type)
    }

    infix operator fun <T : VarType.IntegerType> Int.div(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "/", other, other.type)
    }

    infix operator fun <T : VarType.IntegerType> Int.rem(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this.lit, "%", other, other.type)
    }

    operator fun <T : VarType.Computable> Operand<T>.unaryMinus(): Operand<T> {
        return Operand.Operator.UnaryOperator("-", this, type)
    }

    operator fun <T : VarType.Computable> Operand<T>.unaryPlus(): Operand<T> {
        return Operand.Operator.UnaryOperator("+", this, type)
    }
}