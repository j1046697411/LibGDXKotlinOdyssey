package cn.jzl.ecs.observers

import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityType
import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.Query

data class Observer(
    val queries: List<Query<out QueriedEntity>>,
    val involvedComponents: EntityType,
    val listenToEvents: Sequence<Entity>,
    val mustHoldData: Boolean = false,
    val handle: ObserverHandle
)