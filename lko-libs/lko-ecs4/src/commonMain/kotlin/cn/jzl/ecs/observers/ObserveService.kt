package cn.jzl.ecs.observers

import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.Components
import cn.jzl.ecs.Entity
import cn.jzl.ecs.FamilyMatcher
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World
import cn.jzl.ecs.configure
import cn.jzl.ecs.id
import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.query
import cn.jzl.ecs.relation

@PublishedApi
internal class ObserveService(private val world: World) {

    private val queries = mutableMapOf<Relation, Query<ObserveEntity>>()

    val observerId: ComponentId = world.componentService.id<Observer>()
    val eventOf: ComponentId = world.componentService.configure<Components.EventOf> { it.tag() }

    val noComponent = Relation(world.componentService.components.any, world.componentService.components.any)

    private fun getQuery(target: Entity, eventId: ComponentId): Query<ObserveEntity> {
        return queries.getOrPut(Relation(target, eventId)) {
            world.query { ObserveEntity(world, target, eventId) }
        }
    }

    fun dispatch(entity: Entity, eventId: ComponentId, event: Any?, involved: Relation) {
        getQuery(entity, eventId).forEach {
            it.targetObserver?.handle(entity, event, involved)
            it.globalObserver?.handle(entity, event, involved)
        }
    }

    private fun Observer.handle(entity: Entity, event: Any?, involved: Relation) {
        if (mustHoldData && event == null) return
        if (involved != noComponent && involved !in involvedComponents) return
        if (queries.isNotEmpty()) {
            world.entityService.runOn(entity) {
                if (queries.all { query -> this in query }) {
                    queries.forEach { query ->
                        query.entity.updateCache(this)
                        query.entity.entityIndex = it
                    }
                    handle.handle(entity, event, involved)
                }
            }
        } else {
            handle.handle(entity, event, involved)
        }
    }

    private class ObserveEntity(world: World, target: Entity, private val eventId: ComponentId) : QueriedEntity(world) {
        val targetObserver: Observer? by oneRelationOrNull(target) { null }
        val globalObserver: Observer? by oneRelationOrNull(world.observeService.observerId) { null }
        override fun FamilyMatcher.FamilyBuilder.configure() {
            relation(world.observeService.eventOf, eventId)
        }
    }
}