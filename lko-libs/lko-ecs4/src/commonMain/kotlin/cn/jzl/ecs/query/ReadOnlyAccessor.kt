package cn.jzl.ecs.query

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ReadOnlyAccessor<out T> : Accessor, ReadOnlyProperty<QueryEntityContext, T> {
    override fun getValue(thisRef: QueryEntityContext, property: KProperty<*>): T
}