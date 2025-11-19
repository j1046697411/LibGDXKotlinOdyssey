package cn.jzl.ecs.query

import cn.jzl.ecs.Relation
import kotlin.reflect.KProperty

class RelationAccessor<T : Any>(relation: Relation) : AbstractCachedAccessor(relation), ReadWriteAccessor<T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: QueriedEntity, property: KProperty<*>): T {
        check(componentIndex != -1) { "Component index is not set" }
        val archetype = requireNotNull(this.archetype) { "Archetype is null" }
        return archetype.table[thisRef.entityIndex, componentIndex] as T
    }

    override fun setValue(thisRef: QueriedEntity, property: KProperty<*>, value: T) {
        check(componentIndex != -1) { "Component index is not set" }
        val archetype = requireNotNull(this.archetype) { "Archetype is null" }
        archetype.table[thisRef.entityIndex, componentIndex] = value
    }
}