package cn.jzl.datastructure.signal

import cn.jzl.datastructure.list.ObjectFastList
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

abstract class BSignal<Handler>(private val onRegister: () -> Unit) : AutoCloseable {

    private val nodes = ObjectFastList<Node>()
    private val operates = ObjectFastList<Pair<OperateType, Node>>()
    private val lock = ReentrantLock()

    private fun add(handler: Handler, once: Boolean): AutoCloseable {
        val node = Node(handler, once)
        onRegister()
        lock.withLock { operates.add(OperateType.Add to node) }
        return node
    }

    private fun remove(node: Node): Unit = lock.withLock { operates.add(OperateType.Remove to node) }

    internal inline fun foreachIterator(block: (Handler) -> Unit) {
        if (operates.isNotEmpty()) {
            lock.withLock {
                for ((operate, node) in operates) {
                    when (operate) {
                        OperateType.Add -> nodes.add(node)
                        OperateType.Remove -> nodes.remove(node)
                    }
                }
                operates.clear()
            }
        }
        if (nodes.isNotEmpty()) {
            var index = 0
            while (index < nodes.size) {
                val node = nodes[index]
                block(node.handler)
                if (node.once) {
                    node.close()
                }
                index++
            }
        }
    }

    fun add(handler: Handler): AutoCloseable = add(handler, false)

    fun once(handler: Handler): AutoCloseable = add(handler, true)

    override fun close() {
        lock.withLock { operates.clear() }
        nodes.clear()
    }

    @PublishedApi
    internal inner class Node(val handler: Handler, val once: Boolean) : AutoCloseable {
        override fun close(): Unit = remove(this)
    }

    @PublishedApi
    internal enum class OperateType {
        Add, Remove
    }
}

class Signal<E>(onRegister: () -> Unit = {}) : BSignal<(E) -> Unit>(onRegister) {
    operator fun invoke(event: E): Unit = foreachIterator { it(event) }
}

operator fun <E> Signal<E>.plusAssign(handler: (E) -> Unit) { add(handler) }
