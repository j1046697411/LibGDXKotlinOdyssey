package cn.jzl.datastructure.list

interface ByteMutableFastList : MutableFastList<Byte> {
    fun insertLastAll(elements: ByteArray)
    fun insertAll(index: Int, elements: ByteArray)
}