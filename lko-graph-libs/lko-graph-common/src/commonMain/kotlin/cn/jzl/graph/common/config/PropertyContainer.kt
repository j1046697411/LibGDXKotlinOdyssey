package cn.jzl.graph.common.config

interface PropertyContainer {
    operator fun contains(key: PropertyKey<*>): Boolean
    operator fun <V> get(key: PropertyKey<V>): V
    fun <V> getOrNull(key: PropertyKey<V>): V?
}

