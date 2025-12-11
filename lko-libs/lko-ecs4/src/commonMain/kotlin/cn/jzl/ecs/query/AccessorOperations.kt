package cn.jzl.ecs.query

import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.Entity
import cn.jzl.ecs.Relation
import cn.jzl.ecs.componentId
import cn.jzl.ecs.components
import cn.jzl.ecs.id
import cn.jzl.ecs.relations
import kotlin.reflect.typeOf

abstract class AccessorOperations {

    @PublishedApi
    internal val accessors = mutableSetOf<Accessor>()

    @PublishedApi
    internal val cachingAccessors = mutableSetOf<CachedAccessor>()

    inline fun <reified K> EntityQueryContext.relation(
        target: Entity,
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<K> = addAccessor {
        val relation = Relation(world.componentId<K>(), target)
        RelationAccessor(typeOf<K>(), relation, group) { getComponentIndex(relation) }
    }

    fun EntityQueryContext.relationUp(kind: ComponentId): RelationUpAccessor = addAccessor {
        RelationUpAccessor(kind)
    }

    inline fun <reified K> EntityQueryContext.relationUp(): RelationUpAccessor = relationUp(world.componentId<K>())

    fun EntityQueryContext.prefab(): RelationUpAccessor = relationUp(components.instanceOf)

    fun EntityQueryContext.ifRelationExist(relation: Relation): IfRelationExistAccessor = addAccessor {
        IfRelationExistAccessor(relation)
    }

    inline fun <reified K, reified T> EntityQueryContext.relation(
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<K> = relation(world.componentId<T>(), group)

    inline fun <reified C> EntityQueryContext.component(
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<C> = relation(world.componentService.components.componentId, group)

    inline fun <reified S> EntityQueryContext.sharedComponent(
        group: OptionalGroup = OptionalGroup.Ignore
    ): RelationAccessor<S> = relation(world.componentService.components.sharedId, group)

    @PublishedApi
    internal inline fun <T : Accessor> addAccessor(create: () -> T): T {
        val accessor = create()
        accessors.add(accessor)
        if (accessor is CachedAccessor) cachingAccessors.add(accessor)
        return accessor
    }
}