package cn.jzl.graph.common.field

interface FieldType<T> {
    val fieldType: String

    fun accepts(value: Any) : Boolean
}

