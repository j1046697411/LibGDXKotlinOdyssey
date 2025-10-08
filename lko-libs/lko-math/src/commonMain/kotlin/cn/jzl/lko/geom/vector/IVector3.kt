package cn.jzl.lko.geom.vector

interface IVector3<T> : IGenericVector<T> {
    val x: T
    val y: T
    val z: T

    override val dimensions: Int get() = 3

    override fun get(dimension: Int): T = when (dimension) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException("dimension $dimension")
    }
}