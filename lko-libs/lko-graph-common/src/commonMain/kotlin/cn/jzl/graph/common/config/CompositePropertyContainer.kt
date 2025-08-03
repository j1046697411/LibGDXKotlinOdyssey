package cn.jzl.graph.common.config

class CompositePropertyContainer(
    private val propertyContainers: List<PropertyContainer>
) : PropertyContainer {
    constructor(vararg propertyContainers: PropertyContainer) : this(propertyContainers.toList())

    override operator fun contains(key: PropertyKey<*>): Boolean = propertyContainers.any { it.contains(key) }

    override operator fun <V> get(key: PropertyKey<V>): V = propertyContainers.first { it.contains(key) }[key]

    override fun <V> getOrNull(key: PropertyKey<V>): V? =
        propertyContainers.firstOrNull { it.contains(key) }?.getOrNull(key)
}