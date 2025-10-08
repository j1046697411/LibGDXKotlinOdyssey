package cn.jzl.lko.geom.vector

import cn.jzl.lko.geom.IsAlmostEquals
import cn.jzl.lko.geom.isAlmostEquals
import kotlin.math.hypot

typealias Point2 = Vector2

data class Vector2(override val x: Float, override val y: Float) : IVector2<Float>, IsAlmostEquals<Vector2> {

    override fun isAlmostEquals(other: Vector2, epsilon: Float): Boolean {
        return x.isAlmostEquals(other.x, epsilon) && y.isAlmostEquals(other.y, epsilon)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector2) return false
        return isAlmostEquals(other)
    }

    override fun hashCode(): Int {
        return x.hashCode() * 31 + y.hashCode()
    }

    companion object {
        val ZERO = Vector2(0f, 0f)
        val UNIT = Vector2(1f, 1f)

        val UNIT_X = Vector2(1f, 0f)
        val UNIT_Y = Vector2(0f, 1f)
    }
}

inline val Vector2.length: Float get() = hypot(x, y)
inline val Vector2.normalized: Vector2 get() = this * (1f / length)
inline val Vector2.unit: Vector2 get() = this / length

fun Vector2.distance(other: Vector2): Float = hypot(x - other.x, y - other.y)

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
operator fun Float.rem(other: Vector2): Vector2 = Vector2(this % other.x, this % other.y)

operator fun Vector2.unaryMinus(): Vector2 = Vector2(-x, -y)
operator fun Vector2.unaryPlus(): Vector2 = Vector2(x, y)
