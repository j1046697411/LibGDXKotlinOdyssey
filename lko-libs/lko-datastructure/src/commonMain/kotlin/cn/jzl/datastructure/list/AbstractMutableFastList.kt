package cn.jzl.datastructure.list

import kotlin.IndexOutOfBoundsException

abstract class AbstractMutableFastList<T> : AbstractMutableList<T>(), MutableFastList<T> {

    protected fun checkIndex(index: Int) {
        if (0 > index || index >= size) throw IndexOutOfBoundsException("index $index is out of bounds for size $size")
    }

    override fun add(element: T): Boolean {
        insertLast(element)
        return true
    }

    override fun add(index: Int, element: T): Unit = insert(index, element)
}