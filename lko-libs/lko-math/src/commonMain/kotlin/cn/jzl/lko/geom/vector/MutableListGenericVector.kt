package cn.jzl.lko.geom.vector

internal class MutableListGenericVector<T>(
    private val vectorList: IMutableVectorList<T>,
    private val offset: Int
) : IMutableGenericVector<T> {

    override val dimensions: Int get() = vectorList.dimensions

    override fun get(dimension: Int): T = vectorList[offset, dimension]

    override fun set(dimension: Int, value: T) {
        vectorList[offset, dimension] = value
    }

    override fun toString(): String = components.joinToString(", ", "MutableListGenericVector(", ")")
}