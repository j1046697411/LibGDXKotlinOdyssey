package cn.jzl.datastructure.geom.generic

import cn.jzl.datastructure.list.AbstractMutableFastList
import cn.jzl.datastructure.list.MutableFastList

interface Dimension {
    val dimensions: Int
}

private fun Dimension.checkDimension(dimension: Int) {
    check(0 <= dimension && dimension < this.dimensions) { "dimension $dimension is out of range [0, ${this.dimensions})" }
}

val <T> IGenericVector<T>.components: Sequence<T> get() = sequence { for (i in 0 until dimensions) yield(get(i)) }

interface IGenericVector<T> : Dimension {
    operator fun get(dimension: Int): T
}

interface IMutableGenericVector<T> : IGenericVector<T> {
    operator fun set(dimension: Int, value: T)
}

interface IGenericVectorList<T> : Dimension {
    val size: Int
    operator fun get(index: Int, dimension: Int): T
    operator fun set(index: Int, dimension: Int, value: T)
    operator fun get(index: Int): IGenericVector<T>
}

interface IVectorList<T, V : IGenericVector<T>> : IGenericVectorList<T>, MutableFastList<V> {
    override fun get(index: Int): V
    override operator fun set(index: Int, element: V): V
}

abstract class AbstractVectorList<T, V : IGenericVector<T>>(
    override val dimensions: Int,
    protected val data: MutableFastList<T>,
) : AbstractMutableFastList<V>(), IVectorList<T, V> {

    override val size: Int get() = data.size / dimensions

    private fun index(index: Int, dimension: Int): Int {
        check(0 <= index && index < size) { "index $index is out of range [0, $size)" }
        check(0 <= dimension && dimension < this.dimensions) { "dimension $dimension is out of range [0, ${this.dimensions})" }
        return index * this.dimensions + dimension
    }

    override fun get(index: Int, dimension: Int): T = data[index(index, dimension)]

    override fun set(index: Int, dimension: Int, value: T) {
        data[index(index, dimension)] = value
    }
}

@JvmInline
value class ArrayGenericVector<T> internal constructor(private val data: Array<T>) : IGenericVector<T> {
    override val dimensions: Int get() = data.size
    override operator fun get(dimension: Int): T = data[dimension]
}

internal data class VectorListGenericVector<T>(private val index: Int, private val data: IGenericVectorList<T>) : IMutableGenericVector<T> {

    override val dimensions: Int get() = data.dimensions

    override operator fun get(dimension: Int): T = data[index, dimension]

    override fun set(dimension: Int, value: T) {
        data[index, dimension] = value
    }

    override fun toString(): String {
        return components.joinToString(", ", "GenericVector(", ")")
    }
}

