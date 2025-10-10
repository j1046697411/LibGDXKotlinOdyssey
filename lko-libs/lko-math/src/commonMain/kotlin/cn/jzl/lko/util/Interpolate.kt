package cn.jzl.lko.util


fun interface Interpolator<T> {
    fun interpolate(start: T, end: T, ratio: Ratio): T
}

fun interface Interpolable<T> {
    fun interpolateTo(end: T, ratio: Ratio): T
}

fun interface Easing {
    operator fun invoke(ratio: Ratio): Ratio

    object Linear : Easing {
        override operator fun invoke(ratio: Ratio): Ratio = ratio
    }
}

fun Ratio.ease(easing: Easing): Ratio = easing(this)
fun <T> Ratio.interpolate(start: T, end: T, interpolator: Interpolator<T>): T = interpolator.interpolate(start, end, this)
fun <T : Interpolable<T>> Ratio.interpolate(start: T, end: T): T = start.interpolateTo(end, this)
fun Ratio.interpolate(start: Float, end: Float): Float = (end - start) * value + start
fun Ratio.interpolate(start: Double, end: Double): Double = (end - start) * value + start
fun Ratio.interpolate(start: Int, end: Int): Int = ((end - start) * value).toInt() + start
fun Ratio.interpolate(start: Long, end: Long): Long = ((end - start) * value).toLong() + start