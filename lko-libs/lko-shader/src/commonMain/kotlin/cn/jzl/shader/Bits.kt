package cn.jzl.shader


fun <T : VarType.IntegerType> ExpressionScope.bitfieldExtract(value: Operand<T>, offset: Operand<T>, bits: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "bitfieldExtract", value.type, listOf(value, offset, bits))
}

fun <T : VarType.IntegerType> ExpressionScope.bitfieldInsert(base: Operand<T>, insert: Operand<T>, offset: Operand<T>, bits: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "bitfieldInsert", base.type, listOf(base, insert, offset, bits))
}

fun <T : VarType.IntegerType> ExpressionScope.bitfieldReverse(value: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "bitfieldReverse", value.type, listOf(value))
}

fun <T : VarType.IntegerType> ExpressionScope.bitCount(value: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "bitCount", value.type, listOf(value))
}

fun <T : VarType.IntegerType> ExpressionScope.findLSB(value: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "findLSB", value.type, listOf(value))
}

fun <T : VarType.IntegerType> ExpressionScope.findMSB(value: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "findMSB", value.type, listOf(value))
}