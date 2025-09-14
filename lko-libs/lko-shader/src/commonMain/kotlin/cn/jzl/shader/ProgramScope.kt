package cn.jzl.shader

import cn.jzl.shader.Property
import cn.jzl.shader.Statement
import cn.jzl.shader.Struct
import cn.jzl.shader.StructConstructor
import cn.jzl.shader.StructDeclaration
import cn.jzl.shader.VarType
import cn.jzl.shader.VariableProperty
import kotlin.reflect.KProperty

interface ProgramScope {

    fun vertexShader(block: VertexShaderScope.() -> Unit)

    fun fragmentShader(block: FragmentShaderScope.() -> Unit)

    interface StatementScope {
        fun <S : Statement> statement(statement: S): S
    }

    interface ExpressionScope {

        val Float.lit: Operand.Literal<Float, VarType.Float> get() = Operand.Literal.FloatLiteral(this)
        val Int.lit: Operand.Literal<Int, VarType.Integer> get() = Operand.Literal.IntLiteral(this)
        val Boolean.lit: Operand.Literal<Boolean, VarType.Boolean> get() = Operand.Literal.BooleanLiteral(this)

        infix fun <T : VarType.Comparable> Operand<T>.eq(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, "==", other, VarType.Boolean)
        }

        infix fun <T : VarType.Comparable> Operand<T>.ne(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, "!=", other, VarType.Boolean)
        }

        infix fun <T : VarType.Comparable> Operand<T>.lt(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, "<", other, VarType.Boolean)
        }

        infix fun <T : VarType.Comparable> Operand<T>.gt(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, ">", other, VarType.Boolean)
        }

        infix fun <T : VarType.Comparable> Operand<T>.le(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, "<=", other, VarType.Boolean)
        }

        infix fun <T : VarType.Comparable> Operand<T>.ge(other: Operand<T>): Operand<VarType.Boolean> {
            return Operand.Operator.BinaryOperator(this, ">=", other, VarType.Boolean)
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

    interface ForScope : CodeBlockScope {
        val loopVar: Operand.Variable<VarType.Integer>
    }

    interface CodeBlockScope : StatementScope, ExpressionScope {

        fun codeBlock(block: CodeBlockScope.() -> Unit): Statement.CodeBlock

        fun ifStatement(condition: Operand<*>, block: CodeBlockScope.() -> Unit): Statement.If {
            return statement(Statement.If(condition, codeBlock(block)))
        }

        fun Statement.BeforeElse.elseIfStatement(condition: Operand<*>, block: CodeBlockScope.() -> Unit): Statement.ElseIf {
            return statement(Statement.ElseIf(condition, codeBlock(block)))
        }

        infix fun Statement.BeforeElse.elseStatement(block: CodeBlockScope.() -> Unit): Statement.Else {
            return statement(Statement.Else(codeBlock(block)))
        }

        fun forStatement(
            min: Operand<VarType.Integer>,
            max: Operand<VarType.Integer>,
            step: Operand<VarType.Integer> = 1.lit,
            block: ForScope.() -> Unit
        ): Statement.For


        operator fun <T : VarType> T.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
            val temporaryVariable = Operand.TemporaryVariable(property.name, this)
            statement(Statement.VariableDefinition(temporaryVariable, null))
            return VariableProperty(this@CodeBlockScope, temporaryVariable)
        }

        operator fun <T : VarType> Operand<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
            val temporaryVariable = Operand.TemporaryVariable(property.name, type)
            statement(Statement.VariableDefinition(temporaryVariable, this))
            return VariableProperty(this@CodeBlockScope, temporaryVariable)
        }

        operator fun <S : Struct<S>> S.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S>

        operator fun <S : Struct<S>> StructConstructor<S>.invoke(vararg properties: Operand<*>): StructDeclaration<S>

        operator fun <S : Struct<S>> StructDeclaration<S>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S>
    }

    interface FunctionScope<R : VarType> : CodeBlockScope {

        fun <T : VarType> arg(type: T): ArgConstructor<T> = ArgConstructor(type)

        fun returnValue(returnValue: Operand<R>): Statement.Return<R>

        operator fun <T : VarType> ArgConstructor<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>>
    }

    interface ShaderScope : CodeBlockScope {
        fun <R : VarType> func(name: String, block: FunctionScope<R>.() -> Unit): FunctionDeclaration<R>
    }

    interface VertexShaderScope : ShaderScope

    interface FragmentShaderScope : ShaderScope
}