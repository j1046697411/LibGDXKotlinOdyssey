package cn.jzl.ecs.observers

import cn.jzl.ecs.EntityType
import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.Query

data class ObserverBuilder<Context>(
    val events: ObserverEventsBuilder<Context>,
    val involvedComponents: EntityType,
    val matchQueries: List<Query<out QueriedEntity>>
) : ExecutableObserver<Context> {
    override fun filter(vararg query: Query<out QueriedEntity>): ExecutableObserver<Context> = copy(matchQueries = matchQueries + query)

    override fun exec(handle: Context.() -> Unit): Observer {
        val observer = Observer(
            matchQueries,
            involvedComponents,
            events.listenToEvents,
            events.mustHoldData
        ) { entity, event, involved -> events.provideContext(entity, event, involved).handle() }
        events.onBuild(observer)
        return observer
    }
}