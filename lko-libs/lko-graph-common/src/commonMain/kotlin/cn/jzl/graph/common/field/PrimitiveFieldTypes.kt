package cn.jzl.graph.common.field

import kotlin.reflect.KClass

object PrimitiveFieldTypes {
    val FIELD_TYPE_FLOAT = arrayOf(FloatFieldType)

    private val fieldTypes = mutableListOf<FieldType<*>>()

    fun addDefaultFieldType(fieldType: FieldType<*>) {
        if (fieldType in fieldTypes) return
        this.fieldTypes.add(fieldType)
    }

    fun values(): List<FieldType<*>> = fieldTypes

    abstract class AbstractFieldType<T : Any>(kType: KClass<T>) : FieldType<T> {
        override val fieldType: String = kType.simpleName ?: "unknown"
    }

    sealed interface PrimitiveFieldType<T> : FieldType<T>

    sealed interface NumberFieldType<T> : PrimitiveFieldType<T>

    data object FloatFieldType : AbstractFieldType<Float>(Float::class), NumberFieldType<Float>

    data object BooleanFieldType : AbstractFieldType<Boolean>(Boolean::class), PrimitiveFieldType<Boolean>
}