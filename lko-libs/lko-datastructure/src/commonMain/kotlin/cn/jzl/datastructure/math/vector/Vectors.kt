package cn.jzl.datastructure.math.vector

import cn.jzl.datastructure.math.vector.generic.IGenericVector

typealias Point = Vector2

interface IVector2<T> : IGenericVector<T> {
    val x: T
    val y: T
    override val dimensions: Int get() = 2
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

interface IVector3<T> : IVector2<T> {
    val z: T
    override val dimensions: Int get() = 3
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

interface IVector4<T> : IVector3<T> {
    val w: T
    override val dimensions: Int get() = 4
    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}

data class Vector2(override val x: Float, override val y: Float) : IVector2<Float>
data class Vector3(override val x: Float, override val y: Float, override val z: Float) : IVector3<Float>
data class Vector4(override val x: Float, override val y: Float, override val z: Float, override val w: Float) : IVector4<Float>

data class IntVector2(override val x: Int, override val y: Int) : IVector2<Int>
data class IntVector3(override val x: Int, override val y: Int, override val z: Int) : IVector3<Int>
data class IntVector4(override val x: Int, override val y: Int, override val z: Int, override val w: Int) : IVector4<Int>

// Vector2 操作符扩展
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

operator fun Vector2.unaryMinus(): Vector2 = Vector2(-x, -y)
operator fun Vector2.unaryPlus(): Vector2 = Vector2(x, y)

// Vector3 操作符扩展
operator fun Vector3.plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
operator fun Vector3.plus(other: Float): Vector3 = Vector3(x + other, y + other, z + other)
operator fun Float.plus(other: Vector3): Vector3 = Vector3(this + other.x, this + other.y, this + other.z)

operator fun Vector3.minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)
operator fun Vector3.minus(other: Float): Vector3 = Vector3(x - other, y - other, z - other)
operator fun Float.minus(other: Vector3): Vector3 = Vector3(this - other.x, this - other.y, this - other.z)

operator fun Vector3.times(other: Vector3): Vector3 = Vector3(x * other.x, y * other.y, z * other.z)
operator fun Vector3.times(other: Float): Vector3 = Vector3(x * other, y * other, z * other)
operator fun Float.times(other: Vector3): Vector3 = Vector3(this * other.x, this * other.y, this * other.z)

operator fun Vector3.div(other: Vector3): Vector3 = Vector3(x / other.x, y / other.y, z / other.z)
operator fun Vector3.div(other: Float): Vector3 = Vector3(x / other, y / other, z / other)
operator fun Float.div(other: Vector3): Vector3 = Vector3(this / other.x, this / other.y, this / other.z)

operator fun Vector3.rem(other: Vector3): Vector3 = Vector3(x % other.x, y % other.y, z % other.z)
operator fun Vector3.rem(other: Float): Vector3 = Vector3(x % other, y % other, z % other)

operator fun Vector3.unaryMinus(): Vector3 = Vector3(-x, -y, -z)
operator fun Vector3.unaryPlus(): Vector3 = Vector3(x, y, z)

// Vector4 操作符扩展
operator fun Vector4.plus(other: Vector4): Vector4 = Vector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun Vector4.plus(other: Float): Vector4 = Vector4(x + other, y + other, z + other, w + other)
operator fun Float.plus(other: Vector4): Vector4 = Vector4(this + other.x, this + other.y, this + other.z, this + other.w)

operator fun Vector4.minus(other: Vector4): Vector4 = Vector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun Vector4.minus(other: Float): Vector4 = Vector4(x - other, y - other, z - other, w - other)
operator fun Float.minus(other: Vector4): Vector4 = Vector4(this - other.x, this - other.y, this - other.z, this - other.w)

operator fun Vector4.times(other: Vector4): Vector4 = Vector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun Vector4.times(other: Float): Vector4 = Vector4(x * other, y * other, z * other, w * other)
operator fun Float.times(other: Vector4): Vector4 = Vector4(this * other.x, this * other.y, this * other.z, this * other.w)

operator fun Vector4.div(other: Vector4): Vector4 = Vector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun Vector4.div(other: Float): Vector4 = Vector4(x / other, y / other, z / other, w / other)
operator fun Float.div(other: Vector4): Vector4 = Vector4(this / other.x, this / other.y, this / other.z, this / other.w)

operator fun Vector4.rem(other: Vector4): Vector4 = Vector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun Vector4.rem(other: Float): Vector4 = Vector4(x % other, y % other, z % other, w % other)

operator fun Vector4.unaryMinus(): Vector4 = Vector4(-x, -y, -z, -w)
operator fun Vector4.unaryPlus(): Vector4 = Vector4(x, y, z, w)

// IntVector2 操作符扩展
operator fun IntVector2.plus(other: IntVector2): IntVector2 = IntVector2(x + other.x, y + other.y)
operator fun IntVector2.plus(other: Int): IntVector2 = IntVector2(x + other, y + other)
operator fun Int.plus(other: IntVector2): IntVector2 = IntVector2(this + other.x, this + other.y)

operator fun IntVector2.minus(other: IntVector2): IntVector2 = IntVector2(x - other.x, y - other.y)
operator fun IntVector2.minus(other: Int): IntVector2 = IntVector2(x - other, y - other)
operator fun Int.minus(other: IntVector2): IntVector2 = IntVector2(this - other.x, this - other.y)

operator fun IntVector2.times(other: IntVector2): IntVector2 = IntVector2(x * other.x, y * other.y)
operator fun IntVector2.times(other: Int): IntVector2 = IntVector2(x * other, y * other)
operator fun Int.times(other: IntVector2): IntVector2 = IntVector2(this * other.x, this * other.y)

operator fun IntVector2.div(other: IntVector2): IntVector2 = IntVector2(x / other.x, y / other.y)
operator fun IntVector2.div(other: Int): IntVector2 = IntVector2(x / other, y / other)
operator fun Int.div(other: IntVector2): IntVector2 = IntVector2(this / other.x, this / other.y)

operator fun IntVector2.rem(other: IntVector2): IntVector2 = IntVector2(x % other.x, y % other.y)
operator fun IntVector2.rem(other: Int): IntVector2 = IntVector2(x % other, y % other)

operator fun IntVector2.unaryMinus(): IntVector2 = IntVector2(-x, -y)
operator fun IntVector2.unaryPlus(): IntVector2 = IntVector2(x, y)

// IntVector3 操作符扩展
operator fun IntVector3.plus(other: IntVector3): IntVector3 = IntVector3(x + other.x, y + other.y, z + other.z)
operator fun IntVector3.plus(other: Int): IntVector3 = IntVector3(x + other, y + other, z + other)
operator fun Int.plus(other: IntVector3): IntVector3 = IntVector3(this + other.x, this + other.y, this + other.z)

operator fun IntVector3.minus(other: IntVector3): IntVector3 = IntVector3(x - other.x, y - other.y, z - other.z)
operator fun IntVector3.minus(other: Int): IntVector3 = IntVector3(x - other, y - other, z - other)
operator fun Int.minus(other: IntVector3): IntVector3 = IntVector3(this - other.x, this - other.y, this - other.z)

operator fun IntVector3.times(other: IntVector3): IntVector3 = IntVector3(x * other.x, y * other.y, z * other.z)
operator fun IntVector3.times(other: Int): IntVector3 = IntVector3(x * other, y * other, z * other)
operator fun Int.times(other: IntVector3): IntVector3 = IntVector3(this * other.x, this * other.y, this * other.z)

operator fun IntVector3.div(other: IntVector3): IntVector3 = IntVector3(x / other.x, y / other.y, z / other.z)
operator fun IntVector3.div(other: Int): IntVector3 = IntVector3(x / other, y / other, z / other)
operator fun Int.div(other: IntVector3): IntVector3 = IntVector3(this / other.x, this / other.y, this / other.z)

operator fun IntVector3.rem(other: IntVector3): IntVector3 = IntVector3(x % other.x, y % other.y, z % other.z)
operator fun IntVector3.rem(other: Int): IntVector3 = IntVector3(x % other, y % other, z % other)

operator fun IntVector3.unaryMinus(): IntVector3 = IntVector3(-x, -y, -z)
operator fun IntVector3.unaryPlus(): IntVector3 = IntVector3(x, y, z)

// IntVector4 操作符扩展
operator fun IntVector4.plus(other: IntVector4): IntVector4 = IntVector4(x + other.x, y + other.y, z + other.z, w + other.w)
operator fun IntVector4.plus(other: Int): IntVector4 = IntVector4(x + other, y + other, z + other, w + other)
operator fun Int.plus(other: IntVector4): IntVector4 = IntVector4(this + other.x, this + other.y, this + other.z, this + other.w)

operator fun IntVector4.minus(other: IntVector4): IntVector4 = IntVector4(x - other.x, y - other.y, z - other.z, w - other.w)
operator fun IntVector4.minus(other: Int): IntVector4 = IntVector4(x - other, y - other, z - other, w - other)
operator fun Int.minus(other: IntVector4): IntVector4 = IntVector4(this - other.x, this - other.y, this - other.z, this - other.w)

operator fun IntVector4.times(other: IntVector4): IntVector4 = IntVector4(x * other.x, y * other.y, z * other.z, w * other.w)
operator fun IntVector4.times(other: Int): IntVector4 = IntVector4(x * other, y * other, z * other, w * other)
operator fun Int.times(other: IntVector4): IntVector4 = IntVector4(this * other.x, this * other.y, this * other.z, this * other.w)

operator fun IntVector4.div(other: IntVector4): IntVector4 = IntVector4(x / other.x, y / other.y, z / other.z, w / other.w)
operator fun IntVector4.div(other: Int): IntVector4 = IntVector4(x / other, y / other, z / other, w / other)
operator fun Int.div(other: IntVector4): IntVector4 = IntVector4(this / other.x, this / other.y, this / other.z, this / other.w)

operator fun IntVector4.rem(other: IntVector4): IntVector4 = IntVector4(x % other.x, y % other.y, z % other.z, w % other.w)
operator fun IntVector4.rem(other: Int): IntVector4 = IntVector4(x % other, y % other, z % other, w % other)

operator fun IntVector4.unaryMinus(): IntVector4 = IntVector4(-x, -y, -z, -w)
operator fun IntVector4.unaryPlus(): IntVector4 = IntVector4(x, y, z, w)