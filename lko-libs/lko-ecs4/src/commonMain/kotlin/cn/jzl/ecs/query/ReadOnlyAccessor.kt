package cn.jzl.ecs.query

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ReadOnlyAccessor<out T> : Accessor, ReadOnlyProperty<EntityQueryContext, T> {
    override fun getValue(thisRef: EntityQueryContext, property: KProperty<*>): T
}