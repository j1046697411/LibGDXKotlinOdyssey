package cn.jzl.shader

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConstantProperty<T : VarType>(
    private val provider: ExpressionScope.() -> PrecisionDeclaration<T>
) : ReadOnlyProperty<ExpressionScope, Operand<T>> {

    private val values = mutableMapOf<ExpressionScope, Operand<T>>()

    override fun getValue(thisRef: ExpressionScope, property: KProperty<*>): Operand<T> {
        return values.getOrPut(thisRef) {
            with(thisRef) {
                val value by provider(thisRef)
                value
            }
        }
    }
}

fun <T : VarType> constantProperty(provider: ExpressionScope.() -> PrecisionDeclaration<T>): ConstantProperty<T> = ConstantProperty(provider)

val ExpressionScope.PI: Operand<VarType.Float> by constantProperty { kotlin.math.PI.toFloat().lit.define("PI") }
val ExpressionScope.E : Operand<VarType.Float> by constantProperty { kotlin.math.E.toFloat().lit.define("E") }

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

// 双曲函数相关的反函数
fun <T : VarType.FloatType> ExpressionScope.asinh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "asinh", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.acosh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "acosh", x.type, listOf(x))
}

fun <T : VarType.FloatType> ExpressionScope.atanh(x: Operand<T>): Operand<T> {
    return Operand.SystemFunction(this, "atanh", x.type, listOf(x))
}
//
//// 角度归一化函数，将角度限制在[0, 2π)范围内
//fun <T : VarType.FloatType> ExpressionScope.normalizeAngle(angle: Operand<T>): Operand<T> {
//    // 创建2π常量
//    val twoPi = float(6.2831855f)
//
//    // 计算模运算
//    val modResult = angle % twoPi as Operand<T>
//
//    // 处理负数情况
//    return if (modResult < float(0f)) {
//        modResult + twoPi as Operand<T>
//    } else {
//        modResult
//    }
//}
//
//// 计算两点之间的角度
//fun ExpressionScope.angleBetween(from: Operand<VarType.Vec2>, to: Operand<VarType.Vec2>): Operand<VarType.Float> {
//    val deltaX = to.x - from.x
//    val deltaY = to.y - from.y
//    return atan(deltaY, deltaX)
//}
//
//// 计算向量的方向角
//fun ExpressionScope.directionAngle(vec: Operand<VarType.Vec2>): Operand<VarType.Float> {
//    return atan(vec.y, vec.x)
//}
//
//// 角度差值函数（最短路径）
//fun <T : VarType.FloatType> ExpressionScope.angleDifference(a: Operand<T>, b: Operand<T>): Operand<T> {
//    // 创建π常量
//    val pi = float(3.1415927f)
//
//    // 计算角度差
//    var diff = b - a
//
//    // 确保差值在[-π, π]范围内
//    while (diff > pi) {
//        diff -= float(2f) * pi
//    }
//
//    while (diff < -pi) {
//        diff += float(2f) * pi
//    }
//
//    return diff as Operand<T>
//}
//
//// 角度线性插值
//fun <T : VarType.FloatType> ExpressionScope.angleLerp(a: Operand<T>, b: Operand<T>, t: Operand<T>): Operand<T> {
//    // 计算最短路径的角度差
//    val diff = angleDifference(a, b)
//
//    // 线性插值
//    return a + diff * t
//}
//
//// 计算向量与X轴的夹角
//fun ExpressionScope.angleWithXAxis(vec: Operand<VarType.Vec2>): Operand<VarType.Float> {
//    return atan(vec.y, vec.x)
//}
//
//// 计算向量与Y轴的夹角
//fun ExpressionScope.angleWithYAxis(vec: Operand<VarType.Vec2>): Operand<VarType.Float> {
//    return atan(vec.x, vec.y)
//}
//
//// 根据角度创建方向向量
//fun ExpressionScope.directionVector(angle: Operand<VarType.Float>): Operand<VarType.Vec2> {
//    return vec2(cos(angle), sin(angle))
//}
//
//// 2D旋转矩阵
//fun ExpressionScope.rotationMatrix2D(angle: Operand<VarType.Float>): Operand<VarType.Mat2> {
//    val c = cos(angle)
//    val s = sin(angle)
//    return mat2(
//        c, -s,
//        s, c
//    )
//}
//
//// 3D绕Z轴旋转矩阵
//fun ExpressionScope.rotationMatrixZ(angle: Operand<VarType.Float>): Operand<VarType.Mat4> {
//    val c = cos(angle)
//    val s = sin(angle)
//    return mat4(
//        c, -s, 0f.lit, 0f.lit,
//        s, c, 0f.lit, 0f.lit,
//        0f.lit, 0f.lit, 1f.lit, 0f.lit,
//        0f.lit, 0f.lit, 0f.lit, 1f.lit
//    )
//}
//
//// 3D绕X轴旋转矩阵
//fun ExpressionScope.rotationMatrixX(angle: Operand<VarType.Float>): Operand<VarType.Mat4> {
//    val c = cos(angle)
//    val s = sin(angle)
//    return mat4(
//        1f.lit, 0f.lit, 0f.lit, 0f.lit,
//        0f.lit, c, -s, 0f.lit,
//        0f.lit, s, c, 0f.lit,
//        0f.lit, 0f.lit, 0f.lit, 1f.lit
//    )
//}
//
//// 3D绕Y轴旋转矩阵
//fun ExpressionScope.rotationMatrixY(angle: Operand<VarType.Float>): Operand<VarType.Mat4> {
//    val c = cos(angle)
//    val s = sin(angle)
//    return mat4(
//        c, 0f.lit, s, 0f.lit,
//        0f.lit, 1f.lit, 0f.lit, 0f.lit,
//        -s, 0f.lit, c, 0f.lit,
//        0f.lit, 0f.lit, 0f.lit, 1f.lit
//    )
//}
//
//// 角度平滑步进（smoothstep的角度版本）
//fun <T : VarType.FloatType> ExpressionScope.angleSmoothstep(edge0: Operand<T>, edge1: Operand<T>, x: Operand<T>): Operand<T> {
//    // 将输入角度归一化到相同的环绕周期
//    val normEdge0 = normalizeAngle(edge0)
//    val normEdge1 = normalizeAngle(edge1)
//    val normX = normalizeAngle(x)
//
//    // 计算最短路径的差值
//    val diff = angleDifference(normEdge0, normEdge1)
//
//    // 计算x在[edge0, edge0 + diff]范围内的相对位置
//    val t = if (diff > float(0f)) {
//        ((normX - normEdge0) / diff).clamp(float(0f), float(1f))
//    } else {
//        float(0f)
//    }
//
//    // 应用smoothstep的三次多项式插值
//    return t * t * (float(3f) - float(2f) * t) as Operand<T>
//}
//
//// 限制角度在指定范围内
//fun <T : VarType.FloatType> ExpressionScope.clampAngle(angle: Operand<T>, min: Operand<T>, max: Operand<T>): Operand<T> {
//    val normAngle = normalizeAngle(angle)
//    val normMin = normalizeAngle(min)
//    val normMax = normalizeAngle(max)
//
//    // 检查角度是否在[min, max]范围内（考虑环绕情况）
//    val isBetween = if (normMax >= normMin) {
//        normAngle >= normMin and (normAngle <= normMax)
//    } else {
//        normAngle >= normMin or (normAngle <= normMax)
//    }
//
//    return if (isBetween) {
//        normAngle
//    } else {
//        // 找到最近的边界
//        val distToMin = angleDifference(normAngle, normMin).abs()
//        val distToMax = angleDifference(normAngle, normMax).abs()
//
//        if (distToMin < distToMax) normMin else normMax
//    } as Operand<T>
//}
//
//// 计算反射角
//fun <T : VarType.FloatType> ExpressionScope.reflectAngle(incident: Operand<T>, normal: Operand<T>): Operand<T> {
//    // 将角度转换为方向向量
//    val incidentDir = directionVector(incident)
//    val normalDir = directionVector(normal)
//
//    // 计算反射向量
//    val dotProduct = incidentDir dot normalDir
//    val reflectedDir = incidentDir - float(2f) * dotProduct * normalDir
//
//    // 将反射向量转换回角度
//    return directionAngle(reflectedDir)
//}