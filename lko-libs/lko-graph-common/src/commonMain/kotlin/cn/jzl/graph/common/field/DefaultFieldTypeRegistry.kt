package cn.jzl.graph.common.field

internal class DefaultFieldTypeRegistry: FieldTypeRegistry, FieldTypeResolver{
    private val fieldTypes = hashMapOf<String, FieldType<out Any>>()

    override fun registerFieldTypes(vararg fieldTypes: FieldType<out Any>) {
        if (fieldTypes.isEmpty()) return
        fieldTypes.forEach { fieldType -> this.fieldTypes[fieldType.fieldType] = fieldType }
    }

    override fun resolve(fieldType: String): FieldType<out Any> {
        return fieldTypes[fieldType] ?: throw IllegalArgumentException("Field type $fieldType not found")
    }
}