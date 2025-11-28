@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.ecs.query.QueryEntityContext
import cn.jzl.ecs.query.Query

abstract class EntityRelationContext(val world: World) {

    @PublishedApi
    internal val componentService: ComponentService get() = world.componentService

    inline val Entity.entityType: EntityType get() = world.entityService.runOn(this) { entityType }

    inline fun <reified C> component(): Relation = world.componentService.component<C>()
    inline fun <reified C> sharedComponent(): Relation = relation<C>(componentService.components.shadedId)
    inline fun relation(kind: ComponentId, target: Entity): Relation = Relation(kind, target)
    inline fun <reified K> relation(target: Entity): Relation = relation(componentService.id<K>(), target)
    inline fun <reified K, reified T> relation(): Relation = relation(componentService.id<K>(), componentService.id<T>())

    inline operator fun Entity.contains(relation: Relation): Boolean = world.relationService.hasRelation(this, relation)
    inline fun <reified C> Entity.hasComponent(): Boolean = world.relationService.hasRelation(this, component<C>())
    inline fun <reified T> Entity.hasTag(): Boolean = hasComponent<T>()

    inline fun Entity.hasRelation(relation: Relation): Boolean = relation in this
    inline fun <reified K> Entity.hasRelation(target: Entity): Boolean = hasRelation(relation<K>(target))
    inline fun <reified K, reified T> Entity.hasRelation(): Boolean = hasRelation(relation<K, T>())

    inline fun <reified C> Entity.getComponent(): C {
        return world.relationService.getRelation(this, component<C>()) as C
    }

    inline fun <reified C> Entity.getSharedComponent(): C {
        return world.relationService.getRelation(this, sharedComponent<C>()) as C
    }

    inline fun <reified K> Entity.getRelation(target: Entity): K {
        return world.relationService.getRelation(this, relation<K>(target)) as K
    }

    inline fun <reified K, reified T> Entity.getRelation(): K {
        return world.relationService.getRelation(this, relation<K, T>()) as K
    }

    inline fun <reified K> Entity.getRelationUp(): Entity? {
        return world.relationService.getRelationUp(this, world.componentId<K>())
    }

    inline fun <reified K> Entity.getRelationDown(): Query<QueryEntityContext> {
        return world.relationService.getRelationDown(this, world.componentId<K>())
    }

    inline val Entity.children: Query<QueryEntityContext> get() = getRelationDown<Components.ChildOf>()

    inline val Entity.parent: Entity? get() = getRelationUp<Components.ChildOf>()
}