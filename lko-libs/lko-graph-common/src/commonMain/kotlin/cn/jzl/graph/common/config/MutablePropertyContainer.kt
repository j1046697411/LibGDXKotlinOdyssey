package cn.jzl.graph.common.config

interface MutablePropertyContainer : PropertyContainer {
    operator fun <V> set(key: PropertyKey<V>, value: V)

    fun <V> getOrCreate(key: PropertyKey<V>, defaultValue: ()-> V): V

    operator fun minusAssign(key: PropertyKey<*>)

    fun clear()
}

