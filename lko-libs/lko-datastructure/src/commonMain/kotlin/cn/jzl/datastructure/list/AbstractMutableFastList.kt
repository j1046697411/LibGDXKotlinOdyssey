package cn.jzl.datastructure.list

import kotlin.IndexOutOfBoundsException

abstract class AbstractMutableFastList<T> : AbstractMutableList<T>(), MutableFastList<T> {

    override val unsafeListEditor by lazy { unsafeListEditor() }

    protected fun checkIndex(index: Int) {
        if (0 > index || index >= size) throw IndexOutOfBoundsException("index $index is out of bounds for size $size")
    }

    protected abstract fun unsafeListEditor(): ListEditor<T>
    protected abstract fun ensure(count: Int)
    protected abstract fun migrate(index: Int, count: Int, callback: InsertEditor<T>.() -> Unit)

    override fun add(element: T): Boolean {
        insertLast(element)
        return true
    }

    override fun add(index: Int, element: T): Unit = insert(index, element)

    override fun safeInsertLast(count: Int, callback: InsertLastEditor<T>.() -> Unit) {
        ensure(count)
        unsafeListEditor.apply(callback)
    }

    override fun safeInsert(index: Int, count: Int, callback: InsertEditor<T>.() -> Unit) {
        ensure(count)
        migrate(index, count) { InsertionEditor(index, count, this).apply(callback) }
    }

    private data class InsertionEditor<T>(
        val offset: Int,
        private val count: Int,
        private val editor: InsertEditor<T>
    ) : InsertEditor<T> {
        override fun unsafeSet(index: Int, element: T) {
            check(0 <= index && index < count) { "index $index is out of bounds for count $count" }
            editor.unsafeSet(index + offset, element)
        }
    }
}