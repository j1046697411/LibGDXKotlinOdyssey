package cn.jzl.graph.common.field

interface FieldTypeRegistry{
    fun registerFieldTypes(vararg fieldTypes: FieldType<out Any>)
}