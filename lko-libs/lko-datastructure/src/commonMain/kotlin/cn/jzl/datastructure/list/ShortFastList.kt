package cn.jzl.datastructure.list

import kotlin.math.max

class ShortFastList(capacity: Int = 7) : AbstractMutableFastList<Short>(), ShortMutableFastList {
    private var data = ShortArray(capacity)

    override var size: Int = 0
        private set

    override fun unsafeListEditor(): ListEditor<Short> {
        return object : ListEditor<Short> {
            override fun unsafeInsertLast(element: Short) {
                data[size++] = element
            }

            override fun unsafeSet(index: Int, element: Short) {
                data[index] = element
            }
        }
    }

    override fun migrate(index: Int, count: Int, callback: InsertEditor<Short>.() -> Unit) {
        data.copyInto(data, index + count, index, size)
        unsafeListEditor.apply(callback)
        size += count
    }

    override fun ensure(count: Int) {
        if (count + size > data.size) {
            data = data.copyOf(max(count + size, data.size * 2))
        }
    }

    override fun set(index: Int, element: Short): Short {
        checkIndex(index)
        val old = data[index]
        data[index] = element
        return old
    }

    override fun removeAt(index: Int): Short {
        checkIndex(index)
        val old = data[index]
        data.copyInto(data, index, index + 1, size)
        size--
        return old
    }

    override fun add(index: Int, element: Short) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insertLast(element: Short) {
        ensure(1)
        data[size++] = element
    }

    override fun insertLast(element1: Short, element2: Short) {
        ensure(2)
        data[size++] = element1
        data[size++] = element2
    }

    override fun insertLast(element1: Short, element2: Short, element3: Short) {
        ensure(3)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
    }

    override fun insertLast(element1: Short, element2: Short, element3: Short, element4: Short) {
        ensure(4)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
    }

    override fun insertLast(element1: Short, element2: Short, element3: Short, element4: Short, element5: Short) {
        ensure(5)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
    }

    override fun insertLast(element1: Short, element2: Short, element3: Short, element4: Short, element5: Short, element6: Short) {
        ensure(6)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
        data[size++] = element6
    }

    override fun insertLastAll(elements: Iterable<Short>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is ShortFastList -> {
                ensure(elements.size)
                elements.data.copyInto(data, size, 0, elements.size)
                size += elements.size
            }

            is Collection<Short> -> {
                ensure(elements.size)
                elements.forEachIndexed { i, e -> data[size + i] = e }
                size += elements.size
            }

            else -> {
                val shorts = elements.toList()
                if (shorts.isNotEmpty()) {
                    ensure(shorts.size)
                    shorts.forEachIndexed { i, e -> data[size + i] = e }
                    size += shorts.size
                }
            }
        }
    }

    override fun insertLastAll(elements: ShortArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        elements.copyInto(data, size, 0, elements.size)
        size += elements.size
    }

    override fun insert(index: Int, element: Short) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insert(index: Int, element1: Short, element2: Short) {
        ensure(2)
        data.copyInto(data, index + 2, index, size)
        data[index] = element1
        data[index + 1] = element2
        size += 2
    }

    override fun insert(index: Int, element1: Short, element2: Short, element3: Short) {
        ensure(3)
        data.copyInto(data, index + 3, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        size += 3
    }

    override fun insert(index: Int, element1: Short, element2: Short, element3: Short, element4: Short) {
        ensure(4)
        data.copyInto(data, index + 4, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        size += 4
    }

    override fun insert(index: Int, element1: Short, element2: Short, element3: Short, element4: Short, element5: Short) {
        ensure(5)
        data.copyInto(data, index + 5, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        size += 5
    }

    override fun insert(index: Int, element1: Short, element2: Short, element3: Short, element4: Short, element5: Short, element6: Short) {
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

    override fun insertAll(index: Int, elements: Iterable<Short>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is ShortFastList -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.data.copyInto(data, index, 0, elements.size)
                size += elements.size
            }

            is Collection<Short> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.forEachIndexed { i, e -> data[index + i] = e }
                size += elements.size
            }

            else -> {
                val shorts = elements.toList()
                if (shorts.isNotEmpty()) {
                    ensure(shorts.size)
                    data.copyInto(data, index + shorts.size, index, size)
                    shorts.forEachIndexed { i, e -> data[index + i] = e }
                    size += shorts.size
                }
            }
        }
    }

    override fun insertAll(index: Int, elements: ShortArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        data.copyInto(data, index + elements.size, index, size)
        elements.copyInto(data, index, 0, elements.size)
        size += elements.size
    }

    override fun get(index: Int): Short {
        checkIndex(index)
        return data[index]
    }
}