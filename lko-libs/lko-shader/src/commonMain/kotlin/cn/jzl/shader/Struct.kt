package cn.jzl.shader

import kotlin.reflect.KProperty

abstract class Struct<S : Struct<S>>(
    private val statementScope: StatementScope,
    final override val name: String
) : VarType, Sequence<Struct.StructProperty<*>>, Operand.Variable<S>, VarTypeAccessor {

    private val properties = mutableMapOf<KProperty<*>, Struct.StructProperty<*>>()

    @Suppress("UNCHECKED_CAST")
    final override val type: S get() = this as S
    val structName: String = this::class.simpleName ?: "Undefined"

    @Suppress("UNCHECKED_CAST")
    operator fun <T : VarType> T.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
        return properties.getOrPut(property) {
            StructProperty(this@Struct, property.name, this)
        } as Property<T, Operand<T>>
    }

    final override fun iterator(): Iterator<StructProperty<*>> = properties.values.iterator()

    class StructProperty<T : VarType>(
        private val struct: Struct<*>,
        private val name: String,
        private val type: T
    ) : Property<T, Operand<T>>, Statement.Definition<T, Operand<T>> {

        override val variable: Operand<T> by lazy { Operand.TemporaryVariable(name, type) }
        val swizzle: Operand.Swizzle<T> by lazy { Operand.Swizzle(struct, name, type) }

        override fun getValue(thisRef: Any?, property: KProperty<*>): Operand<T> = swizzle

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Operand<T>) {
            struct.statementScope.statement(Statement.Assignment(swizzle, value))
        }
    }
}