package cn.jzl.shader

import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Temporary
import cn.jzl.shader.struct.StructProperty
import kotlin.reflect.KProperty

interface CodeBlockScope : ExpressionScope {

    fun <S : Statement> statement(statement: S): S

    operator fun <R : VarType> FunctionDefinition<R>.invoke(vararg args: Operand<*>): Operand<R> {
        return CustomFunc(this, args.toList())
    }

    operator fun <S : VarType.Struct<S>> S.getValue(thisRef: Any?, property: KProperty<*>): S = this
    operator fun <S : VarType.Struct<S>> S.setValue(thisRef: Any?, property: KProperty<*>, value: S) {
        this assignment value
    }

    operator fun <S : VarType.Struct<S>> S.provideDelegate(thisRef: Any?, property: KProperty<*>): S

    operator fun <T : VarType> Operand<T>.getValue(thisRef: Any?, property: KProperty<*>): Operand<T> {
        return if (this is StructProperty<T, *>) this.swizzle else this
    }

    operator fun <T : VarType> Operand<T>.setValue(thisRef: Any?, property: KProperty<*>, value: Operand<T>) {
        if (this is StructProperty<T, *>) {
            this.swizzle assignment value
        } else {
            this assignment value
        }
    }

    operator fun <T : VarType> Operand<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Operand<T> {
        val temporary = Temporary(property.name, type)
        statement(VariableDefinition(temporary, this))
        return temporary
    }

    infix fun <T : VarType> Operand<T>.assignment(value: Operand<T>) = statement(Statement.Assignment(this, value))

    fun codeBlock(block: CodeBlockScope.() -> Unit): Statement.CodeBlock

    fun ifBlock(condition: Operand<*>, block: CodeBlockScope.() -> Unit): Statement.If {
        return statement(Statement.If(condition, codeBlock(block)))
    }

    fun Statement.BeforeElse.elseIf(condition: Operand<*>, block: CodeBlockScope.() -> Unit): Statement.ElseIf {
        return statement(Statement.ElseIf(condition, codeBlock(block)))
    }

    infix fun Statement.BeforeElse.elseBlock(block: CodeBlockScope.() -> Unit): Statement.Else {
        return statement(Statement.Else(codeBlock(block)))
    }
}