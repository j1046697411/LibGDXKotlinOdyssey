package cn.jzl.datastructure

internal class FastListIterator<T>(private val list: FastList<T>, private var index: Int) : ListIterator<T> {
    override fun hasNext(): Boolean = index < list.size
    override fun next(): T = list[index++]
    override fun hasPrevious(): Boolean = index > 0
    override fun previous(): T = list[--index]
    override fun nextIndex(): Int = index
    override fun previousIndex(): Int = index - 1
}