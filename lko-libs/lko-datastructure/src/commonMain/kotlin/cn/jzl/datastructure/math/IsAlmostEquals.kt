package cn.jzl.datastructure.math

import kotlin.math.abs

interface IsAlmostEquals<T> {
    fun isAlmostEquals(other: T, epsilon: Float = 0.0001f): Boolean
}

fun Float.isAlmostEquals(other: Float, epsilon: Float = 0.0001f): Boolean {
    return abs(this - other) < epsilon
}

fun Double.isAlmostEquals(other: Double, epsilon: Double = 0.0001): Boolean {
    return abs(this - other) < epsilon
}
