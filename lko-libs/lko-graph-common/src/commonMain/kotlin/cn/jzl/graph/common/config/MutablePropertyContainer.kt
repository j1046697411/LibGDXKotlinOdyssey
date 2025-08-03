package cn.jzl.graph.common.config

interface MutablePropertyContainer : PropertyContainer {
    operator fun <V> set(key: PropertyKey<V>, value: V)

    operator fun minusAssign(key: PropertyKey<*>)

    fun clear()
}