package cn.jzl.shader

fun <T : VarType.FloatType> ExpressionScope.sin(angle: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "sin", angle.type, listOf(angle))
}

fun <T : VarType.FloatType> ExpressionScope.cos(angle: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "cos", angle.type, listOf(angle))
}

fun <T : VarType.FloatType> ExpressionScope.tan(angle: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "tan", angle.type, listOf(angle))
}

fun <T : VarType.FloatType> ExpressionScope.asin(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "asin", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.acos(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "acos", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.atan(y: Operand<T>, x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "atan", y.type, listOf(y, x))
}

fun <T : VarType.FloatType> ExpressionScope.atan(yOverX: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "atan", yOverX.type, listOf(yOverX))
}

fun <T : VarType.FloatType> ExpressionScope.sinh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "sinh", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.cosh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "cosh", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.tanh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "tanh", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.radians(degrees: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "radians", degrees.type, listOf(degrees))
}

fun <T : VarType.FloatType> ExpressionScope.degrees(radians: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "degrees", radians.type, listOf(radians))
}