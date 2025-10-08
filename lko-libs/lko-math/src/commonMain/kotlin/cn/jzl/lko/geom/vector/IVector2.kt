package cn.jzl.lko.geom.vector

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

