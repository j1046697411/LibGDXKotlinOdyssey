package cn.jzl.graph.shader.core

interface ModelContainer<M> : Sequence<M> {
    operator fun minusAssign(model: M)
    operator fun plusAssign(model: M)
    fun clear()
}