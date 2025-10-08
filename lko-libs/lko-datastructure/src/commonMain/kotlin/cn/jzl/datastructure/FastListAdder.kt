package cn.jzl.datastructure

fun interface FastListAdder<T> {
    fun unsafeAdd(element: T)
}