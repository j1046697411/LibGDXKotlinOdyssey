package cn.jzl.datastructure.list

import kotlin.math.max

class ObjectFastList<T>(capacity: Int = 7) : AbstractMutableFastList<T>(), ObjectMutableFastList<T> {
    private var data: Array<Any?> = arrayOfNulls(capacity)

    override var size: Int = 0
        private set

    private fun ensure(count: Int) {
        if (count + size > data.size) {
            data = data.copyOf(max(count + size, data.size * 2))
        }
    }

    override fun safeInsert(index: Int, count: Int, callback: ListEditor<T>.() -> Unit) {
        ensure(count)
        data.copyInto(data, index + count, index, size)
        var offset = index
        ListEditor<T> { data[offset++] = it }.apply(callback)
        check(offset == index + count) { "offset $offset != index $index + count $count" }
        size += count
    }

    override fun safeInsertLast(count: Int, callback: ListEditor<T>.() -> Unit) {
        ensure(count)
        val offset = size
        ListEditor<T> { data[size++] = it }.apply(callback)
        check(offset + count == size) { "offset $offset + count $count != size $size" }
    }

    override fun set(index: Int, element: T): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        val old = data[index] as T
        data[index] = element
        return old
    }

    override fun removeAt(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        val old = data[index] as T
        data.copyInto(data, index, index + 1, size)
        size--
        return old
    }

    // add方法已在AbstractMutableFastList中实现
    // override fun add(index: Int, element: T): Unit = insert(index, element)

    override fun insertLast(element: T) {
        ensure(1)
        data[size++] = element
    }

    override fun insertLast(element1: T, element2: T) {
        ensure(2)
        data[size++] = element1
        data[size++] = element2
    }

    override fun insertLast(element1: T, element2: T, element3: T) {
        ensure(3)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
    }

    override fun insertLast(element1: T, element2: T, element3: T, element4: T) {
        ensure(4)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
    }

    override fun insertLast(element1: T, element2: T, element3: T, element4: T, element5: T) {
        ensure(5)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
    }

    override fun insertLast(element1: T, element2: T, element3: T, element4: T, element5: T, element6: T) {
        ensure(6)
        data[size++] = element1
        data[size++] = element2
        data[size++] = element3
        data[size++] = element4
        data[size++] = element5
        data[size++] = element6
    }

    override fun insertLastAll(elements: Iterable<T>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is ObjectFastList<*> -> {
                ensure(elements.size)
                elements.data.copyInto(data, size, 0, elements.size)
                size += elements.size
            }

            is Collection<T> -> {
                ensure(elements.size)
                elements.forEachIndexed { i, e -> data[size + i] = e }
                size += elements.size
            }

            else -> {
                val list = elements.toList()
                if (list.isNotEmpty()) {
                    ensure(list.size)
                    list.forEachIndexed { i, e -> data[size + i] = e }
                    size += list.size
                }
            }
        }
    }

    override fun insertLastAll(elements: Array<out T>) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        elements.copyInto(data, size, 0, elements.size)
        size += elements.size
    }

    override fun insert(index: Int, element: T) {
        ensure(1)
        data.copyInto(data, index + 1, index, size)
        data[index] = element
        size++
    }

    override fun insert(index: Int, element1: T, element2: T) {
        ensure(2)
        data.copyInto(data, index + 2, index, size)
        data[index] = element1
        data[index + 1] = element2
        size += 2
    }

    override fun insert(index: Int, element1: T, element2: T, element3: T) {
        ensure(3)
        data.copyInto(data, index + 3, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        size += 3
    }

    override fun insert(index: Int, element1: T, element2: T, element3: T, element4: T) {
        ensure(4)
        data.copyInto(data, index + 4, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        size += 4
    }

    override fun insert(index: Int, element1: T, element2: T, element3: T, element4: T, element5: T) {
        ensure(5)
        data.copyInto(data, index + 5, index, size)
        data[index] = element1
        data[index + 1] = element2
        data[index + 2] = element3
        data[index + 3] = element4
        data[index + 4] = element5
        size += 5
    }

    override fun insert(index: Int, element1: T, element2: T, element3: T, element4: T, element5: T, element6: T) {
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

    override fun insertAll(index: Int, elements: Iterable<T>) {
        if (elements is Collection<*> && elements.isEmpty()) return
        when (elements) {
            is ObjectFastList<*> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.data.copyInto(data, index, 0, elements.size)
                size += elements.size
            }

            is Collection<T> -> {
                ensure(elements.size)
                data.copyInto(data, index + elements.size, index, size)
                elements.forEachIndexed { i, e -> data[index + i] = e }
                size += elements.size
            }

            else -> {
                val list = elements.toList()
                if (list.isNotEmpty()) {
                    ensure(list.size)
                    data.copyInto(data, index + list.size, index, size)
                    list.forEachIndexed { i, e -> data[index + i] = e }
                    size += list.size
                }
            }
        }
    }

    override fun insertAll(index: Int, elements: Array<out T>) {
        if (elements.isEmpty()) return
        ensure(elements.size)
        data.copyInto(data, index + elements.size, index, size)
        elements.copyInto(data, index, 0, elements.size)
        size += elements.size
    }

    override fun get(index: Int): T {
        checkIndex(index)
        @Suppress("UNCHECKED_CAST")
        return data[index] as T
    }
}