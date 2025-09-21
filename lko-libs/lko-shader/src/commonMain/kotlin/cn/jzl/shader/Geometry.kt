package cn.jzl.shader

fun <T : VarType.FloatType> ExpressionScope.length(x: Operand<T>): Operand<VarType.Float> {
    return Operand.SystemFunction(this, "length", float, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.distance(p0: Operand<T>, p1: Operand<T>): Operand<VarType.Float> {
    return Operand.SystemFunction(this, "distance", float, listOf(p0, p1))
}

fun <T : VarType.FloatType> ExpressionScope.dot(x: Operand<T>, y: Operand<T>): Operand<VarType.Float> {
    return Operand.SystemFunction(this, "dot", float, listOf(x, y))
}

fun ExpressionScope.cross(x: Operand<VarType.Vec3>, y: Operand<VarType.Vec3>): Operand<VarType.Vec3> {
    return Operand.SystemFunction(this, "cross", vec3, listOf(x, y))
}

fun <T : VarType.FloatType> ExpressionScope.normalize(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "normalize", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.reflect(i: Operand<T>, n: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "reflect", i.type, listOf(i, n))
}

fun <T : VarType.FloatType> ExpressionScope.refract(i: Operand<T>, n: Operand<T>, eta: Operand<VarType.Float>): Operand<T> {
    return Operand.SystemFunction(this, "refract", i.type, listOf(i, n, eta))
}