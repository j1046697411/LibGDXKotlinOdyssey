package cn.jzl.datastructure.list

interface IntMutableFastList : PrimitiveMutableFastList<Int> {
    fun insertLastAll(elements: IntArray)
    fun insertAll(index: Int, elements: IntArray)
}