package cn.jzl.datastructure.list

internal fun Collection<*>.checkIndex(index: Int) {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("Index $index is out of bounds for size $size")
    }
}

fun interface ListEditor<T> {
    fun unsafeInsert(element: T)
}

interface MutableFastList<T> : MutableList<T> {
    override fun add(element: T): Boolean
    override fun add(index: Int, element: T)

    fun insertLast(element: T)
    fun insertLast(element1: T, element2: T)
    fun insertLast(element1: T, element2: T, element3: T)
    fun insertLast(element1: T, element2: T, element3: T, element4: T)
    fun insertLast(element1: T, element2: T, element3: T, element4: T, element5: T)
    fun insertLast(element1: T, element2: T, element3: T, element4: T, element5: T, element6: T)
    fun insertLastAll(elements: Iterable<T>)

    fun insert(index: Int, element: T)
    fun insert(index: Int, element1: T, element2: T)
    fun insert(index: Int, element1: T, element2: T, element3: T)
    fun insert(index: Int, element1: T, element2: T, element3: T, element4: T)
    fun insert(index: Int, element1: T, element2: T, element3: T, element4: T, element5: T)
    fun insert(index: Int, element1: T, element2: T, element3: T, element4: T, element5: T, element6: T)
    fun insertAll(index: Int, elements: Iterable<T>)

    fun safeInsertLast(count: Int, callback: ListEditor<T>.() -> Unit)
    fun safeInsert(index: Int, count: Int, callback: ListEditor<T>.() -> Unit)
}

