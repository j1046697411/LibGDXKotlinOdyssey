package cn.jzl.shader

import cn.jzl.shader.operand.Operand

interface Statement {

    interface BeforeElse : Statement

    @JvmInline
    value class CodeBlock(val statements: List<Statement>) : Statement

    data class Assignment(val variable: Operand<*>, val value: Operand<*>) : Statement

    data class If(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse
    data class ElseIf(val condition: Operand<*>, val body: CodeBlock) : Statement, BeforeElse
    data class Else(val body: CodeBlock) : Statement
    data class While(val condition: Operand<*>, val body: CodeBlock) : Statement
    data class For(
        val loopVar: () -> Operand<VarType.Integer>,
        val condition: (Operand<VarType.Integer>) -> Operand<*>,
        val update: (Operand<VarType.Integer>) -> Operand<*>,
        val body: CodeBlock
    ) : Statement

    data class Return<T : VarType>(val value: Operand<T>) : Statement

    data object Discard : Statement
    data object Break : Statement
    data object Continue : Statement
}