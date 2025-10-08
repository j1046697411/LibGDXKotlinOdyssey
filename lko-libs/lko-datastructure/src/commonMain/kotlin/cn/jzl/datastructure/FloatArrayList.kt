package cn.jzl.datastructure

import java.lang.IndexOutOfBoundsException
import kotlin.math.max

class FloatArrayList(capacity: Int = 7) : MutableFloatFastList {
    private var data = FloatArray(capacity)

    private var length: Int = 0
    val capacity: Int get() = data.size
    override val size: Int get() = length

    private val fastListAdder = FastListAdder<Float> { data[length++] = it }

    private fun ensure(count: Int) {
        if (length + count > capacity) {
            this.data = data.copyOf(max(capacity * 3, length + count))
        }
    }

    override fun add(e: Float) {
        ensure(1)
        data[length++] = e
    }

    override fun add(e1: Float, e2: Float) {
        ensure(2)
        data[length++] = e1
        data[length++] = e2
    }

    override fun add(e1: Float, e2: Float, e3: Float) {
        ensure(3)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
    }

    override fun add(e1: Float, e2: Float, e3: Float, e4: Float) {
        ensure(4)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
    }

    override fun add(e1: Float, e2: Float, e3: Float, e4: Float, e5: Float) {
        ensure(5)
        data[length++] = e1
        data[length++] = e2
        data[length++] = e3
        data[length++] = e4
        data[length++] = e5
    }

    override fun safeAdd(count: Int, callback: FastListAdder<Float>.() -> Unit) {
        ensure(count)
        fastListAdder.callback()
    }
    override fun safeSet(index: Int, element: Float) {
        if (index >= length) ensure(index + 1 - length)
        data[index] = element
    }

    override fun set(index: Int, element: Float) {
        check(index in 0 until length) { "index must be in [0, $length)" }
        data[index] = element
    }
    override fun plusAssign(elements: Iterable<Float>) {
        when (elements) {
            is FloatArrayList -> {
                ensure(elements.size)
                data.copyInto(elements.data, 0, length, elements.size)
                length += elements.size
            }

            is Collection<Float> -> {
                ensure(elements.size)
                elements.forEachIndexed { index, i -> data[length + index] = i }
                length += elements.size
            }

            else -> elements.forEach(::add)
        }
    }

    override fun get(index: Int): Float {
        if (index < 0 || index >= length) throw IndexOutOfBoundsException("index: $index, size: $length")
        return data[index]
    }

    override fun clear() {
        data.fill(0f)
        length = 0
    }
}