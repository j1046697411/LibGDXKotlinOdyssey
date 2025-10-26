package cn.jzl.ecs.v2

interface ComponentsHolder<C> {

    val componentType: ComponentType<C>

    operator fun contains(entity: Entity): Boolean

    operator fun get(entity: Entity): C

    operator fun set(entity: Entity, component: C): C?

    fun getOrNull(entity: Entity): C?

    fun remove(entity: Entity): C?
}