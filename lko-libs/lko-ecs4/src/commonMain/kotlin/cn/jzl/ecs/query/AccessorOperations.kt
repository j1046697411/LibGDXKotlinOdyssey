package cn.jzl.ecs.query

import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation
import cn.jzl.ecs.id
import kotlin.reflect.typeOf

abstract class AccessorOperations {

    @PublishedApi
    internal val accessors = mutableSetOf<Accessor>()

    @PublishedApi
    internal val cachingAccessors = mutableSetOf<CachedAccessor>()

    inline fun <reified K> QueriedEntity.relation(
        target: Entity,
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<K> = addAccessor {
        val relation = Relation(world.componentService.id<K>(), target)
        if (world.componentService.isShadedComponent(relation)) {
            RelationAccessor(typeOf<K>(), relation, group) { entityType.indexOf(it) }
        } else {
            RelationAccessor(typeOf<K>(), relation, group) { table.entityType.indexOf(it) }
        }
    }

    inline fun <reified K, reified T> QueriedEntity.relation(
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<K> = relation(world.componentService.id<T>(), group)

    inline fun <reified C> QueriedEntity.component(
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<C> = relation(world.componentService.components.componentId, group)

    @PublishedApi
    internal inline fun <T : Accessor> addAccessor(create: () -> T): T {
        val accessor = create()
        accessors.add(accessor)
        if (accessor is CachedAccessor) cachingAccessors.add(accessor)
        return accessor
    }
}