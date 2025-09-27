package cn.jzl.graph.common.field

import cn.jzl.graph.common.config.PropertyKey

data class  FieldPropertyKey<T>(
    val fieldType: FieldType<T>,
    val propertyKey: String
) : PropertyKey<T>