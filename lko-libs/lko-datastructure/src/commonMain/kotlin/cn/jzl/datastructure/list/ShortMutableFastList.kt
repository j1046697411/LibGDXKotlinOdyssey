package cn.jzl.datastructure.list

interface ShortMutableFastList : PrimitiveMutableFastList<Short> {
    fun insertLastAll(elements: ShortArray)
    fun insertAll(index: Int, elements: ShortArray)
}