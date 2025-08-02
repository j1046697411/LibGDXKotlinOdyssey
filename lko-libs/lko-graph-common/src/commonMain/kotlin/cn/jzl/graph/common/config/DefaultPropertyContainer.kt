package cn.jzl.graph.common.config

open class DefaultPropertyContainer : MutablePropertyContainer {
    private val properties = mutableMapOf<PropertyKey<*>, Any?>()

    override fun <V> set(key: PropertyKey<V>, value: V) {
        properties[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> getOrCreate(key: PropertyKey<V>, defaultValue: () -> V): V {
        return properties.getOrPut(key) { defaultValue() } as V
    }

    override fun minusAssign(key: PropertyKey<*>) {
        properties.remove(key)
    }

    override fun clear() {
        properties.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> get(key: PropertyKey<V>): V {
        return properties[key] as V
    }
}