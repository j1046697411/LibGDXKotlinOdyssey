package cn.jzl.shader

sealed interface Statement {

    interface InlineStatement : Statement {
        val inline: Boolean
    }

    interface Definition<T : VarType, O : Operand<T>> : InlineStatement {
        val variable: Operand<T>
        val value: Operand<T>?
        override val inline: Boolean
    }

    data class Return<T : VarType>(val value: Operand<T>) : Statement

    data class Assignment<T : VarType>(val variable: Operand<T>, val value: Operand<T>, override val inline: Boolean = false) : Statement, InlineStatement

    data class VariableDefinition<T : VarType>(
        override val variable: Operand.Variable<T>,
        override val value: Operand<T>?,
        override val inline: Boolean = false
    ) : Definition<T, Operand<T>>

    data class CodeBlock(val statements: List<Statement>, override var inline: Boolean = false) : InlineStatement

    interface BeforeElse : InlineStatement {
        override var inline: Boolean
    }

    data class If(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse {
        override var inline: Boolean by body::inline
    }

    data class ElseIf(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse {
        override var inline: Boolean by body::inline
    }

    data class Else(val body: CodeBlock) : Statement

    data class For(val init: Statement, val condition: Operand<*>, val update: Statement, val body: CodeBlock) : Statement

    data class While(val condition: Operand<*>, val body: CodeBlock) : Statement

    data object Break : Statement
    data object Continue : Statement
    data object Discard : Statement
}