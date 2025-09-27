package cn.jzl.graph.common.config

open class DefaultPropertyContainer : MutablePropertyContainer {

    private val properties = mutableMapOf<PropertyKey<*>, Any?>()

    override fun contains(key: PropertyKey<*>): Boolean {
        return key in properties
    }

    override fun <V> set(key: PropertyKey<V>, value: V) {
        properties[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> getOrNull(key: PropertyKey<V>): V? {
        return properties[key] as? V
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