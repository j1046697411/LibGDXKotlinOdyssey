package cn.jzl.datastructure.list

interface LongMutableFastList : MutableFastList<Long> {
    fun insertLastAll(elements: LongArray)
    fun insertAll(index: Int, elements: LongArray)
}