package cn.jzl.ecs.entity

import cn.jzl.ecs.EntityId

interface EntityStore : Sequence<Entity> {
    fun create(): Entity
    fun create(entityId: Int): Entity

    operator fun contains(entityId: EntityId): Boolean

    fun destroy(entityId: EntityId)
}

