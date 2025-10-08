package cn.jzl.datastructure

internal inline fun <T> indexOfCheck(value: T, start: Int, end: Int, get: (Int) -> T): Int {
    for (n in start until end) if (get(n) == value) return n
    return -1
}

internal inline fun <T> lastIndexOfCheck(value: T, start: Int, end: Int, get: (Int) -> T): Int {
    for (n in end - 1 downTo start) if (get(n) == value) return n
    return -1
}

internal fun <T> containsCheck(collection: Collection<T>, element: T): Boolean {
    for (v in collection) if (v == element) return true
    return false
}

internal fun <T> containsAllCheck(collection: Collection<T>, elements: Collection<T>): Boolean {
    val elementsMap = elements.toMutableSet()
    for (v in collection) elementsMap.remove(v)
    return elementsMap.isEmpty()
}

internal fun <T> fastSubList(list: FastList<T>, fromIndex: Int, toIndex: Int): List<T> = object : AbstractList<T>() {
    override val size: Int = toIndex - fromIndex
    override fun get(index: Int): T {
        if (index !in 0 until size) throw IndexOutOfBoundsException()
        return list[index + fromIndex]
    }
}


