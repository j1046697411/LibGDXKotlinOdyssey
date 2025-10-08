package cn.jzl.lko.geom

data class Size(val width: Float, val height: Float) {
    companion object {
        val ZERO = Size(0f, 0f)
        fun square(size: Float) = Size(size, size)
    }
}

interface Sizeable {
    val size: Size
}

operator fun Size.plus(other: Size): Size = Size(width + other.width, height + other.height)
operator fun Size.plus(other: Float): Size = Size(width + other, height + other)
operator fun Float.plus(other: Size): Size = Size(this + other.width, this + other.height)

operator fun Size.minus(other: Size): Size = Size(width - other.width, height - other.height)
operator fun Float.minus(other: Size): Size = Size(this - other.width, this - other.height)
operator fun Size.minus(other: Float): Size = Size(width - other, height - other)

operator fun Size.times(other: Size): Size = Size(width * other.width, height * other.height)
operator fun Float.times(other: Size): Size = Size(this * other.width, this * other.height)
operator fun Size.times(other: Float): Size = Size(width * other, height * other)

operator fun Size.div(scalar: Float): Size = Size(width / scalar, height / scalar)
operator fun Float.div(other: Size): Size = Size(this / other.width, this / other.height)
operator fun Size.div(other: Size): Size = Size(width / other.width, height / other.height)
