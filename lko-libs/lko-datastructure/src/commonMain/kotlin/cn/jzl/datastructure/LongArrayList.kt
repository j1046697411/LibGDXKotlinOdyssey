package cn.jzl.datastructure

import kotlin.math.max

class LongArrayList(capacity: Int = 7) : MutableLongFastList {
    private var data = LongArray(capacity)

    private var length: Int = 0
    val capacity: Int get() = data.size
    override val size: Int get() = length

    private val fastListAdder = FastListAdder<Long> { data[length++] = it }

    private fun ensure(count: Int) {
        if (length + count > capacity) {
            this.data = data.copyOf(max(capacity * 3, length + count))
        }
    }

    override fun add(e: Long) {
        ensure(1)
        data[length++] = e
    }

    override fun add(e1: Long, e2: Long) {
        ensure(2)
        data[length++] = e1
        data[length++] = e2
    }

    override fun add(e1: Long, e2: Long, e3: Long) {
        ensure(3)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
    }

    override fun add(e1: Long, e2: Long, e3: Long, e4: Long) {
        ensure(4)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
    }

    override fun add(e1: Long, e2: Long, e3: Long, e4: Long, e5: Long) {
        ensure(5)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
        data[length++] = e5
    }

    override fun safeAdd(count: Int, callback: FastListAdder<Long>.() -> Unit) {
        ensure(count)
        fastListAdder.callback()
    }

    override fun safeSet(index: Int, element: Long) {
        if (index >= length) ensure(index + 1)
        data[index] = element
    }

    override fun set(index: Int, element: Long) {
        check(index in 0 until length) { "index must be in [0, $length)" }
        data[index] = element
    }

    override fun plusAssign(elements: Iterable<Long>) {
        when (elements) {
            is LongArrayList -> {
                ensure(elements.size)
                data.copyInto(elements.data, 0, length, elements.size)
                length += elements.size
            }

            is Collection<Long> -> {
                ensure(elements.size)
                elements.forEachIndexed { index, i -> data[length + index] = i }
                length += elements.size
            }

            else -> elements.forEach(::add)
        }
    }

    override fun get(index: Int): Long {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("index: $index, size: $length")
        return data[index]
    }

    override fun clear() {
        data.fill(0)
        length = 0
    }
}