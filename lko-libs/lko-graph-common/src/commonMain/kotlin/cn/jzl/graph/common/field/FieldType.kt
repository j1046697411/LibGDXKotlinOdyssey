package cn.jzl.graph.common.field

import cn.jzl.graph.common.config.PropertyKey

interface FieldType<T> {
    val fieldType: String

    fun accepts(value: Any) : Boolean

    fun createPropertyKey(propertyName: String): PropertyKey<T> = FieldPropertyKey(this, propertyName)
}

