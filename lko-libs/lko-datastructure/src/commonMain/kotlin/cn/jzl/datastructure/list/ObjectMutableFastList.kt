package cn.jzl.datastructure.list

interface ObjectMutableFastList<T> : PrimitiveMutableFastList<T> {
    fun insertLastAll(elements: Array<out T>)
    fun insertAll(index: Int, elements: Array<out T>)
}