package cn.jzl.datastructure

interface FastList<T> : Collection<T> {
    operator fun get(index: Int): T
    fun indexOf(value: T, start: Int = 0, end: Int = size): Int = indexOfCheck(value, start, end, this::get)
    fun lastIndexOf(value: T, start: Int = size - 1, end: Int = 0): Int = lastIndexOfCheck(value, start, end, this::get)
    fun listIterator(start: Int = 0): ListIterator<T> = FastListIterator(this, start)
    fun subList(fromIndex: Int, toIndex: Int): List<T> = fastSubList(this, fromIndex, toIndex)
    override fun contains(element: T): Boolean = containsCheck(this, element)
    override fun containsAll(elements: Collection<T>): Boolean = containsAllCheck(this, elements)
    override fun isEmpty(): Boolean = size == 0
    override fun iterator(): Iterator<T> = listIterator(0)
}