package cn.jzl.ecs.v2

interface EntityStore {

    val size: Int

    val entities: Sequence<Entity>

    fun create(): Entity

    fun create(id: Int): Entity

    operator fun contains(entity: Entity): Boolean

    operator fun get(entityId: Int): Entity

    operator fun minusAssign(entity: Entity)

    fun clear()
}