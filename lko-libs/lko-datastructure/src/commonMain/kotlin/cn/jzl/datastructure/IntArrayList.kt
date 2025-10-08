package cn.jzl.datastructure

import kotlin.math.max

class IntArrayList(capacity: Int = 7) : MutableIntFastList {
    private var data = IntArray(capacity)

    private var length: Int = 0
    val capacity: Int get() = data.size
    override val size: Int get() = length

    private val fastListAdder = FastListAdder<Int> { data[length++] = it }

    private fun ensure(count: Int) {
        if (length + count > capacity) {
            this.data = data.copyOf(max(capacity * 3, length + count))
        }
    }

    override fun add(e: Int) {
        ensure(1)
        data[length++] = e
    }

    override fun add(e1: Int, e2: Int) {
        ensure(2)
        data[length++] = e1
        data[length++] = e2
    }

    override fun add(e1: Int, e2: Int, e3: Int) {
        ensure(3)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
    }

    override fun add(e1: Int, e2: Int, e3: Int, e4: Int) {
        ensure(4)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
    }

    override fun add(e1: Int, e2: Int, e3: Int, e4: Int, e5: Int) {
        ensure(5)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
        data[length++] = e5
    }

    override fun add(elements: IntArray, offset: Int, count: Int) {
        ensure(count)
        data.copyInto(elements, offset, length, count)
        length += count
    }

    override fun safeAdd(count: Int, callback: FastListAdder<Int>.() -> Unit) {
        ensure(count)
        fastListAdder.callback()
    }
    override fun safeSet(index: Int, element: Int) {
        if (index >= length) ensure(index + 1)
        data[index] = element
    }

    override fun set(index: Int, element: Int) {
        check(index in 0 until length) { "index must be in [0, $length)" }
        data[index] = element
    }

    override fun plusAssign(element: Int): Unit = add(element)

    override fun plusAssign(elements: Iterable<Int>) {
        when (elements) {
            is IntArrayList -> {
                ensure(elements.size)
                data.copyInto(elements.data, 0, length, elements.size)
                length += elements.size
            }

            is Collection<Int> -> {
                ensure(elements.size)
                elements.forEachIndexed { index, i -> data[length + index] = i }
                length += elements.size
            }

            else -> elements.forEach(::add)
        }
    }

    override fun get(index: Int): Int {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("index: $index, size: $length")
        return data[index]
    }

    override fun clear() {
        length = 0
        data.fill(0)
    }
}