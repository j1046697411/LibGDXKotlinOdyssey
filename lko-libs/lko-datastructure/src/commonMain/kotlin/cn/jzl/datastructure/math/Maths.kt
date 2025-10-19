@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.datastructure.math

inline fun Int.inRange(min: Int, max: Int): Boolean = this >= min && this <= max
inline fun Long.inRange(min: Long, max: Long): Boolean = this >= min && this <= max
inline fun Float.inRange(min: Float, max: Float): Boolean = this >= min && this <= max
inline fun Double.inRange(min: Double, max: Double): Boolean = this >= min && this <= max

inline fun Float.lerp(to: Float, t: Float) : Float = (to - this) * t + this
inline fun Double.lerp(to: Double, t: Double) : Double = (to - this) * t + this
inline fun Float.lerp(to: Float, t: Ratio) : Float = (to - this) * t.value + this
inline fun Double.lerp(to: Double, t: Ratio) : Double = (to - this) * t.value + this
inline fun Int.lerp(to: Int, t: Ratio) : Int = ((to - this) * t.value).toInt() + this
inline fun Long.lerp(to: Long, t: Ratio) : Long = ((to - this) * t.value).toLong() + this

