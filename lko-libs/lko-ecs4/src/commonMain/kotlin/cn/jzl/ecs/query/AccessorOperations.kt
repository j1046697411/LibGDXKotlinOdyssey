package cn.jzl.ecs.query

import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation
import cn.jzl.ecs.id

abstract class AccessorOperations {

    @PublishedApi
    internal val accessors = mutableSetOf<Accessor>()

    @PublishedApi
    internal val cachingAccessors = mutableSetOf<CachedAccessor>()

    inline fun <reified K : Any> QueriedEntity.relation(target: Entity): RelationAccessor<K> = addAccessor {
        RelationAccessor(Relation(world.componentService.id<K>(), target))
    }

    inline fun <reified K : Any, reified T> QueriedEntity.relation(): RelationAccessor<K> = relation(world.componentService.id<T>())

    inline fun <reified C : Any> QueriedEntity.component(): RelationAccessor<C> = relation(world.componentService.components.componentId)

    inline fun <reified K> QueriedEntity.oneRelationOrNull(target: Entity, noinline default: () -> K): RelationOrDefaultAccessor<K> = addAccessor {
        RelationOrDefaultAccessor(Relation.Companion(world.componentService.id<K>(), target), default)
    }

    inline fun <reified K, reified T> QueriedEntity.oneRelationOrNull(noinline default: () -> K): RelationOrDefaultAccessor<K> {
        return oneRelationOrNull(world.componentService.id<T>(), default)
    }

    inline fun <reified C> QueriedEntity.oneComponentOrNull(noinline default: () -> C): RelationOrDefaultAccessor<C> {
        return oneRelationOrNull(world.componentService.components.componentId, default)
    }

    @PublishedApi
    internal inline fun <T : Accessor> addAccessor(create: () -> T): T {
        val accessor = create()
        accessors.add(accessor)
        if (accessor is CachedAccessor) cachingAccessors.add(accessor)
        return accessor
    }
}