package cn.jzl.ecs.query

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ReadOnlyAccessor<out T> : Accessor, ReadOnlyProperty<QueriedEntity, T> {
    override fun getValue(thisRef: QueriedEntity, property: KProperty<*>): T
}