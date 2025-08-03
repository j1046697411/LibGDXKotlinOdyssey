package cn.jzl.graph.common.field

fun interface  FieldTypeValidator {
    fun validate(value: Any): FieldType<out Any>
}