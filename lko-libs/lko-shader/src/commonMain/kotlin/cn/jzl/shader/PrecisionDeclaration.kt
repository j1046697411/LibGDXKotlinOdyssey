package cn.jzl.shader

data class PrecisionDeclaration<T : VarType>(
    val name: String,
    val type: T,
    val typeModifier: TypeModifier,
    val precision: Precision = Precision.Default,
    val location: Int = -1,
    val initialValue: Operand<T>? = null
)