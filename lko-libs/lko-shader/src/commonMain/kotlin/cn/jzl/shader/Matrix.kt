package cn.jzl.shader


fun <T : VarType.Matrix> ExpressionScope.matrixCompMult(x: Operand<T>, y: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "matrixCompMult", x.type, listOf(x, y))
}

fun <T : VarType.Matrix> ExpressionScope.transpose(m: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "transpose", m.type, listOf(m))
}

fun <T : VarType.Matrix> ExpressionScope.determinant(m: Operand<T>): Operand<VarType.Float> {
    return Operand.SystemFunction(this, "determinant", float, listOf(m))
}

fun <T : VarType.Matrix> ExpressionScope.inverse(m: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "inverse", m.type, listOf(m))
}