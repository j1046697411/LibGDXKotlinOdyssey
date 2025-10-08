package cn.jzl.lko.geom

import cn.jzl.datastructure.DoubleArrayList
import cn.jzl.datastructure.FastListAdder
import cn.jzl.datastructure.FloatArrayList
import cn.jzl.datastructure.IntArrayList
import cn.jzl.datastructure.LongArrayList
import cn.jzl.datastructure.MutableFastList
import cn.jzl.lko.geom.vector.IGenericVector
import cn.jzl.lko.geom.vector.checkDimensions
import cn.jzl.lko.geom.vector.components

class VectorArrayList<T> private constructor(private val data: MutableFastList<T>, override val dimensions: Int) : IVectorList<T>, IMutableVectorList<T> {

    override val size: Int get() = data.size / dimensions
    override var closed: Boolean = false

    private inline fun setInternal(index: Int, dimension: Int, block: (Int) -> Unit) {
        check(index >= 0 && index < size) { "index must be in [0, $size)" }
        check(dimension >= 0 && dimension < dimensions) { "dimension must be in [0, $dimensions)" }
        block(index * dimensions + dimension)
    }

    private fun index(index: Int, dimension: Int): Int {
        check(index >= 0 && index < size) { "index must be in [0, $size)" }
        check(dimension >= 0 && dimension < dimensions) { "dimension must be in [0, $dimensions)" }
        return index * dimensions + dimension
    }

    override fun get(index: Int, dimension: Int): T = data[index(index, dimension)]

    private fun FastListAdder<T>.unsafeAdd(vector: IGenericVector<T>) {
        checkDimensions(vector.dimensions)
        vector.components.forEach { component -> unsafeAdd(component) }
    }

    override fun set(index: Int, dimension: Int, component: T) = setInternal(index, dimension) { data[it] = component }

    override fun add(e: IGenericVector<T>) = data.safeAdd(e.dimensions) { unsafeAdd(e) }

    override fun add(e1: IGenericVector<T>, e2: IGenericVector<T>) = data.safeAdd(e1.dimensions + e2.dimensions) {
        unsafeAdd(e1)
        unsafeAdd(e2)
    }

    override fun add(e1: IGenericVector<T>, e2: IGenericVector<T>, e3: IGenericVector<T>) = data.safeAdd(e1.dimensions + e2.dimensions + e3.dimensions) {
        unsafeAdd(e1)
        unsafeAdd(e2)
        unsafeAdd(e3)
    }

    override fun add(
        e1: IGenericVector<T>,
        e2: IGenericVector<T>,
        e3: IGenericVector<T>,
        e4: IGenericVector<T>
    ) = data.safeAdd(e1.dimensions + e2.dimensions + e3.dimensions + e4.dimensions) {
        unsafeAdd(e1)
        unsafeAdd(e2)
        unsafeAdd(e3)
        unsafeAdd(e4)
    }

    override fun add(
        e1: IGenericVector<T>,
        e2: IGenericVector<T>,
        e3: IGenericVector<T>,
        e4: IGenericVector<T>,
        e5: IGenericVector<T>
    ) = data.safeAdd(e1.dimensions + e2.dimensions + e3.dimensions + e4.dimensions + e5.dimensions) {
        unsafeAdd(e1)
        unsafeAdd(e2)
        unsafeAdd(e3)
        unsafeAdd(e4)
        unsafeAdd(e5)
    }

    override fun safeAdd(count: Int, callback: FastListAdder<IGenericVector<T>>.() -> Unit) = data.safeAdd(count * dimensions) {
        FastListAdder<IGenericVector<T>> { vector -> unsafeAdd(vector) }.callback()
    }

    override fun safeSet(index: Int, element: IGenericVector<T>) {
        element.components.forEachIndexed { dimension, component -> setInternal(index, dimension) { data.safeSet(it, component) } }
    }

    override fun set(index: Int, element: IGenericVector<T>) {
        element.components.forEachIndexed { dimension, component -> setInternal(index, dimension) { data[it] = component } }
    }

    override fun plusAssign(elements: Iterable<IGenericVector<T>>) {
        when (elements) {
            is VectorArrayList -> data += elements.data
            is Collection<*> -> data.safeAdd(elements.size * dimensions) { elements.forEach { unsafeAdd(it) } }
            else -> elements.forEach { vector -> data.safeAdd(vector.dimensions) { unsafeAdd(vector) } }
        }
    }

    override fun clear() {
        data.clear()
        closed = false
    }

    companion object {
        fun double(dimensions: Int, capacity: Int = 7) = VectorArrayList(DoubleArrayList(capacity), dimensions)
        fun float(dimensions: Int, capacity: Int = 7) = VectorArrayList(FloatArrayList(capacity), dimensions)
        fun int(dimensions: Int, capacity: Int = 7) = VectorArrayList(IntArrayList(capacity), dimensions)
        fun long(dimensions: Int, capacity: Int = 7) = VectorArrayList(LongArrayList(capacity), dimensions)
    }
}