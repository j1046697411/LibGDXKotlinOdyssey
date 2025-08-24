package cn.jzl.lko.math

interface IsAlmostEquals<T> {
    fun isAlmostEquals(other: T, epsilon: Float = 0.000001f): Boolean
}

