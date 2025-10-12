package cn.jzl.datastructure.list

interface ShortMutableFastList : MutableFastList<Short> {
    fun insertLastAll(elements: ShortArray)
    fun insertAll(index: Int, elements: ShortArray)
}