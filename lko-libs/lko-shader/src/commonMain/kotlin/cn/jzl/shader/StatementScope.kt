package cn.jzl.shader

interface StatementScope {
    fun <S : Statement> statement(statement: S): S
}