package cn.jzl.datastructure

interface MutableFastList<T> : FastList<T> {
    fun add(e: T)
    fun add(e1: T, e2: T)
    fun add(e1: T, e2: T, e3: T)
    fun add(e1: T, e2: T, e3: T, e4: T)
    fun add(e1: T, e2: T, e3: T, e4: T, e5: T)

    fun safeAdd(count: Int, callback: FastListAdder<T>.() -> Unit)
    fun safeSet(index: Int, element: T)

    operator fun set(index: Int, element: T)

    operator fun plusAssign(element: T): Unit = add(element)
    operator fun plusAssign(elements: Iterable<T>)
    operator fun plusAssign(elements: Sequence<T>) {
        this += elements.toList()
    }

    fun clear()
}