/**
 * 向量类型与常用运算集合。
 *
 * 提供 `Vector2/3/4` 与 `IntVector2/3/4` 以及它们的：
 * - 操作符重载（加、减、乘、除、取模、一元正负）
 * - 几何相关函数（长度、归一化、点/叉积、角度、投影、反射）
 * - 插值与夹取（`lerp`、`coerceIn`、`clamp`、`min/max`、近似比较）
 *
 * 约定：
 * - 浮点向量以 `Float` 进行几何计算；整数向量保持整型分量以便网格/像素坐标。
 * - `Ratio` 表示插值比例，通常为 `[0, 1]`，但并不强制限制。
 */
package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.math.*
import cn.jzl.datastructure.math.vector.generic.IGenericVector
import kotlin.math.*

typealias Point = Vector2

interface IVector2<T> : IGenericVector<T> {
    val x: T
    val y: T
    override val dimensions: Int get() = 2
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

interface IVector3<T> : IVector2<T> {
    val z: T
    override val dimensions: Int get() = 3
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

interface IVector4<T> : IVector3<T> {
    val w: T
    override val dimensions: Int get() = 4
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

data class Vector2(override val x: Float, override val y: Float) : IVector2<Float>, Interpolable<Vector2> {
    override fun interpolateTo(end: Vector2, ratio: Ratio): Vector2 = lerp(end, ratio)

    companion object {
        /** 零向量（0, 0）。 */
        val ZERO = Vector2(0f, 0f)

        /** 单位向量（1, 0）。 */
        val UNIT_X = Vector2(1f, 0f)

        /** 单位向量（0, 1）。 */
        val UNIT_Y = Vector2(0f, 1f)

        /** 单位向量（1, 1）。 */
        val ONE = Vector2(1f, 1f)
    }
}

data class Vector3(override val x: Float, override val y: Float, override val z: Float) : IVector3<Float>, Interpolable<Vector3> {
    override fun interpolateTo(end: Vector3, ratio: Ratio): Vector3 = lerp(end, ratio)

    companion object {
        /** 零向量（0, 0, 0）。 */
        val ZERO = Vector3(0f, 0f, 0f)

        /** 单位向量（1, 0, 0）。 */
        val UNIT_X = Vector3(1f, 0f, 0f)

        /** 单位向量（0, 1, 0）。 */
        val UNIT_Y = Vector3(0f, 1f, 0f)

        /** 单位向量（0, 0, 1）。 */
        val UNIT_Z = Vector3(0f, 0f, 1f)

        /** 单位向量（1, 1, 1）。 */
        val ONE = Vector3(1f, 1f, 1f)
    }
}

data class Vector4(override val x: Float, override val y: Float, override val z: Float, override val w: Float) : IVector4<Float>, Interpolable<Vector4> {
    override fun interpolateTo(end: Vector4, ratio: Ratio): Vector4 = lerp(end, ratio)
    companion object {
        /** 零向量（0, 0, 0, 0）。 */
        val ZERO = Vector4(0f, 0f, 0f, 0f)

        /** 单位向量（1, 1, 1, 1）。 */
        val ONE = Vector4(1f, 1f, 1f, 1f)
    }
}

data class IntVector2(override val x: Int, override val y: Int) : IVector2<Int>, Interpolable<IntVector2> {
    override fun interpolateTo(end: IntVector2, ratio: Ratio): IntVector2 = lerp(end, ratio)
    companion object {
        /** 零向量（0, 0）。 */
        val ZERO = IntVector2(0, 0)
        /** 单位向量（1, 0）。 */
        val UNIT_X = IntVector2(1, 0)
        /** 单位向量（0, 1）。 */
        val UNIT_Y = IntVector2(0, 1)
        /** 单位向量（1, 1）。 */
        val ONE = IntVector2(1, 1)
    }
}

data class IntVector3(override val x: Int, override val y: Int, override val z: Int) : IVector3<Int>, Interpolable<IntVector3> {
    override fun interpolateTo(end: IntVector3, ratio: Ratio): IntVector3 = lerp(end, ratio)

    companion object {
        /** 零向量（0, 0, 0）。 */
        val ZERO = IntVector3(0, 0, 0)
        /** 单位向量（1, 0, 0）。 */
        val UNIT_X = IntVector3(1, 0, 0)
        /** 单位向量（0, 1, 0）。 */
        val UNIT_Y = IntVector3(0, 1, 0)
        /** 单位向量（0, 0, 1）。 */
        val UNIT_Z = IntVector3(0, 0, 1)
        /** 单位向量（1, 1, 1）。 */
        val ONE = IntVector3(1, 1, 1)
    }
}

data class IntVector4(override val x: Int, override val y: Int, override val z: Int, override val w: Int) : IVector4<Int>, Interpolable<IntVector4> {
    override fun interpolateTo(end: IntVector4, ratio: Ratio): IntVector4 = lerp(end, ratio)

    companion object{
        /** 零向量（0, 0, 0, 0）。 */
        val ZERO = IntVector4(0, 0, 0, 0)
        /** 单位向量（1, 1, 1, 1）。 */
        val ONE = IntVector4(1, 1, 1, 1)
    }
}

// Vector2 操作符扩展
operator fun Vector2.plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
operator fun Vector2.plus(other: Float): Vector2 = Vector2(x + other, y + other)
operator fun Float.plus(other: Vector2): Vector2 = Vector2(this + other.x, this + other.y)

operator fun Vector2.minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
operator fun Vector2.minus(other: Float): Vector2 = Vector2(x - other, y - other)
operator fun Float.minus(other: Vector2): Vector2 = Vector2(this - other.x, this - other.y)

operator fun Vector2.times(other: Vector2): Vector2 = Vector2(x * other.x, y * other.y)
operator fun Vector2.times(other: Float): Vector2 = Vector2(x * other, y * other)
operator fun Float.times(other: Vector2): Vector2 = Vector2(this * other.x, this * other.y)

operator fun Vector2.div(other: Vector2): Vector2 = Vector2(x / other.x, y / other.y)
operator fun Vector2.div(other: Float): Vector2 = Vector2(x / other, y / other)
operator fun Float.div(other: Vector2): Vector2 = Vector2(this / other.x, this / other.y)

operator fun Vector2.rem(other: Vector2): Vector2 = Vector2(x % other.x, y % other.y)
operator fun Vector2.rem(other: Float): Vector2 = Vector2(x % other, y % other)

operator fun Vector2.unaryMinus(): Vector2 = Vector2(-x, -y)
operator fun Vector2.unaryPlus(): Vector2 = Vector2(x, y)

// Vector3 操作符扩展
operator fun Vector3.plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
operator fun Vector3.plus(other: Float): Vector3 = Vector3(x + other, y + other, z + other)
operator fun Float.plus(other: Vector3): Vector3 = Vector3(this + other.x, this + other.y, this + other.z)

operator fun Vector3.minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)
operator fun Vector3.minus(other: Float): Vector3 = Vector3(x - other, y - other, z - other)
operator fun Float.minus(other: Vector3): Vector3 = Vector3(this - other.x, this - other.y, this - other.z)

operator fun Vector3.times(other: Vector3): Vector3 = Vector3(x * other.x, y * other.y, z * other.z)
operator fun Vector3.times(other: Float): Vector3 = Vector3(x * other, y * other, z * other)
operator fun Float.times(other: Vector3): Vector3 = Vector3(this * other.x, this * other.y, this * other.z)

operator fun Vector3.div(other: Vector3): Vector3 = Vector3(x / other.x, y / other.y, z / other.z)
operator fun Vector3.div(other: Float): Vector3 = Vector3(x / other, y / other, z / other)
operator fun Float.div(other: Vector3): Vector3 = Vector3(this / other.x, this / other.y, this / other.z)

operator fun Vector3.rem(other: Vector3): Vector3 = Vector3(x % other.x, y % other.y, z % other.z)
operator fun Vector3.rem(other: Float): Vector3 = Vector3(x % other, y % other, z % other)

operator fun Vector3.unaryMinus(): Vector3 = Vector3(-x, -y, -z)
operator fun Vector3.unaryPlus(): Vector3 = Vector3(x, y, z)

// Vector4 操作符扩展
operator fun Vector4.plus(other: Vector4): Vector4 = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun Vector4.plus(other: Float): Vector4 = Vector4(x + other, y + other, z + other, w + other)
operator fun Float.plus(other: Vector4): Vector4 = Vector4(this + other.x, this + other.y, this + other.z, this + other.w)

operator fun Vector4.minus(other: Vector4): Vector4 = Vector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun Vector4.minus(other: Float): Vector4 = Vector4(x - other, y - other, z - other, w - other)
operator fun Float.minus(other: Vector4): Vector4 = Vector4(this - other.x, this - other.y, this - other.z, this - other.w)

operator fun Vector4.times(other: Vector4): Vector4 = Vector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun Vector4.times(other: Float): Vector4 = Vector4(x * other, y * other, z * other, w * other)
operator fun Float.times(other: Vector4): Vector4 = Vector4(this * other.x, this * other.y, this * other.z, this * other.w)

operator fun Vector4.div(other: Vector4): Vector4 = Vector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun Vector4.div(other: Float): Vector4 = Vector4(x / other, y / other, z / other, w / other)
operator fun Float.div(other: Vector4): Vector4 = Vector4(this / other.x, this / other.y, this / other.z, this / other.w)

operator fun Vector4.rem(other: Vector4): Vector4 = Vector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun Vector4.rem(other: Float): Vector4 = Vector4(x % other, y % other, z % other, w % other)

operator fun Vector4.unaryMinus(): Vector4 = Vector4(-x, -y, -z, -w)
operator fun Vector4.unaryPlus(): Vector4 = Vector4(x, y, z, w)

// IntVector2 操作符扩展
operator fun IntVector2.plus(other: IntVector2): IntVector2 = IntVector2(x + other.x, y + other.y)
operator fun IntVector2.plus(other: Int): IntVector2 = IntVector2(x + other, y + other)
operator fun Int.plus(other: IntVector2): IntVector2 = IntVector2(this + other.x, this + other.y)

operator fun IntVector2.minus(other: IntVector2): IntVector2 = IntVector2(x - other.x, y - other.y)
operator fun IntVector2.minus(other: Int): IntVector2 = IntVector2(x - other, y - other)
operator fun Int.minus(other: IntVector2): IntVector2 = IntVector2(this - other.x, this - other.y)

operator fun IntVector2.times(other: IntVector2): IntVector2 = IntVector2(x * other.x, y * other.y)
operator fun IntVector2.times(other: Int): IntVector2 = IntVector2(x * other, y * other)
operator fun Int.times(other: IntVector2): IntVector2 = IntVector2(this * other.x, this * other.y)

operator fun IntVector2.div(other: IntVector2): IntVector2 = IntVector2(x / other.x, y / other.y)
operator fun IntVector2.div(other: Int): IntVector2 = IntVector2(x / other, y / other)
operator fun Int.div(other: IntVector2): IntVector2 = IntVector2(this / other.x, this / other.y)

operator fun IntVector2.rem(other: IntVector2): IntVector2 = IntVector2(x % other.x, y % other.y)
operator fun IntVector2.rem(other: Int): IntVector2 = IntVector2(x % other, y % other)

operator fun IntVector2.unaryMinus(): IntVector2 = IntVector2(-x, -y)
operator fun IntVector2.unaryPlus(): IntVector2 = IntVector2(x, y)

// IntVector3 操作符扩展
operator fun IntVector3.plus(other: IntVector3): IntVector3 = IntVector3(x + other.x, y + other.y, z + other.z)
operator fun IntVector3.plus(other: Int): IntVector3 = IntVector3(x + other, y + other, z + other)
operator fun Int.plus(other: IntVector3): IntVector3 = IntVector3(this + other.x, this + other.y, this + other.z)

operator fun IntVector3.minus(other: IntVector3): IntVector3 = IntVector3(x - other.x, y - other.y, z - other.z)
operator fun IntVector3.minus(other: Int): IntVector3 = IntVector3(x - other, y - other, z - other)
operator fun Int.minus(other: IntVector3): IntVector3 = IntVector3(this - other.x, this - other.y, this - other.z)

operator fun IntVector3.times(other: IntVector3): IntVector3 = IntVector3(x * other.x, y * other.y, z * other.z)
operator fun IntVector3.times(other: Int): IntVector3 = IntVector3(x * other, y * other, z * other)
operator fun Int.times(other: IntVector3): IntVector3 = IntVector3(this * other.x, this * other.y, this * other.z)

operator fun IntVector3.div(other: IntVector3): IntVector3 = IntVector3(x / other.x, y / other.y, z / other.z)
operator fun IntVector3.div(other: Int): IntVector3 = IntVector3(x / other, y / other, z / other)
operator fun Int.div(other: IntVector3): IntVector3 = IntVector3(this / other.x, this / other.y, this / other.z)

operator fun IntVector3.rem(other: IntVector3): IntVector3 = IntVector3(x % other.x, y % other.y, z % other.z)
operator fun IntVector3.rem(other: Int): IntVector3 = IntVector3(x % other, y % other, z % other)

operator fun IntVector3.unaryMinus(): IntVector3 = IntVector3(-x, -y, -z)
operator fun IntVector3.unaryPlus(): IntVector3 = IntVector3(x, y, z)

// IntVector4 操作符扩展
operator fun IntVector4.plus(other: IntVector4): IntVector4 = IntVector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun IntVector4.plus(other: Int): IntVector4 = IntVector4(x + other, y + other, z + other, w + other)
operator fun Int.plus(other: IntVector4): IntVector4 = IntVector4(this + other.x, this + other.y, this + other.z, this + other.w)

operator fun IntVector4.minus(other: IntVector4): IntVector4 = IntVector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun IntVector4.minus(other: Int): IntVector4 = IntVector4(x - other, y - other, z - other, w - other)
operator fun Int.minus(other: IntVector4): IntVector4 = IntVector4(this - other.x, this - other.y, this - other.z, this - other.w)

operator fun IntVector4.times(other: IntVector4): IntVector4 = IntVector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun IntVector4.times(other: Int): IntVector4 = IntVector4(x * other, y * other, z * other, w * other)
operator fun Int.times(other: IntVector4): IntVector4 = IntVector4(this * other.x, this * other.y, this * other.z, this * other.w)

operator fun IntVector4.div(other: IntVector4): IntVector4 = IntVector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun IntVector4.div(other: Int): IntVector4 = IntVector4(x / other, y / other, z / other, w / other)
operator fun Int.div(other: IntVector4): IntVector4 = IntVector4(this / other.x, this / other.y, this / other.z, this / other.w)

operator fun IntVector4.rem(other: IntVector4): IntVector4 = IntVector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun IntVector4.rem(other: Int): IntVector4 = IntVector4(x % other, y % other, z % other, w % other)

operator fun IntVector4.unaryMinus(): IntVector4 = IntVector4(-x, -y, -z, -w)
operator fun IntVector4.unaryPlus(): IntVector4 = IntVector4(x, y, z, w)

// ===== 常用向量扩展：Float 类型 =====
// Vector2
/** 向量长度（欧氏范数）。 */
val Vector2.length: Float get() = hypot(x, y)

/** 向量长度平方，避免开根号开销。 */
val Vector2.lengthSquared: Float get() = x * x + y * y

/** 单位化向量（长度归一化）。长度为 0 时返回自身。 */
val Vector2.normalized: Vector2
    get() {
        val len = length
        return if (len > 0f) Vector2(x / len, y / len) else this
    }

/** 点积：衡量两个向量的相似程度。 */
fun Vector2.dot(other: Vector2): Float = x * other.x + y * other.y

/** 叉积（2D 标量形式）：正负表示旋转方向，数值与面积相关。 */
fun Vector2.cross(other: Vector2): Float = x * other.y - y * other.x

/** 两点之间的距离。 */
fun Vector2.distanceTo(other: Vector2): Float = (this - other).length

/** 向量与 x 轴的夹角，范围为 `(-π, π]`。 */
val Vector2.angle: Angle get() = atan2(y, x).toAngleRadians()

/**
 * 计算当前向量到 `other` 的夹角。
 * 返回值使用弧度，调用方可再转为 `Angle`。
 */
fun Vector2.angleTo(other: Vector2): Angle = atan2(cross(other), dot(other)).toAngleRadians()

/** 按比例对两个 2D 向量进行线性插值。 */
fun Vector2.lerp(to: Vector2, ratio: Ratio): Vector2 = Vector2(x.lerp(to.x, ratio), y.lerp(to.y, ratio))

/** 将向量按给定角度旋转。 */
fun Vector2.rotate(angle: Angle): Vector2 {
    val c = angle.cos
    val s = angle.sin
    return Vector2(x * c - y * s, x * s + y * c)
}

/** 与当前向量垂直的向量（逆时针旋转 90°）。 */
val Vector2.perpendicular: Vector2 get() = Vector2(-y, x)

/** 将当前向量投影到 `onto` 上。`onto` 为零向量时返回零。 */
fun Vector2.project(onto: Vector2): Vector2 {
    val denom = onto.lengthSquared
    if (denom == 0f) return Vector2(0f, 0f)
    val scale = dot(onto) / denom
    return Vector2(onto.x * scale, onto.y * scale)
}

/** 按法线 `normal` 进行镜像反射。 */
fun Vector2.reflect(normal: Vector2): Vector2 {
    val n = normal.normalized
    val factor = 2f * dot(n)
    return Vector2(x - factor * n.x, y - factor * n.y)
}

// 范围与比较：clamp/coerceIn/min/max/isZero/nearEquals
fun Vector2.coerceIn(minVec: Vector2, maxVec: Vector2): Vector2 = Vector2(x.coerceIn(minVec.x, maxVec.x), y.coerceIn(minVec.y, maxVec.y))
fun Vector2.clamp(min: Float, max: Float): Vector2 = Vector2(x.coerceIn(min, max), y.coerceIn(min, max))
fun Vector2.min(other: Vector2): Vector2 = Vector2(min(x, other.x), min(y, other.y))
fun Vector2.max(other: Vector2): Vector2 = Vector2(max(x, other.x), max(y, other.y))
fun Vector2.isZero(epsilon: Float = 1e-6f): Boolean = x.inRange(-epsilon, epsilon) && y.inRange(-epsilon, epsilon)
fun Vector2.nearEquals(other: Vector2, epsilon: Float = 1e-6f): Boolean = (x - other.x).inRange(-epsilon, epsilon) && (y - other.y).inRange(-epsilon, epsilon)

// Vector3
/** 向量长度（欧氏范数）。 */
val Vector3.length: Float get() = sqrt(x * x + y * y + z * z)

/** 向量长度平方，避免开根号开销。 */
val Vector3.lengthSquared: Float get() = x * x + y * y + z * z

/** 单位化向量（长度归一化）。长度为 0 时返回自身。 */
val Vector3.normalized: Vector3
    get() {
        val len = length
        return if (len > 0f) Vector3(x / len, y / len, z / len) else this
    }

/** 点积。 */
fun Vector3.dot(other: Vector3): Float = x * other.x + y * other.y + z * other.z

/** 叉积（右手坐标系）。 */
fun Vector3.cross(other: Vector3): Vector3 =
    Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

/** 距离与夹角、投影与反射等常用函数。 */
fun Vector3.distanceTo(other: Vector3): Float = (this - other).length
fun Vector3.angleTo(other: Vector3): Angle {
    val denom = (length * other.length)
    if (denom == 0f) return Angle.ZERO
    return acos((dot(other) / denom).coerceIn(-1f, 1f)).toAngleRadians()
}

fun Vector3.lerp(to: Vector3, ratio: Ratio): Vector3 =
    Vector3(x.lerp(to.x, ratio.value), y.lerp(to.y, ratio.value), z.lerp(to.z, ratio.value))

fun Vector3.project(onto: Vector3): Vector3 {
    val denom = onto.lengthSquared
    if (denom == 0f) return Vector3(0f, 0f, 0f)
    val scale = dot(onto) / denom
    return Vector3(onto.x * scale, onto.y * scale, onto.z * scale)
}

fun Vector3.reflect(normal: Vector3): Vector3 {
    val n = normal.normalized
    val factor = 2f * dot(n)
    return Vector3(x - factor * n.x, y - factor * n.y, z - factor * n.z)
}

fun Vector3.coerceIn(minVec: Vector3, maxVec: Vector3): Vector3 =
    Vector3(
        x.coerceIn(minVec.x, maxVec.x),
        y.coerceIn(minVec.y, maxVec.y),
        z.coerceIn(minVec.z, maxVec.z)
    )

fun Vector3.clamp(min: Float, max: Float): Vector3 =
    Vector3(x.coerceIn(min, max), y.coerceIn(min, max), z.coerceIn(min, max))

fun Vector3.min(other: Vector3): Vector3 =
    Vector3(
        min(x, other.x),
        min(y, other.y),
        min(z, other.z)
    )

fun Vector3.max(other: Vector3): Vector3 =
    Vector3(
        max(x, other.x),
        max(y, other.y),
        max(z, other.z)
    )

fun Vector3.isZero(epsilon: Float = 1e-6f): Boolean =
    x.inRange(-epsilon, epsilon) && y.inRange(-epsilon, epsilon) && z.inRange(-epsilon, epsilon)

fun Vector3.nearEquals(other: Vector3, epsilon: Float = 1e-6f): Boolean =
    (x - other.x).inRange(-epsilon, epsilon) &&
            (y - other.y).inRange(-epsilon, epsilon) &&
            (z - other.z).inRange(-epsilon, epsilon)

// Vector4
/** 向量长度（欧氏范数）。 */
val Vector4.length: Float get() = sqrt(x * x + y * y + z * z + w * w)

/** 向量长度平方。 */
val Vector4.lengthSquared: Float get() = x * x + y * y + z * z + w * w

/** 单位化向量（长度归一化）。长度为 0 时返回自身。 */
val Vector4.normalized: Vector4
    get() {
        val len = length
        return if (len > 0f) Vector4(x / len, y / len, z / len, w / len) else this
    }

/** 点积与插值、夹取等常用函数。 */
fun Vector4.dot(other: Vector4): Float = x * other.x + y * other.y + z * other.z + w * other.w
fun Vector4.lerp(to: Vector4, ratio: Ratio): Vector4 =
    Vector4(x.lerp(to.x, ratio.value), y.lerp(to.y, ratio.value), z.lerp(to.z, ratio.value), w.lerp(to.w, ratio.value))

fun Vector4.clamp(min: Float, max: Float): Vector4 =
    Vector4(x.coerceIn(min, max), y.coerceIn(min, max), z.coerceIn(min, max), w.coerceIn(min, max))

fun Vector4.coerceIn(minVec: Vector4, maxVec: Vector4): Vector4 =
    Vector4(
        x.coerceIn(minVec.x, maxVec.x),
        y.coerceIn(minVec.y, maxVec.y),
        z.coerceIn(minVec.z, maxVec.z),
        w.coerceIn(minVec.w, maxVec.w)
    )

fun Vector4.min(other: Vector4): Vector4 =
    Vector4(
        min(x, other.x),
        min(y, other.y),
        min(z, other.z),
        min(w, other.w)
    )

fun Vector4.max(other: Vector4): Vector4 =
    Vector4(
        max(x, other.x),
        max(y, other.y),
        max(z, other.z),
        max(w, other.w)
    )

fun Vector4.isZero(epsilon: Float = 1e-6f): Boolean =
    x.inRange(-epsilon, epsilon) && y.inRange(-epsilon, epsilon) && z.inRange(-epsilon, epsilon) && w.inRange(-epsilon, epsilon)

fun Vector4.nearEquals(other: Vector4, epsilon: Float = 1e-6f): Boolean =
    (x - other.x).inRange(-epsilon, epsilon) &&
            (y - other.y).inRange(-epsilon, epsilon) &&
            (z - other.z).inRange(-epsilon, epsilon) &&
            (w - other.w).inRange(-epsilon, epsilon)

// ===== 常用向量扩展：Int 类型 =====
/** 将 `IntVector2/3/4` 转换为浮点向量，便于参与几何计算。 */
fun IntVector2.toVector2(): Vector2 = Vector2(x.toFloat(), y.toFloat())
fun IntVector3.toVector3(): Vector3 = Vector3(x.toFloat(), y.toFloat(), z.toFloat())
fun IntVector4.toVector4(): Vector4 = Vector4(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

// 长度与距离（Int）
val IntVector2.length: Float get() = sqrt((x * x + y * y).toFloat())
val IntVector2.lengthSquared: Int get() = x * x + y * y
fun IntVector2.distanceTo(other: IntVector2): Float = (this - other).length

val IntVector3.length: Float get() = sqrt((x * x + y * y + z * z).toFloat())
val IntVector3.lengthSquared: Int get() = x * x + y * y + z * z
fun IntVector3.distanceTo(other: IntVector3): Float = (this - other).length

val IntVector4.length: Float get() = sqrt((x * x + y * y + z * z + w * w).toFloat())
val IntVector4.lengthSquared: Int get() = x * x + y * y + z * z + w * w
fun IntVector4.distanceTo(other: IntVector4): Float = (this - other).length

// 夹取与分量比较（Int）
// - `coerceIn`：按向量的每个分量夹取到给定范围。
// - `min/max`：按分量比较选取最小/最大值。
// - `lerp`：整数插值由库函数处理。
fun IntVector2.coerceIn(minVec: IntVector2, maxVec: IntVector2): IntVector2 =
    IntVector2(x.coerceIn(minVec.x, maxVec.x), y.coerceIn(minVec.y, maxVec.y))

fun IntVector3.coerceIn(minVec: IntVector3, maxVec: IntVector3): IntVector3 =
    IntVector3(x.coerceIn(minVec.x, maxVec.x), y.coerceIn(minVec.y, maxVec.y), z.coerceIn(minVec.z, maxVec.z))

fun IntVector4.coerceIn(minVec: IntVector4, maxVec: IntVector4): IntVector4 =
    IntVector4(
        x.coerceIn(minVec.x, maxVec.x),
        y.coerceIn(minVec.y, maxVec.y),
        z.coerceIn(minVec.z, maxVec.z),
        w.coerceIn(minVec.w, maxVec.w)
    )

fun IntVector2.lerp(to: IntVector2, ratio: Ratio): IntVector2 = IntVector2(x.lerp(to.x, ratio), y.lerp(to.y, ratio))
fun IntVector3.lerp(to: IntVector3, ratio: Ratio): IntVector3 = IntVector3(x.lerp(to.x, ratio), y.lerp(to.y, ratio), z.lerp(to.z, ratio))
fun IntVector4.lerp(to: IntVector4, ratio: Ratio): IntVector4 = IntVector4(x.lerp(to.x, ratio), y.lerp(to.y, ratio), z.lerp(to.z, ratio), w.lerp(to.w, ratio))

fun IntVector2.min(other: IntVector2): IntVector2 = IntVector2(min(x, other.x), min(y, other.y))
fun IntVector2.max(other: IntVector2): IntVector2 = IntVector2(max(x, other.x), max(y, other.y))
fun IntVector3.min(other: IntVector3): IntVector3 = IntVector3(min(x, other.x), min(y, other.y), min(z, other.z))
fun IntVector3.max(other: IntVector3): IntVector3 = IntVector3(max(x, other.x), max(y, other.y), max(z, other.z))
fun IntVector4.min(other: IntVector4): IntVector4 = IntVector4(min(x, other.x), min(y, other.y), min(z, other.z), min(w, other.w))
fun IntVector4.max(other: IntVector4): IntVector4 = IntVector4(max(x, other.x), max(y, other.y), max(z, other.z), max(w, other.w))