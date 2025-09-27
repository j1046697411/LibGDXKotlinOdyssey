package cn.jzl.shader

data class FunctionDeclaration<T : VarType>(
    val name: String,
    val args: List<Statement.VariableDefinition<*>>,
    val body: Statement.CodeBlock,
    val returnType: T
)

