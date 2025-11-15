@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs.entity

import cn.jzl.ecs.EntityId
import cn.jzl.ecs.EntityType
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World
import cn.jzl.ecs.component.ComponentId
import cn.jzl.ecs.component.ComponentService
import cn.jzl.ecs.component.id


abstract class EntityRelationContext(val world: World) {

    @PublishedApi
    internal val componentService: ComponentService get() = world.componentService

    inline val Entity.entityType: EntityType get() = world.entityService.runOn(entityId) { entityType }

    inline fun <reified C> component(): Relation = world.componentService.component<C>()
    inline fun relation(kind: ComponentId, target: EntityId): Relation = Relation.Companion(kind, target)
    inline fun <reified K> relation(target: EntityId): Relation = relation(componentService.id<K>(), target)
    inline fun <reified K, reified T> relation(): Relation = relation(componentService.id<K>(), componentService.id<T>())

    inline operator fun Entity.contains(relation: Relation): Boolean = world.relationService.hasRelation(entityId, relation)
    inline fun <reified C> Entity.hasComponent() : Boolean = world.relationService.hasRelation(entityId, component<C>())
    inline fun <reified T> Entity.hasTag() : Boolean = hasComponent<T>()

    inline fun Entity.hasRelation(relation: Relation): Boolean = relation in this
    inline fun <reified K> Entity.hasRelation(target: EntityId): Boolean = hasRelation(relation<K>(target))
    inline fun <reified K, reified T> Entity.hasRelation(): Boolean = hasRelation(relation<K, T>())

    inline fun <reified C> Entity.getComponent(): C {
        return world.relationService.getRelation(entityId, component<C>()) as C
    }

    inline fun <reified K> Entity.getRelation(target: EntityId): K {
        return world.relationService.getRelation(entityId, relation<K>(target)) as K
    }

    inline fun <reified K, reified T> Entity.getRelation(): K {
        return world.relationService.getRelation(entityId, relation<K, T>()) as K
    }
}