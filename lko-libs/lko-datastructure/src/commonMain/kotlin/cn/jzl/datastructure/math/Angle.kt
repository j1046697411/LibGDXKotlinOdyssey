package cn.jzl.datastructure.math

import kotlin.math.*
import kotlin.jvm.JvmInline

/**
 * Angle 表示以弧度为单位的角度轻量封装。
 * - 存储单位：`radians`（Float）
 * - 常用范围：任意实数；提供规范化到 `[0, 2π)` 与 `(-π, π]` 的方法
 * - 互操作：支持与 Angle 以及 Double/Float/Int/Long 的算术与求余运算
 */
@JvmInline
value class Angle(val radians: Float) : Interpolable<Angle> {
    override fun interpolateTo(end: Angle, ratio: Ratio): Angle = lerp(end, ratio)

    companion object {
        val ZERO: Angle = Angle(0f)
        val PI: Angle = Angle(3.1415925f)
        val HALF_PI: Angle = PI / 2f
        val TWO_PI: Angle = PI * 2f
    }
}

/**
 * 与 Angle 的算术/求余。
 * 返回新 Angle（弧度相加、相减、取模）。
 */
operator fun Angle.plus(other: Angle): Angle = Angle(this.radians + other.radians)
operator fun Angle.minus(other: Angle): Angle = Angle(this.radians - other.radians)
operator fun Angle.rem(other: Angle): Angle = Angle(this.radians % other.radians)

/** 与 Float 的算术与求余（返回 Angle）。 */
operator fun Angle.plus(v: Float): Angle = Angle(this.radians + v)
operator fun Angle.minus(v: Float): Angle = Angle(this.radians - v)
operator fun Angle.times(v: Float): Angle = Angle(this.radians * v)
operator fun Angle.div(v: Float): Angle = Angle(this.radians / v)
operator fun Angle.rem(v: Float): Angle = Angle(this.radians % v)

/** 与 Double 的算术与求余（返回 Angle）。 */
operator fun Angle.plus(v: Double): Angle = Angle((this.radians + v).toFloat())
operator fun Angle.minus(v: Double): Angle = Angle((this.radians - v).toFloat())
operator fun Angle.times(v: Double): Angle = Angle((this.radians * v).toFloat())
operator fun Angle.div(v: Double): Angle = Angle((this.radians / v).toFloat())
operator fun Angle.rem(v: Double): Angle = Angle((this.radians.toDouble() % v).toFloat())

/** 与 Int 的算术与求余（返回 Angle）。 */
operator fun Angle.plus(v: Int): Angle = Angle(this.radians + v)
operator fun Angle.minus(v: Int): Angle = Angle(this.radians - v)
operator fun Angle.times(v: Int): Angle = Angle(this.radians * v)
operator fun Angle.div(v: Int): Angle = Angle(this.radians / v)
operator fun Angle.rem(v: Int): Angle = Angle(this.radians % v)

/** 与 Long 的算术与求余（返回 Angle）。 */
operator fun Angle.plus(v: Long): Angle = Angle(this.radians + v.toFloat())
operator fun Angle.minus(v: Long): Angle = Angle(this.radians - v.toFloat())
operator fun Angle.times(v: Long): Angle = Angle(this.radians * v.toFloat())
operator fun Angle.div(v: Long): Angle = Angle(this.radians / v.toFloat())
operator fun Angle.rem(v: Long): Angle = Angle(this.radians % v.toFloat())

/** 反向操作符：Double 作为左操作数与 Angle 的算术/求余（返回 Angle）。 */
operator fun Double.plus(other: Angle): Angle = Angle(this.toFloat() + other.radians)
operator fun Double.minus(other: Angle): Angle = Angle(this.toFloat() - other.radians)
operator fun Double.times(other: Angle): Angle = Angle(this.toFloat() * other.radians)
operator fun Double.div(other: Angle): Angle = Angle(this.toFloat() / other.radians)
operator fun Double.rem(other: Angle): Angle = Angle((this % other.radians).toFloat())

/** 反向操作符：Float 作为左操作数与 Angle 的算术/求余（返回 Angle）。 */
operator fun Float.plus(other: Angle): Angle = Angle(this + other.radians)
operator fun Float.minus(other: Angle): Angle = Angle(this - other.radians)
operator fun Float.times(other: Angle): Angle = Angle(this * other.radians)
operator fun Float.div(other: Angle): Angle = Angle(this / other.radians)
operator fun Float.rem(other: Angle): Angle = Angle(this % other.radians)

/** 反向操作符：Int 作为左操作数与 Angle 的算术/求余（返回 Angle）。 */
operator fun Int.plus(other: Angle): Angle = Angle(this.toFloat() + other.radians)
operator fun Int.minus(other: Angle): Angle = Angle(this.toFloat() - other.radians)
operator fun Int.times(other: Angle): Angle = Angle(this.toFloat() * other.radians)
operator fun Int.div(other: Angle): Angle = Angle(this.toFloat() / other.radians)
operator fun Int.rem(other: Angle): Angle = Angle((this % other.radians).toFloat())

/** 反向操作符：Long 作为左操作数与 Angle 的算术/求余（返回 Angle）。 */
operator fun Long.plus(other: Angle): Angle = Angle(this.toFloat() + other.radians)
operator fun Long.minus(other: Angle): Angle = Angle(this.toFloat() - other.radians)
operator fun Long.times(other: Angle): Angle = Angle(this.toFloat() * other.radians)
operator fun Long.div(other: Angle): Angle = Angle(this.toFloat() / other.radians)
operator fun Long.rem(other: Angle): Angle = Angle((this % other.radians).toFloat())

// 常用扩展属性与方法
/** 将弧度转换为度数（`radians * 180 / π`）。 */
val Angle.degrees: Float get() = this.radians * 180f / PI.toFloat()

/** 正弦：`sin(radians)`。 */
val Angle.sin: Float get() = sin(this.radians)

/** 余弦：`cos(radians)`。 */
val Angle.cos: Float get() = cos(this.radians)

/** 正切：`tan(radians)`。 */
val Angle.tan: Float get() = tan(this.radians)

/** 绝对值角：返回 `|radians|` 对应的 Angle。 */
val Angle.abs: Angle get() = Angle(abs(this.radians))

/** 判断角度接近 0：`abs(radians) <= eps`。 */
fun Angle.isZero(eps: Float = 1e-6f): Boolean = abs(this.radians) <= eps

/** 判断两个角度近似相等：`abs(this - other) <= eps`。 */
fun Angle.nearEquals(other: Angle, eps: Float = 1e-6f): Boolean = abs(this.radians - other.radians) <= eps

/** 规范化到 `[0, 2π)`。 */
fun Angle.normalizedPositive(): Angle {
    val twoPi = (2 * PI).toFloat()
    var r = this.radians % twoPi
    if (r < 0f) r += twoPi
    return Angle(r)
}

/** 规范化到 `(-π, π]`（最短有符号范围）。 */
fun Angle.normalizedSigned(): Angle {
    val pi = PI.toFloat()
    val twoPi = (2 * PI).toFloat()
    var r = this.radians % twoPi
    if (r > pi) r -= twoPi
    if (r <= -pi) r += twoPi
    return Angle(r)
}

/**
 * 返回最短有符号角差：`(other - this)` 并规范化到 `(-π, π]`。
 */
fun Angle.deltaTo(other: Angle): Angle = (other - this).normalizedSigned()

/**
 * 将角度包裹到 `[min, max)` 范围内（循环区间）。
 * 要求：`min.radians <= max.radians`。
 */
fun Angle.wrap(min: Angle, max: Angle): Angle {
    require(min.radians <= max.radians) { "Cannot wrap to an empty range: min(${min.radians}) > max(${max.radians})." }
    val range = max.radians - min.radians
    var r = this.radians
    r = ((r - min.radians) % range + range) % range + min.radians
    return Angle(r)
}

/**
 * 将角度钳制到 `[min, max]` 范围内（闭区间）。
 * 要求：`min.radians <= max.radians`。
 */
fun Angle.coerceIn(min: Angle, max: Angle): Angle {
    require(min.radians <= max.radians) { "Cannot coerce to an empty range: min(${min.radians}) > max(${max.radians})." }
    val r = this.radians
    return when {
        r < min.radians -> min
        r > max.radians -> max
        else -> this
    }
}

fun Angle.lerp(to: Angle, t: Ratio): Angle = Angle(radians.lerp(to.radians, t))


/** 工厂方法：从弧度创建 Angle。 */
fun Float.toAngleRadians(): Angle = Angle(this)

/** 工厂方法：从弧度创建 Angle。 */
fun Double.toAngleRadians(): Angle = Angle(this.toFloat())

/** 工厂方法：从弧度创建 Angle。 */
fun Int.toAngleRadians(): Angle = Angle(this.toFloat())

/** 工厂方法：从弧度创建 Angle。 */
fun Long.toAngleRadians(): Angle = Angle(this.toFloat())

/** 工厂方法：从度数创建 Angle（自动转换为弧度）。 */
fun Float.toAngleDegrees(): Angle = Angle(this * PI.toFloat() / 180f)

/** 工厂方法：从度数创建 Angle（自动转换为弧度）。 */
fun Double.toAngleDegrees(): Angle = Angle((this * PI / 180.0).toFloat())

/** 工厂方法：从度数创建 Angle（自动转换为弧度）。 */
fun Int.toAngleDegrees(): Angle = Angle(this.toFloat() * PI.toFloat() / 180f)

/** 工厂方法：从度数创建 Angle（自动转换为弧度）。 */
fun Long.toAngleDegrees(): Angle = Angle(this.toFloat() * PI.toFloat() / 180f)