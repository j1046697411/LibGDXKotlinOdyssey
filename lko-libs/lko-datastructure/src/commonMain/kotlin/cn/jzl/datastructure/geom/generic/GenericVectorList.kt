package cn.jzl.datastructure.geom.generic

import cn.jzl.datastructure.list.*

class GenericVectorList<T : Any>(dimensions: Int, data: MutableFastList<T>) : AbstractVectorList<T, IGenericVector<T>>(dimensions, data) {

    override fun get(index: Int): IMutableGenericVector<T> {
        checkIndex(index)
        return VectorListGenericVector(index, this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: IGenericVector<T>): ArrayGenericVector<T> {
        checkIndex(index)
        val data = Array<Any>(dimensions) { data.set(index * dimensions + it, element[it]) }
        return ArrayGenericVector(data as Array<T>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): ArrayGenericVector<T> {
        checkIndex(index)
        val data = Array<Any>(dimensions) { data.removeAt(index * dimensions + (dimensions - it - 1)) }
        data.reverse()
        return ArrayGenericVector(data as Array<T>)
    }

    override fun insertLast(element: IGenericVector<T>) = data.safeInsertLast(dimensions) {
        unsafeInsert(element)
    }

    override fun insertLast(element1: IGenericVector<T>, element2: IGenericVector<T>) = data.safeInsertLast(dimensions * 2) {
        unsafeInsert(element1)
        unsafeInsert(element2)
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 3) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 4) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 5) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
        unsafeInsert(element5)
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>,
        element6: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 6) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
        unsafeInsert(element5)
        unsafeInsert(element6)
    }

    override fun insertLastAll(elements: Iterable<IGenericVector<T>>) {
        when (elements) {
            is Collection<IGenericVector<T>> -> data.safeInsertLast(dimensions * elements.size) {
                elements.forEach { unsafeInsert(it) }
            }

            else -> elements.forEach(::insertLast)
        }
    }

    override fun safeInsertLast(count: Int, callback: ListEditor<IGenericVector<T>>.() -> Unit) = data.safeInsertLast(count * dimensions) {
        ListEditor<IGenericVector<T>> { unsafeInsert(it) }.apply(callback)
    }

    override fun safeInsert(index: Int, count: Int, callback: ListEditor<IGenericVector<T>>.() -> Unit) = data.safeInsert(index * dimensions, count * dimensions) {
        ListEditor<IGenericVector<T>> { unsafeInsert(it) }.apply(callback)
    }

    override fun insert(index: Int, element: IGenericVector<T>) = data.safeInsert(index * dimensions, dimensions) {
        unsafeInsert(element)
    }

    override fun insert(index: Int, element1: IGenericVector<T>, element2: IGenericVector<T>) = data.safeInsert(index * dimensions, dimensions * 2) {
        unsafeInsert(element1)
        unsafeInsert(element2)
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 3) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 4) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 5) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
        unsafeInsert(element5)
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>,
        element6: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 6) {
        unsafeInsert(element1)
        unsafeInsert(element2)
        unsafeInsert(element3)
        unsafeInsert(element4)
        unsafeInsert(element5)
        unsafeInsert(element6)
    }

    override fun insertAll(index: Int, elements: Iterable<IGenericVector<T>>) {
        when (elements) {
            is Collection<IGenericVector<T>> -> data.safeInsert(index * dimensions, dimensions * elements.size) {
                elements.forEach { unsafeInsert(it) }
            }

            else -> elements.forEachIndexed { offset, vector -> insert((index + offset) * dimensions, vector) }
        }
    }

    companion object {
        fun int(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, IntFastList(dimensions * capacity))
        fun float(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, FloatFastList(dimensions * capacity))
        fun long(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, LongFastList(dimensions * capacity))
        fun double(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, DoubleFastList(dimensions * capacity))
        fun short(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, ShortFastList(dimensions * capacity))
        fun byte(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, ByteFastList(dimensions * capacity))
        fun char(dimensions: Int, capacity: Int = 7) = GenericVectorList(dimensions, CharFastList(dimensions * capacity))
    }
}