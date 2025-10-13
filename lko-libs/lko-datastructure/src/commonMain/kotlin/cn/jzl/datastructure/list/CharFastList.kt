package cn.jzl.datastructure.list

import kotlin.math.max

class CharFastList(capacity: Int = 7) : AbstractMutableFastList<Char>(), CharMutableFastList {
    private var data = CharArray(capacity)

    override var size: Int = 0
        private set

    private fun ensure(count: Int) {
        if (count + size > data.size) {
            data = data.copyOf(max(count + size, data.size * 2))
        }
    }

    override fun safeInsert(index: Int, count: Int, callback: ListEditor<Char>.() -> Unit) {
        ensure(count)
        data.copyInto(data, index + count, index, size)
        var offset = index
        ListEditor<Char> { data[offset++] = it }.apply(callback)
        check(offset == index + count) { "offset $offset != index $index + count $count" }
        size += count
    }

    override fun safeInsertLast(count: Int, callback: ListEditor<Char>.() -> Unit) {
        ensure(count)
        val offset = size
        ListEditor<Char> { data[size++] = it }.apply(callback)
        check(offset + count == size) { "offset $offset + count $count != size $size" }
    }


    override fun set(index: Int, element: Char): Char {
        checkIndex(index)
        val old = data[index]
        data[index] = element
        return old
    }

    override fun removeAt(index: Int): Char {
        checkIndex(index)
        val old = data[index]
        data.copyInto(data, index, index + 1, size)
        size--
        return old
    }

    override fun add(index: Int, element: Char) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insertLast(element: Char) {
        ensure(1)
        data[size++] = element
    }

    override fun insertLast(element1: Char, element2: Char) {
        ensure(2)
        data[size++] = element1
        data[size++] = element2
    }

    override fun insertLast(element1: Char, element2: Char, element3: Char) {
        ensure(3)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
    }

    override fun insertLast(element1: Char, element2: Char, element3: Char, element4: Char) {
        ensure(4)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
    }

    override fun insertLast(element1: Char, element2: Char, element3: Char, element4: Char, element5: Char) {
        ensure(5)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
    }

    override fun insertLast(element1: Char, element2: Char, element3: Char, element4: Char, element5: Char, element6: Char) {
        ensure(6)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
        data[size++] = element6
    }

    override fun insertLastAll(elements: Iterable<Char>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is CharFastList -> {
                ensure(elements.size)
                elements.data.copyInto(data, size, 0, elements.size)
                size += elements.size
            }

            is Collection<Char> -> {
                ensure(elements.size)
                elements.forEachIndexed { i, e -> data[size + i] = e }
                size += elements.size
            }

            else -> {
                val chars = elements.toList()
                if (chars.isNotEmpty()) {
                    ensure(chars.size)
                    chars.forEachIndexed { i, e -> data[size + i] = e }
                    size += chars.size
                }
            }
        }
    }

    override fun insertLastAll(elements: CharArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        elements.copyInto(data, size, 0, elements.size)
        size += elements.size
    }

    override fun insert(index: Int, element: Char) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insert(index: Int, element1: Char, element2: Char) {
        ensure(2)
        data.copyInto(data, index + 2, index, size)
        data[index] = element1
        data[index + 1] = element2
        size += 2
    }

    override fun insert(index: Int, element1: Char, element2: Char, element3: Char) {
        ensure(3)
        data.copyInto(data, index + 3, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        size += 3
    }

    override fun insert(index: Int, element1: Char, element2: Char, element3: Char, element4: Char) {
        ensure(4)
        data.copyInto(data, index + 4, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        size += 4
    }

    override fun insert(index: Int, element1: Char, element2: Char, element3: Char, element4: Char, element5: Char) {
        ensure(5)
        data.copyInto(data, index + 5, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        size += 5
    }

    override fun insert(index: Int, element1: Char, element2: Char, element3: Char, element4: Char, element5: Char, element6: Char) {
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

    override fun insertAll(index: Int, elements: Iterable<Char>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is CharFastList -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.data.copyInto(data, index, 0, elements.size)
                size += elements.size
            }

            is Collection<Char> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.forEachIndexed { i, e -> data[index + i] = e }
                size += elements.size
            }

            else -> {
                val chars = elements.toList()
                if (chars.isNotEmpty()) {
                    ensure(chars.size)
                    data.copyInto(data, index + chars.size, index, size)
                    chars.forEachIndexed { i, e -> data[index + i] = e }
                    size += chars.size
                }
            }
        }
    }

    override fun insertAll(index: Int, elements: CharArray) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        data.copyInto(data, index + elements.size, index, size)
        elements.copyInto(data, index, 0, elements.size)
        size += elements.size
    }

    override fun get(index: Int): Char {
        checkIndex(index)
        return data[index]
    }
}