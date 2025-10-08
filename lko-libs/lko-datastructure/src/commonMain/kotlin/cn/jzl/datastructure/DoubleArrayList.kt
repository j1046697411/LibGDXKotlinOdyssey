package cn.jzl.datastructure

import kotlin.math.max

class DoubleArrayList(capacity: Int = 7) : MutableDoubleFastList {
    private var data = DoubleArray(capacity)

    private var length: Int = 0
    val capacity: Int get() = data.size
    override val size: Int get() = length

    private val fastListAdder = FastListAdder<Double> { data[length++] = it }

    private fun ensure(count: Int) {
        if (length + count > capacity) {
            this.data = data.copyOf(max(capacity * 3, length + count))
        }
    }

    override fun add(e: Double) {
        ensure(1)
        data[length++] = e
    }

    override fun add(e1: Double, e2: Double) {
        ensure(2)
        data[length++] = e1
        data[length++] = e2
    }

    override fun add(e1: Double, e2: Double, e3: Double) {
        ensure(3)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
    }

    override fun add(e1: Double, e2: Double, e3: Double, e4: Double) {
        ensure(4)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
    }


    override fun add(e1: Double, e2: Double, e3: Double, e4: Double, e5: Double) {
        ensure(5)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
        data[length++] = e5
    }

    override fun safeAdd(count: Int, callback: FastListAdder<Double>.() -> Unit) {
        ensure(count)
        fastListAdder.callback()
    }

    override fun safeSet(index: Int, element: Double) {
        if (index >= length) ensure(index + 1)
        data[index] = element
    }

    override fun set(index: Int, element: Double) {
        check(index in 0 until length) { "index must be in [0, $length)" }
        data[index] = element
    }

    override fun plusAssign(elements: Iterable<Double>) {
        when (elements) {
            is DoubleArrayList -> {
                ensure(elements.size)
                data.copyInto(elements.data, 0, length, elements.size)
                length += elements.size
            }

            is Collection<Double> -> {
                ensure(elements.size)
                elements.forEachIndexed { index, i -> data[length + index] = i }
                length += elements.size
            }

            else -> elements.forEach(::add)
        }
    }

    override fun get(index: Int): Double {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("index: $index, size: $length")
        return data[index]
    }

    override fun clear() {
        length = 0
        data.fill(0.0)
    }
}