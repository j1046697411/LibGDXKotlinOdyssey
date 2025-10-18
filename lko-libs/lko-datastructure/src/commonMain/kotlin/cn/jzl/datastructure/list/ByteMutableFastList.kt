package cn.jzl.datastructure.list

interface ByteMutableFastList : PrimitiveMutableFastList<Byte> {
    fun insertLastAll(elements: ByteArray)
    fun insertAll(index: Int, elements: ByteArray)
}