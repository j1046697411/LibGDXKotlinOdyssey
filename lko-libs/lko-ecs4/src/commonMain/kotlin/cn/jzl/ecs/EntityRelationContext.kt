@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Shorthand1Query
import cn.jzl.ecs.query.SingleQuery

data class RelationWithData<T>(val relation: Relation, val data: T)

abstract class EntityRelationContext(override val world: World) : WorldOwner {

    @PublishedApi
    internal val componentService: ComponentService get() = world.componentService

    inline val Entity.entityType: EntityType get() = world.entityService.runOn(this) { archetypeType }

    inline operator fun Entity.contains(relation: Relation): Boolean = hasRelation(relation)
    inline fun <reified C> Entity.hasComponent(): Boolean = hasRelation(relations.component<C>())
    inline fun <reified T> Entity.hasTag(): Boolean = hasRelation(relations.component<T>())
    inline fun Entity.hasPrefab(): Boolean = hasComponent(components.prefab)
    inline fun <reified K> Entity.hasSharedComponent(): Boolean = hasRelation(relations.sharedComponent<K>())
    inline fun Entity.hasComponent(kind: ComponentId): Boolean = hasRelation(relations.component(kind))
    inline fun Entity.hasSharedComponent(kind: ComponentId): Boolean = hasRelation(relations.sharedComponent(kind))
    inline fun Entity.hasTag(kind: ComponentId): Boolean = hasRelation(relations.component(kind))

    inline fun Entity.hasRelation(relation: Relation): Boolean = world.relationService.hasRelation(this, relation)
    inline fun <reified K> Entity.hasRelation(target: Entity): Boolean = hasRelation(relations.relation<K>(target))
    inline fun <reified K, reified T> Entity.hasRelation(): Boolean = hasRelation(relations.relation<K, T>())

    inline fun <reified T> Entity.requireTag(lazyMessage: () -> Any = {}): Unit = require(hasTag<T>(), lazyMessage)
    inline fun <reified T> Entity.requireComponent(lazyMessage: () -> Any = {}): Unit = require(hasComponent<T>(), lazyMessage)
    inline fun <reified K, reified T> Entity.requireRelation(lazyMessage: () -> Any = {}): Unit = require(hasRelation<K, T>(), lazyMessage)
    inline fun <reified C> Entity.requireSharedComponent(lazyMessage: () -> Any = {}): Unit = require(hasSharedComponent<C>(), lazyMessage)

    inline fun <reified C> Entity.getComponent(): C {
        return world.relationService.getRelation(this, relations.component<C>()) as C
    }

    inline fun <reified C> Entity.getSharedComponent(): C {
        return world.relationService.getRelation(this, relations.sharedComponent<C>()) as C
    }

    inline fun <reified K> Entity.getRelation(target: Entity): K {
        return world.relationService.getRelation(this, relations.relation<K>(target)) as K
    }

    inline fun <reified K> Entity.getRelations(): Sequence<K> {
        val kind = relations.id<K>()
        return entityType.filter { relation -> relation.kind == kind }.map {
            world.relationService.getRelation(this, it) as K
        }
    }

    inline fun <reified K> Entity.getRelationsWithData(): Sequence<RelationWithData<K>> {
        val kind = relations.id<K>()
        return entityType.filter { relation -> relation.kind == kind }.map {
            RelationWithData(it, world.relationService.getRelation(this, it) as K)
        }
    }

    inline fun <reified K, reified T> Entity.getRelation(): K {
        return world.relationService.getRelation(this, relations.relation<K, T>()) as K
    }

    inline fun <reified K> Entity.getRelationUp(): Entity? {
        return world.relationService.getRelationUp(this, world.componentId<K>())
    }

    inline fun <reified K> Entity.getRelationDown(): SingleQuery<Shorthand1Query<K>> = world.relationService.getRelationDown(this)
    inline val Entity.children: SingleQuery<EntityQueryContext> get() = world.relationService.getRelationDown(this, components.childOf)

    inline val Entity.parent: Entity? get() = getRelationUp<Components.ChildOf>()
    inline val Entity.prefab: Entity? get() = getRelationUp<Components.InstanceOf>()
}