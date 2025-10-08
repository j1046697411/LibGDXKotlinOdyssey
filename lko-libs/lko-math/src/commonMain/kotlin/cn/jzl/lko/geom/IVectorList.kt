package cn.jzl.lko.geom

import cn.jzl.lko.geom.vector.Dimension
import cn.jzl.lko.geom.vector.IGenericVector
import cn.jzl.lko.geom.vector.ListGenericVector

interface IVectorList<T> : Dimension {
    val size: Int

    val closed: Boolean

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = size > 0

    operator fun get(index: Int, dimension: Int): T
    operator fun get(index: Int): IGenericVector<T> = ListGenericVector(this, index)
}