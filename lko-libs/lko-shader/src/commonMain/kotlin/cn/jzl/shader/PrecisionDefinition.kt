package cn.jzl.shader

data class PrecisionDefinition<T : VarType>(
    val typeModifier: TypeModifier,
    override val variable: Operand.Variable<T>,
    val precision: Precision,
    val location: Int,
    val initialValue: Operand<T>? = null
) : Statement.Definition<T, Operand<T>>