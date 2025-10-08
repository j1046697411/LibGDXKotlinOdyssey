package cn.jzl.lko.geom

import cn.jzl.datastructure.MutableFastList
import cn.jzl.lko.geom.vector.IGenericVector
import cn.jzl.lko.geom.vector.IMutableGenericVector
import cn.jzl.lko.geom.vector.MutableListGenericVector

interface IMutableVectorList<T> : IVectorList<T>, MutableFastList<IGenericVector<T>> {
    override val size: Int
    override val dimensions: Int
    override fun isEmpty(): Boolean = size == 0
    override fun get(index: Int, dimension: Int): T
    override fun get(index: Int): IMutableGenericVector<T> = MutableListGenericVector(this, index)

    operator fun set(index: Int, dimension: Int, component: T)
}