package cn.jzl.shader

fun <T : VarType.NumberType> ExpressionScope.oneMinus(value: Operand<T>): Operand<T> {
    return Operand.Operator.BinaryOperator(1f.lit, "-", value, value.type)
}

fun <T : VarType.NumberType> ExpressionScope.reciprocal(value: Operand<T>): Operand<T> {
    return Operand.Operator.BinaryOperator(1f.lit, "/", value, value.type)
}

fun <T : VarType.FloatType> ExpressionScope.pow(x: Operand<T>, y: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "pow", x.type, listOf(x, y))
}

fun <T : VarType.FloatType> ExpressionScope.exp(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "exp", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.log(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "log", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.exp2(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "exp2", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.log2(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "log2", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.sqrt(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "sqrt", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.inverseSqrt(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "inversesqrt", x.type, listOf(x))
}

fun <T : VarType.NumberType> ExpressionScope.abs(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "abs", x.type, listOf(x))
}

fun <T : VarType.NumberType> ExpressionScope.sign(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "sign", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.floor(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "floor", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.ceil(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "ceil", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.fract(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "fract", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.mod(x: Operand<T>, y: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "mod", x.type, listOf(x, y))
}

fun <T : VarType.NumberType> ExpressionScope.min(x: Operand<T>, y: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "min", x.type, listOf(x, y))
}

fun <T : VarType.NumberType> ExpressionScope.max(x: Operand<T>, y: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "max", x.type, listOf(x, y))
}

fun <T : VarType.NumberType> ExpressionScope.clamp(x: Operand<T>, minVal: Operand<T>, maxVal: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "clamp", x.type, listOf(x, minVal, maxVal))
}

fun <T : VarType.FloatType> ExpressionScope.mix(x: Operand<T>, y: Operand<T>, a: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "mix", x.type, listOf(x, y, a))
}

fun <T : VarType.FloatType> ExpressionScope.step(edge: Operand<T>, x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "step", edge.type, listOf(edge, x))
}

fun <T : VarType.FloatType> ExpressionScope.smoothstep(edge0: Operand<T>, edge1: Operand<T>, x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "smoothstep", edge0.type, listOf(edge0, edge1, x))
}