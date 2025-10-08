package cn.jzl.lko.geom

import kotlin.math.abs

interface IsAlmostEquals<T> {
    fun isAlmostEquals(other: T, epsilon: Float = 0.0001f): Boolean
}

fun Float.isAlmostEquals(other: Float, epsilon: Float): Boolean = abs(this - other) < epsilon
fun Double.isAlmostEquals(other: Double, epsilon: Double): Boolean = abs(this - other) < epsilon
