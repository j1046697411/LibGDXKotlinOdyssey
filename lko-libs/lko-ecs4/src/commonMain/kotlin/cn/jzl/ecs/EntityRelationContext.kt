@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ecs

import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Query

abstract class EntityRelationContext(override val world: World) : WorldOwner {

    @PublishedApi
    internal val componentService: ComponentService get() = world.componentService

    inline val Entity.entityType: EntityType get() = world.entityService.runOn(this) { entityType }

    inline operator fun Entity.contains(relation: Relation): Boolean = world.relationService.hasRelation(this, relation)
    inline fun <reified C> Entity.hasComponent(): Boolean = world.relationService.hasRelation(this, relations.component<C>())
    inline fun <reified T> Entity.hasTag(): Boolean = hasComponent<T>()

    inline fun Entity.hasRelation(relation: Relation): Boolean = relation in this
    inline fun <reified K> Entity.hasRelation(target: Entity): Boolean = hasRelation(relations.relation<K>(target))
    inline fun <reified K, reified T> Entity.hasRelation(): Boolean = hasRelation(relations.relation<K, T>())

    inline fun <reified C> Entity.getComponent(): C {
        return world.relationService.getRelation(this, relations.component<C>()) as C
    }

    inline fun <reified C> Entity.getSharedComponent(): C {
        return world.relationService.getRelation(this, relations.sharedComponent<C>()) as C
    }

    inline fun <reified K> Entity.getRelation(target: Entity): K {
        return world.relationService.getRelation(this, relations.relation<K>(target)) as K
    }

    inline fun <reified K, reified T> Entity.getRelation(): K {
        return world.relationService.getRelation(this, relations.relation<K, T>()) as K
    }

    inline fun <reified K> Entity.getRelationUp(): Entity? {
        return world.relationService.getRelationUp(this, world.componentId<K>())
    }

    inline fun <reified K> Entity.getRelationDown(): Query<EntityQueryContext> {
        return world.relationService.getRelationDown(this, world.componentId<K>())
    }

    inline val Entity.children: Query<EntityQueryContext> get() = getRelationDown<Components.ChildOf>()

    inline val Entity.parent: Entity? get() = getRelationUp<Components.ChildOf>()
}