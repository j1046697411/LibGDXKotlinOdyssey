package cn.jzl.graph.common.field

fun interface PipelineFieldTypeResolver{
    fun resolve(fieldType: String) : FieldType<out Any>
}