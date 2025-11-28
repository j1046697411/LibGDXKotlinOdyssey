package cn.jzl.ecs.query

import cn.jzl.ecs.Archetype
import cn.jzl.ecs.Relation
import kotlin.reflect.KProperty
import kotlin.reflect.KType


class RelationAccessor<T>(
    type: KType,
    relation: Relation,
    optionalGroup: OptionalGroup,
    provider: Archetype.(Relation) -> Int,
) : AbstractCachedAccessor(type, relation, optionalGroup, provider), ReadWriteAccessor<T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: QueryEntityContext, property: KProperty<*>): T {
        if (isMarkedNullable && componentIndex == -1) return null as T
        check(componentIndex != -1) { "Component index is not set $componentIndex $relation ${archetype?.entityType}" }
        val archetype = requireNotNull(this.archetype) { "Archetype is null" }
        return thisRef.world.relationService.getRelation(archetype, relation, thisRef.entityIndex, componentIndex) as T
    }

    override fun setValue(thisRef: QueryEntityContext, property: KProperty<*>, value: T) {
        if (value != null) {
            thisRef.batchEntityEditor.addRelation(thisRef.entity, relation, value)
        } else {
            thisRef.batchEntityEditor.removeRelation(thisRef.entity, relation)
        }
    }
}
