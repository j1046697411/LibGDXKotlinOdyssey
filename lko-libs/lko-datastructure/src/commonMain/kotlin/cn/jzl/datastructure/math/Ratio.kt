package cn.jzl.datastructure.math

/**
 * Ratio 表示一个以 `Float` 存储的比例/权重/进度等数值的轻量封装。
 *
 * - 存储类型：`Float`，属性为 `value`
 * - 常见用途：表达比例、权重、进度等，常见范围为 `[0f, 1f]`（但本类不强制限制范围）
 * - 算术支持：与 `Ratio` 及 `Float/Int/Double/Long` 的双向 `+ - * /` 运算（包含左/右操作数形式）
 * - 工厂与常量：`toRatio()` 扩展与 `ZERO_RATIO / ONE_RATIO / HALF_RATIO`
 *
 * 示例：
 * ```kotlin
 * val r = 0.25f.toRatio()
 * val sum = HALF_RATIO + r            // 0.75
 * val w = 2 * r                       // 0.5
 * val q = (1 - r) / HALF_RATIO        // 1.5
 * ```
 *
 * 注意：
 * - 本类型不限制取值范围，也不进行溢出或除零保护；传入非法值会产生与 `Float` 相同的语义（如 `NaN/Infinity`）。
 * - 若需约束范围，可在调用处使用 `coerceIn(0f, 1f)` 或其他策略。
 */
@JvmInline
value class Ratio(val value: Float)

// 加法操作符
operator fun Ratio.plus(other: Ratio): Ratio = Ratio(this.value + other.value)
operator fun Ratio.plus(other: Float): Ratio = Ratio(this.value + other)
operator fun Ratio.plus(other: Int): Ratio = Ratio(this.value + other.toFloat())
operator fun Ratio.plus(other: Double): Ratio = Ratio(this.value + other.toFloat())
operator fun Ratio.plus(other: Long): Ratio = Ratio(this.value + other.toFloat())

// 减法操作符
operator fun Ratio.minus(other: Ratio): Ratio = Ratio(this.value - other.value)
operator fun Ratio.minus(other: Float): Ratio = Ratio(this.value - other)
operator fun Ratio.minus(other: Int): Ratio = Ratio(this.value - other.toFloat())
operator fun Ratio.minus(other: Double): Ratio = Ratio(this.value - other.toFloat())
operator fun Ratio.minus(other: Long): Ratio = Ratio(this.value - other.toFloat())

// 乘法操作符
operator fun Ratio.times(other: Ratio): Ratio = Ratio(this.value * other.value)
operator fun Ratio.times(other: Float): Ratio = Ratio(this.value * other)
operator fun Ratio.times(other: Int): Ratio = Ratio(this.value * other.toFloat())
operator fun Ratio.times(other: Double): Ratio = Ratio(this.value * other.toFloat())
operator fun Ratio.times(other: Long): Ratio = Ratio(this.value * other.toFloat())

// 除法操作符
operator fun Ratio.div(other: Ratio): Ratio = Ratio(this.value / other.value)
operator fun Ratio.div(other: Float): Ratio = Ratio(this.value / other)
operator fun Ratio.div(other: Int): Ratio = Ratio(this.value / other.toFloat())
operator fun Ratio.div(other: Double): Ratio = Ratio(this.value / other.toFloat())
operator fun Ratio.div(other: Long): Ratio = Ratio(this.value / other.toFloat())

// 零检查和空安全操作
val Ratio.isZero: Boolean get() = this.value == 0f
val Ratio.isPositive: Boolean get() = this.value > 0f
val Ratio.isNegative: Boolean get() = this.value < 0f

// 反向操作符（Double作为左操作数）
operator fun Double.plus(other: Ratio): Ratio = Ratio(this.toFloat() + other.value)
operator fun Double.minus(other: Ratio): Ratio = Ratio(this.toFloat() - other.value)
operator fun Double.times(other: Ratio): Ratio = Ratio(this.toFloat() * other.value)
operator fun Double.div(other: Ratio): Ratio = Ratio(this.toFloat() / other.value)

// 反向操作符（Float作为左操作数）
operator fun Float.plus(other: Ratio): Ratio = Ratio(this + other.value)
operator fun Float.minus(other: Ratio): Ratio = Ratio(this - other.value)
operator fun Float.times(other: Ratio): Ratio = Ratio(this * other.value)
operator fun Float.div(other: Ratio): Ratio = Ratio(this / other.value)

// 反向操作符（Int作为左操作数）
operator fun Int.plus(other: Ratio): Ratio = Ratio(this.toFloat() + other.value)
operator fun Int.minus(other: Ratio): Ratio = Ratio(this.toFloat() - other.value)
operator fun Int.times(other: Ratio): Ratio = Ratio(this.toFloat() * other.value)
operator fun Int.div(other: Ratio): Ratio = Ratio(this.toFloat() / other.value)

// 反向操作符（Long作为左操作数）
operator fun Long.plus(other: Ratio): Ratio = Ratio(this.toFloat() + other.value)
operator fun Long.minus(other: Ratio): Ratio = Ratio(this.toFloat() - other.value)
operator fun Long.times(other: Ratio): Ratio = Ratio(this.toFloat() * other.value)
operator fun Long.div(other: Ratio): Ratio = Ratio(this.toFloat() / other.value)

// 工厂方法
fun Float.toRatio(): Ratio = Ratio(this)
fun Int.toRatio(): Ratio = Ratio(this.toFloat())
fun Double.toRatio(): Ratio = Ratio(this.toFloat())
fun Long.toRatio(): Ratio = Ratio(this.toFloat())

// 创建常量Ratio
val ZERO_RATIO: Ratio = 0f.toRatio()
val ONE_RATIO: Ratio = 1f.toRatio()
val HALF_RATIO: Ratio = 0.5f.toRatio()

// 约束到区间
fun Ratio.coerceIn(min: Ratio, max: Ratio): Ratio {
    require(min.value <= max.value) { "Cannot coerce to an empty range: min(${min.value}) > max(${max.value})." }
    val v = this.value
    return when {
        v < min.value -> min
        v > max.value -> max
        else -> this
    }
}

fun Ratio.coerceIn(min: Float, max: Float): Ratio {
    require(min <= max) { "Cannot coerce to an empty range: min($min) > max($max)." }
    return Ratio(this.value.coerceIn(min, max))
}

fun Ratio.coerceIn(range: ClosedFloatingPointRange<Float>): Ratio {
    require(range.start <= range.endInclusive) { "Cannot coerce to an empty range: start(${range.start}) > end(${range.endInclusive})." }
    return Ratio(this.value.coerceIn(range))
}

fun Ratio.coerceIn(min: Double, max: Double): Ratio {
    require(min <= max) { "Cannot coerce to an empty range: min($min) > max($max)." }
    val v = this.value.toDouble().coerceIn(min, max).toFloat()
    return Ratio(v)
}

@JvmName("coerceInDoubleRange")
fun Ratio.coerceIn(range: ClosedFloatingPointRange<Double>): Ratio {
    require(range.start <= range.endInclusive) { "Cannot coerce to an empty range: start(${range.start}) > end(${range.endInclusive})." }
    val v = this.value.toDouble().coerceIn(range).toFloat()
    return Ratio(v)
}