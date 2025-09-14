package cn.jzl.shader

sealed interface Statement {

    data object Line : Statement

    interface Definition<T : VarType, O : Operand<T>> : Statement {
        val variable: Operand<T>
        val value: Operand<T>?
    }

    data class Return<T : VarType>(val value: Operand<T>) : Statement

    data class Assignment<T : VarType>(val variable: Operand<T>, val value: Operand<T>) : Statement

    data class VariableDefinition<T : VarType>(
        override val variable: Operand.Variable<T>,
        override val value: Operand<T>?,
    ) : Definition<T, Operand<T>>

    data class ArgDefinition<T : VarType>(
        override val variable: Operand.Variable<T>,
        override val value: Operand<T>?,
    ) : Definition<T, Operand<T>>

    data class CodeBlock(val statements: List<Statement>) : Statement

    interface BeforeElse : Statement

    data class If(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse
    data class ElseIf(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse
    data class Else(val body: CodeBlock) : Statement, BeforeElse

    data class For(val init: Statement, val condition: Operand<*>, val update: Statement, val body: CodeBlock) : Statement
}