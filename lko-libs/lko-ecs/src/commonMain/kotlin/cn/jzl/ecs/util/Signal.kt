package cn.jzl.ecs.util

abstract class BSignal<Handler>(private val onRegister: () -> Unit) : AutoCloseable {

    private val nodes = arrayListOf<Node>()
    private val operates = arrayListOf<Pair<OperateType, Node>>()

    private fun add(handler: Handler, once: Boolean): AutoCloseable {
        val node = Node(handler, once)
        onRegister()
        synchronized(operates) { operates.add(OperateType.Add to node) }
        return node
    }

    private fun remove(node: Node) {
        synchronized(operates) { operates.add(OperateType.Remove to node) }
    }

    internal inline fun foreachIterator(block: (Handler) -> Unit) {
        if (operates.isNotEmpty()) {
            synchronized(operates) {
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

    fun once(handler: Handler): java.lang.AutoCloseable = add(handler, true)

    override fun close() {
        synchronized(operates) { operates.clear() }
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