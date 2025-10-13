package cn.jzl.datastructure.list

import kotlin.math.max

class FloatFastList(capacity: Int = 7) : AbstractMutableFastList<Float>(), FloatMutableFastList {
    private var data = FloatArray(capacity)

    override var size: Int = 0
        private set

    private fun ensure(count: Int) {
        if (count + size > data.size) {
            data = data.copyOf(max(count + size, data.size * 2))
        }
    }

    override fun safeInsert(index: Int, count: Int, callback: ListEditor<Float>.() -> Unit) {
        ensure(count)
        data.copyInto(data, index + count, index, size)
        var offset = index
        ListEditor<Float> { data[offset++] = it }.apply(callback)
        check(offset == index + count) { "offset $offset != index $index + count $count" }
        size += count
    }

    override fun safeInsertLast(count: Int, callback: ListEditor<Float>.() -> Unit) {
        ensure(count)
        val offset = size
        ListEditor<Float> { data[size++] = it }.apply(callback)
        check(offset + count == size) { "offset $offset + count $count != size $size" }
    }

    override fun set(index: Int, element: Float): Float {
        checkIndex(index)
        val old = data[index]
        data[index] = element
        return old
    }

    override fun removeAt(index: Int): Float {
        checkIndex(index)
        val old = data[index]
        data.copyInto(data, index, index + 1, size)
        size--
        return old
    }

    override fun add(index: Int, element: Float) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insertLast(element: Float) {
        ensure(1)
        data[size++] = element
    }

    override fun insertLast(element1: Float, element2: Float) {
        ensure(2)
        data[size++] = element1
        data[size++] = element2
    }

    override fun insertLast(element1: Float, element2: Float, element3: Float) {
        ensure(3)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
    }

    override fun insertLast(element1: Float, element2: Float, element3: Float, element4: Float) {
        ensure(4)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
    }

    override fun insertLast(element1: Float, element2: Float, element3: Float, element4: Float, element5: Float) {
        ensure(5)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
    }

    override fun insertLast(element1: Float, element2: Float, element3: Float, element4: Float, element5: Float, element6: Float) {
        ensure(6)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
        data[size++] = element6
    }

    override fun insertLastAll(elements: Iterable<Float>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is FloatFastList -> {
                ensure(elements.size)
                elements.data.copyInto(data, size, 0, elements.size)
                size += elements.size
            }

            is Collection<Float> -> {
                ensure(elements.size)
                elements.forEachIndexed { i, e -> data[size + i] = e }
                size += elements.size
            }

            else -> {
                val floats = elements.toList()
                if (floats.isNotEmpty()) {
                    ensure(floats.size)
                    floats.forEachIndexed { i, e -> data[size + i] = e }
                    size += floats.size
                }
            }
        }
    }

    override fun insertLastAll(elements: FloatArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        elements.copyInto(data, size, 0, elements.size)
        size += elements.size
    }

    override fun insert(index: Int, element: Float) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insert(index: Int, element1: Float, element2: Float) {
        ensure(2)
        data.copyInto(data, index + 2, index, size)
        data[index] = element1
        data[index + 1] = element2
        size += 2
    }

    override fun insert(index: Int, element1: Float, element2: Float, element3: Float) {
        ensure(3)
        data.copyInto(data, index + 3, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        size += 3
    }

    override fun insert(index: Int, element1: Float, element2: Float, element3: Float, element4: Float) {
        ensure(4)
        data.copyInto(data, index + 4, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        size += 4
    }

    override fun insert(index: Int, element1: Float, element2: Float, element3: Float, element4: Float, element5: Float) {
        ensure(5)
        data.copyInto(data, index + 5, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        size += 5
    }

    override fun insert(index: Int, element1: Float, element2: Float, element3: Float, element4: Float, element5: Float, element6: Float) {
        ensure(6)
        data.copyInto(data, index + 6, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        data[index + 5] = element6
        size += 6
    }

    override fun insertAll(index: Int, elements: Iterable<Float>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is FloatFastList -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.data.copyInto(data, index, 0, elements.size)
                size += elements.size
            }

            is Collection<Float> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.forEachIndexed { i, e -> data[index + i] = e }
                size += elements.size
            }

            else -> {
                val floats = elements.toList()
                if (floats.isNotEmpty()) {
                    ensure(floats.size)
                    data.copyInto(data, index + floats.size, index, size)
                    floats.forEachIndexed { i, e -> data[index + i] = e }
                    size += floats.size
                }
            }
        }
    }

    override fun insertAll(index: Int, elements: FloatArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        data.copyInto(data, index + elements.size, index, size)
        elements.copyInto(data, index, 0, elements.size)
        size += elements.size
    }

    override fun get(index: Int): Float {
        checkIndex(index)
        return data[index]
    }
}