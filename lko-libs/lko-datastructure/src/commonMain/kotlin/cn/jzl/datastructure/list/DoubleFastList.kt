package cn.jzl.datastructure.list

import kotlin.math.max

class DoubleFastList(capacity: Int = 7) : AbstractMutableFastList<Double>(), DoubleMutableFastList {
    private var data = DoubleArray(capacity)

    override var size: Int = 0
        private set

    override fun unsafeListEditor(): ListEditor<Double> {
        return object : ListEditor<Double> {
            override fun unsafeInsertLast(element: Double) {
                data[size++] = element
            }

            override fun unsafeSet(index: Int, element: Double) {
                data[index] = element
            }
        }
    }

    override fun migrate(index: Int, count: Int, callback: InsertEditor<Double>.() -> Unit) {
        data.copyInto(data, index + count, index, size)
        unsafeListEditor.apply(callback)
        size += count
    }

    override fun ensure(count: Int) {
        if (count + size > data.size) {
            data = data.copyOf(max(count + size, data.size * 2))
        }
    }

    override fun set(index: Int, element: Double): Double {
        checkIndex(index)
        val old = data[index]
        data[index] = element
        return old
    }

    override fun removeAt(index: Int): Double {
        checkIndex(index)
        val old = data[index]
        data.copyInto(data, index, index + 1, size)
        size--
        return old
    }

    override fun add(index: Int, element: Double) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insertLast(element: Double) {
        ensure(1)
        data[size++] = element
    }

    override fun insertLast(element1: Double, element2: Double) {
        ensure(2)
        data[size++] = element1
        data[size++] = element2
    }

    override fun insertLast(element1: Double, element2: Double, element3: Double) {
        ensure(3)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
    }

    override fun insertLast(element1: Double, element2: Double, element3: Double, element4: Double) {
        ensure(4)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
    }

    override fun insertLast(element1: Double, element2: Double, element3: Double, element4: Double, element5: Double) {
        ensure(5)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
    }

    override fun insertLast(element1: Double, element2: Double, element3: Double, element4: Double, element5: Double, element6: Double) {
        ensure(6)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
        data[size++] = element6
    }

    override fun insertLastAll(elements: Iterable<Double>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is DoubleFastList -> {
                ensure(elements.size)
                elements.data.copyInto(data, size, 0, elements.size)
                size += elements.size
            }

            is Collection<Double> -> {
                ensure(elements.size)
                elements.forEachIndexed { i, e -> data[size + i] = e }
                size += elements.size
            }

            else -> {
                val doubles = elements.toList()
                if (doubles.isNotEmpty()) {
                    ensure(doubles.size)
                    doubles.forEachIndexed { i, e -> data[size + i] = e }
                    size += doubles.size
                }
            }
        }
    }

    override fun insertLastAll(elements: DoubleArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        elements.copyInto(data, size, 0, elements.size)
        size += elements.size
    }

    override fun insert(index: Int, element: Double) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insert(index: Int, element1: Double, element2: Double) {
        ensure(2)
        data.copyInto(data, index + 2, index, size)
        data[index] = element1
        data[index + 1] = element2
        size += 2
    }

    override fun insert(index: Int, element1: Double, element2: Double, element3: Double) {
        ensure(3)
        data.copyInto(data, index + 3, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        size += 3
    }

    override fun insert(index: Int, element1: Double, element2: Double, element3: Double, element4: Double) {
        ensure(4)
        data.copyInto(data, index + 4, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        size += 4
    }

    override fun insert(index: Int, element1: Double, element2: Double, element3: Double, element4: Double, element5: Double) {
        ensure(5)
        data.copyInto(data, index + 5, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        size += 5
    }

    override fun insert(index: Int, element1: Double, element2: Double, element3: Double, element4: Double, element5: Double, element6: Double) {
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

    override fun insertAll(index: Int, elements: Iterable<Double>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is DoubleFastList -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.data.copyInto(data, index, 0, elements.size)
                size += elements.size
            }

            is Collection<Double> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.forEachIndexed { i, e -> data[index + i] = e }
                size += elements.size
            }

            else -> {
                val doubles = elements.toList()
                if (doubles.isNotEmpty()) {
                    ensure(doubles.size)
                    data.copyInto(data, index + doubles.size, index, size)
                    doubles.forEachIndexed { i, e -> data[index + i] = e }
                    size += doubles.size
                }
            }
        }
    }

    override fun insertAll(index: Int, elements: DoubleArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        data.copyInto(data, index + elements.size, index, size)
        elements.copyInto(data, index, 0, elements.size)
        size += elements.size
    }

    override fun get(index: Int): Double {
        checkIndex(index)
        return data[index]
    }
}