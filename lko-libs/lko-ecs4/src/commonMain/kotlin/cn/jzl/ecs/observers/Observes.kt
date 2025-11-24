package cn.jzl.ecs.observers

import cn.jzl.ecs.*
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.ShorthandQuery

inline fun <reified Context, reified E : ShorthandQuery> ObserverBuilder<Context>.exec(
    query: Query<E>,
    noinline handle: Context.(E) -> Unit
): Observer = filter(query).exec { handle(query.entity) }

inline fun <reified Context, reified E1 : ShorthandQuery, reified E2 : ShorthandQuery> ObserverBuilder<Context>.exec(
    query1: Query<E1>,
    query2: Query<E2>,
    noinline handle: Context.(E1, E2) -> Unit
): Observer = filter(query1, query2).exec { handle(query1.entity, query2.entity) }

inline fun <reified E> World.observe(): ObserverEventsBuilder<ObserverContext> = observe {
    yield(componentService.id<E>())
}

fun World.observe(
    configure: suspend SequenceScope<ComponentId>.() -> Unit
): ObserverEventsBuilder<ObserverContext> = observe(observeService.observerId, configure)

inline fun <reified E> World.observeWithData(): ObserverEventsBuilder<ObserverContextWithData<E>> = observeWithData {
    yield(componentService.id<E>())
}

inline fun <reified E> World.observeWithData(
    noinline configure: suspend SequenceScope<ComponentId>.() -> Unit
): ObserverEventsBuilder<ObserverContextWithData<E>> = observeWithData<E>(observeService.observerId, configure)

fun World.observe(entity: Entity, configure: suspend SequenceScope<ComponentId>.() -> Unit): ObserverEventsBuilder<ObserverContext> {
    val listenToEvents = sequence { configure() }
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
internal fun World.attachObserver(entity: Entity, observer: Observer): Entity = childOf(entity) {
    it.addRelation<Observer>(entity, observer)
    observer.listenToEvents.forEach { eventId ->
        it.addRelation(observeService.eventOf, eventId)
    }
}

inline fun <reified E> World.emit(entity: Entity, event: E, involvedRelation: Relation = observeService.notInvolvedRelation) {
    observeService.dispatch(entity, componentService.id<E>(), event, involvedRelation)
}

inline fun <reified E> World.emit(entity: Entity, involvedRelation: Relation = observeService.notInvolvedRelation) {
    observeService.dispatch(entity, componentService.id<E>(), null, involvedRelation)
}
