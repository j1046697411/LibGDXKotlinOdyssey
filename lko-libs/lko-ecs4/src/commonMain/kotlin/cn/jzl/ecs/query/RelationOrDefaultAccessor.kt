package cn.jzl.ecs.query

import cn.jzl.ecs.Relation
import kotlin.reflect.KProperty

class RelationOrDefaultAccessor<T>(relation: Relation, val default: () -> T) : AbstractCachedAccessor(relation), ReadOnlyAccessor<T>,
    OneFamilyMatching {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: QueriedEntity, property: KProperty<*>): T {
        val archetype = requireNotNull(this.archetype) { "Archetype is null" }
        return if (componentIndex == -1) default() else archetype.table[thisRef.entityIndex, componentIndex] as T
    }
}