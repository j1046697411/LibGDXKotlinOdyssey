package cn.jzl.lko.geom.vector

interface IVector4<T> : IGenericVector<T> {
    val x: T
    val y: T
    val z: T
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