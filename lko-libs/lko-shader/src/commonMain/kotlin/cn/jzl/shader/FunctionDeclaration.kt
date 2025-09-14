package cn.jzl.shader

import cn.jzl.shader.Statement
import cn.jzl.shader.VarType

data class FunctionDeclaration<T : VarType>(
    val name: String,
    val args: List<Statement.ArgDefinition<*>>,
    val body: Statement.CodeBlock,
    val returnType: T
)