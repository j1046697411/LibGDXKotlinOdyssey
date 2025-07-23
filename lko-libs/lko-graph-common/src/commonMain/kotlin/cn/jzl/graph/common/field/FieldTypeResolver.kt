package cn.jzl.graph.common.field

fun interface FieldTypeResolver {
    fun resolve(fieldType: String): FieldType<out Any>
}