package cn.jzl.datastructure.list

interface LongMutableFastList : PrimitiveMutableFastList<Long> {
    fun insertLastAll(elements: LongArray)
    fun insertAll(index: Int, elements: LongArray)
}