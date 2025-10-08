package cn.jzl.datastructure

interface MutableIntFastList : MutableFastList<Int>, IntFastList {
    fun add(elements: IntArray, offset: Int = 0, count: Int = elements.size - offset)
    fun plusAssign(elements: IntArray): Unit = add(elements)
}