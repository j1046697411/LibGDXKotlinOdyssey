package cn.jzl.ecs.v2

internal class ComponentsHolderImpl<C>(
    private val world: World,
    override val componentType: ComponentType<C>
) : ComponentsHolder<C> {
    private val components = hashMapOf<Int, C>()
    override operator fun contains(entity: Entity): Boolean = entity.id in components
    override operator fun get(entity: Entity): C = components[entity.id] ?: throw NoSuchElementException("Entity $entity does not have component $componentType")
    override operator fun set(entity: Entity, component: C): C? {
        val oldComponent = components.put(entity.id, component)
        oldComponent?.onDetach(entity)
        component.onAttach(entity)
        return oldComponent
    }

    override fun getOrNull(entity: Entity): C? = components[entity.id]

    override fun remove(entity: Entity): C? {
        val component = components.remove(entity.id)
        component?.onDetach(entity)
        return component
    }

    private fun Any?.onAttach(entity: Entity) {
        if (this is Component<*>) world.onAttach(entity)
    }

    private fun Any?.onDetach(entity: Entity) {
        if (this is Component<*>) world.onDetach(entity)
    }
}