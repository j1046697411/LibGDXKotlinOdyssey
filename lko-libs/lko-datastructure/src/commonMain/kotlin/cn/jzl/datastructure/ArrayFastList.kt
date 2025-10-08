package cn.jzl.datastructure

import kotlin.math.max

class ArrayFastList<T>(capacity: Int = 7) : MutableFastList<T> {
    private var data = arrayOfNulls<Any?>(capacity)
    private val fastListAdder = FastListAdder<T> { data[length++] = it }

    private var length: Int = 0
    val capacity: Int get() = data.size
    override val size: Int get() = length

    private fun ensure(count: Int) {
        if (length + count > capacity) {
            this.data = data.copyOf(max(capacity * 3, length + count))
        }
    }

    override fun add(e: T) {
        ensure(1)
        data[length++] = e
    }

    override fun add(e1: T, e2: T) {
        ensure(2)
        data[length++] = e1
        data[length++] = e2
    }

    override fun add(e1: T, e2: T, e3: T) {
        ensure(3)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
    }

    override fun add(e1: T, e2: T, e3: T, e4: T) {
        ensure(4)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
    }

    override fun add(e1: T, e2: T, e3: T, e4: T, e5: T) {
        ensure(5)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
        data[length++] = e5
    }

    override fun safeAdd(count: Int, callback: FastListAdder<T>.() -> Unit) {
        ensure(count)
        fastListAdder.callback()
    }

    override fun safeSet(index: Int, element: T) {
        if (index >= length) ensure(index + 1)
        data[index] = element
    }

    override fun set(index: Int, element: T) {
        check(index in 0 until length) { "index must be in [0, $length)" }
        data[index] = element
    }

    override fun plusAssign(elements: Iterable<T>) {
        when (elements) {
            is ArrayFastList<T> -> {
                ensure(elements.size)
                data.copyInto(elements.data, 0, length, elements.size)
                length += elements.size
            }

            is Collection<T> -> {
                ensure(elements.size)
                elements.forEachIndexed { index, i -> data[length + index] = i }
                length += elements.size
            }

            else -> elements.forEach(::add)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): T {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("index: $index, size: $length")
        return data[index] as T
    }

    override fun clear() {
        length = 0
        data.fill(null)
    }
}