package cn.jzl.ecs.observers

import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityType
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.EntityQueryContext

data class Observer(
    val queries: List<Query<out EntityQueryContext>>,
    val involvedRelations: EntityType,
    val listenToEvents: Sequence<Entity>,
    val mustHoldData: Boolean = false,
    val handle: ObserverHandle
) : AutoCloseable {

    private val unsubscribes = mutableListOf<() -> Unit>()

    fun unsubscribe(onUnsubscribe: () -> Unit) {
        unsubscribes.add(onUnsubscribe)
    }

    override fun close() {
        unsubscribes.forEach { it() }
        unsubscribes.clear()
    }
}