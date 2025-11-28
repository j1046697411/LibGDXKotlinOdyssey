package cn.jzl.ecs

interface EntityStore : Sequence<Entity> {
    val size: Int

    fun create(): Entity

    fun create(entityId: Int): Entity

    operator fun get(entityId: Int) : Entity?

    operator fun contains(entity: Entity): Boolean

    fun destroy(entity: Entity)
}