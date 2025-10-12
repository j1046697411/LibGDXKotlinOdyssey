package cn.jzl.datastructure.list

interface CharMutableFastList : MutableFastList<Char> {
    fun insertLastAll(elements: CharArray)
    fun insertAll(index: Int, elements: CharArray)
}