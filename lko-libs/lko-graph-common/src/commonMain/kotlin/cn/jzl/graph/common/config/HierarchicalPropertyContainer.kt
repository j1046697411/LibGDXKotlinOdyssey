package cn.jzl.graph.common.config

class HierarchicalPropertyContainer(
    private val parent: PropertyContainer? = null
) : MutablePropertyContainer {
    private val propertyContainer = DefaultPropertyContainer()

    override fun contains(key: PropertyKey<*>): Boolean = key in propertyContainer || parent?.contains(key) == true

    override fun <V> set(key: PropertyKey<V>, value: V) {
        propertyContainer[key] = value
    }

    override fun minusAssign(key: PropertyKey<*>) {
        propertyContainer -= key
    }

    override fun clear() {
        propertyContainer.clear()
    }

    @Suppress("UNCHECKED_CAST", "USELESS_CAST")
    override fun <V> get(key: PropertyKey<V>): V {
        return if (key in propertyContainer) propertyContainer[key] as V else parent?.get(key) as V
    }

    override fun <V> getOrNull(key: PropertyKey<V>): V? {
        return propertyContainer.getOrNull(key) ?: parent?.getOrNull(key)
    }
}