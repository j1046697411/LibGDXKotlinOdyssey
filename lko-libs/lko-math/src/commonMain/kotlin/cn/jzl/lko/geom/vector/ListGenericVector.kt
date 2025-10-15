package cn.jzl.lko.geom.vector

internal open class ListGenericVector<T>(
    private val vectorList: IVectorList<T>,
    private val offset: Int
) : IGenericVector<T> {
    override val dimensions: Int get() = vectorList.dimensions
    override fun get(dimension: Int): T = vectorList[offset, dimension]
    override fun toString(): String = components.joinToString(",", "ListGenericVector(", ")")
}