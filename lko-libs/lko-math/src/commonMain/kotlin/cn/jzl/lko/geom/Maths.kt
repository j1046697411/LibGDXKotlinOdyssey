@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.lko.geom

inline fun Int.inRange(min: Int, max: Int): Boolean = this >= min && this <= max
inline fun Long.inRange(min: Long, max: Long): Boolean = this >= min && this <= max
inline fun Float.inRange(min: Float, max: Float): Boolean = this >= min && this <= max
inline fun Double.inRange(min: Double, max: Double): Boolean = this >= min && this <= max

