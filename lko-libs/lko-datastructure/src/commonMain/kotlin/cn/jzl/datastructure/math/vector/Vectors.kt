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

