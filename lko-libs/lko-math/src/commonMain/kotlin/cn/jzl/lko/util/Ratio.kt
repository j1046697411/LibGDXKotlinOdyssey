package cn.jzl.lko.util

@JvmInline
value class Ratio(val value: Float) {
    companion object {
        val ZERO = Ratio(0f)
        val QUARTER = Ratio(0.25f)
        val HALF = Ratio(0.5f)
        val THREE_QUARTER = Ratio(0.75f)
        val ONE = Ratio(1f)
        val NAN = Ratio(Float.NaN)
    }
}

inline val Float.ratio: Ratio get() = Ratio(this)
inline val Double.ratio: Ratio get() = Ratio(this.toFloat())

operator fun Ratio.plus(other: Ratio): Ratio = Ratio(value + other.value)
operator fun Ratio.minus(other: Ratio): Ratio = Ratio(value - other.value)
operator fun Ratio.times(other: Ratio): Ratio = Ratio(value * other.value)
operator fun Ratio.div(other: Ratio): Ratio = Ratio(value / other.value)

operator fun Ratio.plus(other: Float): Ratio = Ratio(value + other)
operator fun Ratio.minus(other: Float): Ratio = Ratio(value - other)
operator fun Ratio.times(other: Float): Ratio = Ratio(value * other)
operator fun Ratio.div(other: Float): Ratio = Ratio(value / other)

operator fun Float.plus(other: Ratio): Ratio = Ratio(this + other.value)
operator fun Float.minus(other: Ratio): Ratio = Ratio(this - other.value)
operator fun Float.times(other: Ratio): Ratio = Ratio(this * other.value)
operator fun Float.div(other: Ratio): Ratio = Ratio(this / other.value)
