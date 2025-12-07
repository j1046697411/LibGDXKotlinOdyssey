package cn.jzl.ecs.observers

import cn.jzl.ecs.*
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Query

inline fun <reified Context, reified E : EntityQueryContext> ExecutableObserver<Context>.exec(
    query: Query<E>,
    noinline handle: Context.(E) -> Unit
): Observer = filter(query).exec { handle(query.context) }

inline fun <reified Context, reified E1 : EntityQueryContext, reified E2 : EntityQueryContext> ExecutableObserver<Context>.exec(
    query1: Query<E1>,
    query2: Query<E2>,
    noinline handle: Context.(E1, E2) -> Unit
): Observer = filter(query1, query2).exec { handle(query1.context, query2.context) }

inline fun <reified E> World.observe(): ObserverEventsBuilder<ObserverContext> = observe<E>(components.observerId)

inline fun World.observe(
    crossinline configure: suspend SequenceScope<ComponentId>.(RelationProvider) -> Unit
): ObserverEventsBuilder<ObserverContext> = observe(components.observerId, configure)

inline fun <reified E> World.observeWithData(): ObserverEventsBuilder<ObserverContextWithData<E>> = observeWithData(components.observerId)

inline fun <reified E> World.observeWithData(
    noinline configure: suspend SequenceScope<ComponentId>.() -> Unit
): ObserverEventsBuilder<ObserverContextWithData<E>> = observeWithData<E>(components.observerId, configure)

inline fun <reified E> World.observe(entity: Entity): ObserverEventsBuilder<ObserverContext> = observe(entity) {
    yield(componentService.id<E>())
}

inline fun <reified E> World.observeWithData(entity: Entity): ObserverEventsBuilder<ObserverContextWithData<E>> = observeWithData(entity) {
    yield(componentService.id<E>())
}

inline fun World.observe(entity: Entity, crossinline configure: suspend SequenceScope<ComponentId>.(RelationProvider) -> Unit): ObserverEventsBuilder<ObserverContext> {
    val listenToEvents = sequence { configure(relations) }
    return ObserverWithoutData(this, listenToEvents) {
        attachObserver(entity, it)
    }
}

inline fun <reified E> World.observeWithData(entity: Entity, noinline configure: suspend SequenceScope<ComponentId>.() -> Unit): ObserverEventsBuilder<ObserverContextWithData<E>> {
    val listenToEvents = sequence { configure() }
    return ObserverWithData(this, listenToEvents) {
        attachObserver(entity, it)
    }
}

@PublishedApi
internal fun World.attachObserver(entity: Entity, observer: Observer): Entity = entityService.childOf(entity, false) {
    it.addRelation(entity, observer)
    observer.listenToEvents.forEach { eventId ->
        it.addRelation(components.eventId, eventId)
    }
    observer.unsubscribe { world.destroy(it) }
}

inline fun <reified E> World.emit(entity: Entity, event: E, involvedRelation: Relation = observeService.notInvolvedRelation) {
    observeService.dispatch(entity, componentService.id<E>(), event, involvedRelation)
}

inline fun <reified E> World.emit(entity: Entity, involvedRelation: Relation = observeService.notInvolvedRelation) {
    observeService.dispatch(entity, componentService.id<E>(), null, involvedRelation)
}