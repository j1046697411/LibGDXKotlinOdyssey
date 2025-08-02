package cn.jzl.graph.common.config

interface PropertyContainer {
    operator fun <V> get(key: PropertyKey<V>): V
}