package cn.jzl.datastructure.geom.generic

import cn.jzl.datastructure.list.*

class GenericVectorList<T : Any>(dimensions: Int, data: MutableFastList<T>) : AbstractVectorList<T, IGenericVector<T>>(dimensions, data) {

    override fun ensure(count: Int) {
    }

    override fun migrate(index: Int, count: Int, callback: InsertEditor<IGenericVector<T>>.() -> Unit) {
        data.safeInsert(index, count * dimensions) { unsafeListEditor.apply(callback) }
    }

    override fun get(index: Int): IMutableGenericVector<T> = VectorListGenericVector(index, this)

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: IGenericVector<T>): ArrayGenericVector<T> {
        checkIndex(index)
        val data = Array<Any>(dimensions) { data[index * dimensions + it] = element[it] }
        return ArrayGenericVector(data as Array<T>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): ArrayGenericVector<T> {
        check(0 <= index && index < size) { "index $index is out of range [0, $size)" }
        val data = Array<Any>(dimensions) { data.removeAt(index * dimensions + (dimensions - it - 1)) }
        data.reverse()
        return ArrayGenericVector(data as Array<T>)
    }

    override fun insertLast(element: IGenericVector<T>) = data.safeInsertLast(dimensions) {
        element.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLast(element1: IGenericVector<T>, element2: IGenericVector<T>) = data.safeInsertLast(dimensions * 2) {
        element1.components.forEach { unsafeInsertLast(it) }
        element2.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 3) {
        element1.components.forEach { unsafeInsertLast(it) }
        element2.components.forEach { unsafeInsertLast(it) }
        element3.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 4) {
        element1.components.forEach { unsafeInsertLast(it) }
        element2.components.forEach { unsafeInsertLast(it) }
        element3.components.forEach { unsafeInsertLast(it) }
        element4.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 5) {
        element1.components.forEach { unsafeInsertLast(it) }
        element2.components.forEach { unsafeInsertLast(it) }
        element3.components.forEach { unsafeInsertLast(it) }
        element4.components.forEach { unsafeInsertLast(it) }
        element5.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLast(
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>,
        element6: IGenericVector<T>
    ) = data.safeInsertLast(dimensions * 6) {
        element1.components.forEach { unsafeInsertLast(it) }
        element2.components.forEach { unsafeInsertLast(it) }
        element3.components.forEach { unsafeInsertLast(it) }
        element4.components.forEach { unsafeInsertLast(it) }
        element5.components.forEach { unsafeInsertLast(it) }
        element6.components.forEach { unsafeInsertLast(it) }
    }

    override fun insertLastAll(elements: Iterable<IGenericVector<T>>) {
        when (elements) {
            is Collection<IGenericVector<T>> -> data.safeInsertLast(dimensions * elements.size) {
                elements.forEach { vector -> vector.components.forEach { unsafeInsertLast(it) } }
            }

            else -> elements.forEach(::insertLast)
        }
    }

    override fun insert(index: Int, element: IGenericVector<T>) = data.safeInsert(index * dimensions, dimensions) {
        element.components.forEachIndexed { index, component -> unsafeSet(index, component) }
    }

    override fun insert(index: Int, element1: IGenericVector<T>, element2: IGenericVector<T>) = data.safeInsert(index * dimensions, dimensions * 2) {
        element1.components.forEachIndexed { index, component -> unsafeSet(index, component) }
        element2.components.forEachIndexed { index, component -> unsafeSet(index + dimensions, component) }
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 3) {
        element1.components.forEachIndexed { index, component -> unsafeSet(index, component) }
        element2.components.forEachIndexed { index, component -> unsafeSet(index + dimensions, component) }
        element3.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 2, component) }
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 4) {
        element1.components.forEachIndexed { index, component -> unsafeSet(index, component) }
        element2.components.forEachIndexed { index, component -> unsafeSet(index + dimensions, component) }
        element3.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 2, component) }
        element4.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 3, component) }
    }

    override fun insert(
        index: Int,
        element1: IGenericVector<T>,
        element2: IGenericVector<T>,
        element3: IGenericVector<T>,
        element4: IGenericVector<T>,
        element5: IGenericVector<T>
    ) = data.safeInsert(index * dimensions, dimensions * 5) {
        element1.components.forEachIndexed { index, component -> unsafeSet(index, component) }
        element2.components.forEachIndexed { index, component -> unsafeSet(index + dimensions, component) }
        element3.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 2, component) }
        element4.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 3, component) }
        element5.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 4, component) }
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
        element1.components.forEachIndexed { index, component -> unsafeSet(index, component) }
        element2.components.forEachIndexed { index, component -> unsafeSet(index + dimensions, component) }
        element3.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 2, component) }
        element4.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 3, component) }
        element5.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 4, component) }
        element6.components.forEachIndexed { index, component -> unsafeSet(index + dimensions * 5, component) }
    }

    override fun insertAll(index: Int, elements: Iterable<IGenericVector<T>>) {
        when (elements) {
            is Collection<IGenericVector<T>> -> data.safeInsert(index * dimensions, dimensions * elements.size) {
                elements.forEach { vector -> vector.components.forEachIndexed { index, component -> unsafeSet(index, component) } }
            }

            else -> elements.forEach { insert(index * dimensions, it) }
        }
    }

    override fun unsafeListEditor(): ListEditor<IGenericVector<T>> {
        return object : ListEditor<IGenericVector<T>> {
            override fun unsafeInsertLast(element: IGenericVector<T>) {
                element.components.forEach(data.unsafeListEditor::unsafeInsertLast)
            }

            override fun unsafeSet(index: Int, element: IGenericVector<T>) {
                element.components.forEachIndexed { componentIndex, component ->
                    data.unsafeListEditor.unsafeSet(index * dimensions + componentIndex, component)
                }
            }
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