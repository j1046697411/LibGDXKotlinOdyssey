package cn.jzl.datastructure.list

interface IntMutableFastList : MutableFastList<Int> {
    fun insertLastAll(elements: IntArray)
    fun insertAll(index: Int, elements: IntArray)
}