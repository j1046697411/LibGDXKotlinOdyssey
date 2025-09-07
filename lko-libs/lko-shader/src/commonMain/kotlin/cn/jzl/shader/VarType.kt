package cn.jzl.shader

import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Swizzle
import cn.jzl.shader.operand.Variable
import cn.jzl.shader.struct.StructProperty
import kotlin.reflect.KProperty

sealed interface VarType {

    val kind: VarKind
    val elementCount: Int
    val bytesSize: Int get() = kind.bytesSize * elementCount

    abstract class Struct<S : Struct<S>>(val shader: Program.ShaderScope, override val name: String) : VarType, Variable<S>, Sequence<StructProperty<*, S>> {

        private val properties = mutableMapOf<String, StructProperty<*, S>>()

        val structName: String = this::class.simpleName ?: "Undefined"

        @Suppress("UNCHECKED_CAST")
        override val type: S get() = this as S
        override val kind: VarKind get() = VarKind.Struct
        override val elementCount: Int get() = 1
        override val bytesSize: Int get() = fold(0) { acc, property -> acc + property.type.bytesSize }

        override fun iterator(): Iterator<StructProperty<*, S>> {
            return properties.values.iterator()
        }

        @Suppress("UNCHECKED_CAST")
        internal fun <T : VarType> Program.getStructProperty(property: KProperty<*>, type: T): StructProperty<T, S> {
            return properties.getOrPut(property.name) {
                SimpleStructProperty(this, this@Struct.type, property.name, type)
            } as StructProperty<T, S>
        }

        override fun toString(): String = "Struct(name=$name, type=$structName)"

        private data class SimpleStructProperty<T : VarType, S : Struct<S>>(
            private val program: Program,
            override val struct: S,
            override val name: String,
            override val type: T
        ) : StructProperty<T, S> {
            override val swizzle: Operand<T> by lazy { Swizzle(struct, name, type) }

            override fun getValue(thisRef: Any?, property: KProperty<*>): Operand<T> = swizzle

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: Operand<T>): Unit = with(struct.shader) {
                swizzle assignment value
            }
        }
    }

    interface FloatVarType : VarType

    interface FloatVector : FloatVarType

    interface Vector2<V1 : VarType> : VarType {
        operator fun component1(): V1
    }

    interface Vector3<V1 : VarType, V2 : Vector2<V1>> : VarType, Vector2<V1> {
        operator fun component2(): V2
    }

    interface Vector4<V1 : VarType, V2 : Vector2<V1>, V3 : Vector3<V1, V2>> : Vector3<V1, V2> {
        operator fun component3(): V3
    }

    sealed class AbstractVarType(override val kind: VarKind, override val elementCount: Int = 1) : VarType

    data object Void : AbstractVarType(VarKind.Void)
    data object Integer : AbstractVarType(VarKind.Integer)
    data object IntVec2 : AbstractVarType(VarKind.Integer, 2), Vector2<Integer> {
        override fun component1(): Integer = Integer
    }

    data object IntVec3 : AbstractVarType(VarKind.Integer, 3), Vector3<Integer, IntVec2> {
        override fun component2(): IntVec2 = IntVec2
        override fun component1(): Integer = Integer
    }

    data object IntVec4 : AbstractVarType(VarKind.Integer, 4), Vector4<Integer, IntVec2, IntVec3> {
        override fun component3(): IntVec3 = IntVec3
        override fun component2(): IntVec2 = IntVec2
        override fun component1(): Integer = Integer
    }

    data object Float : AbstractVarType(VarKind.Float), FloatVarType

    data object FloatVec2 : AbstractVarType(VarKind.Float, 2), Vector2<Float>, FloatVarType, FloatVector {
        override fun component1(): Float = Float
    }

    data object FloatVec3 : AbstractVarType(VarKind.Float, 3), Vector3<Float, FloatVec2>, FloatVarType, FloatVector {
        override fun component2(): FloatVec2 = FloatVec2
        override fun component1(): Float = Float
    }

    data object FloatVec4 : AbstractVarType(VarKind.Float, 4), Vector4<Float, FloatVec2, FloatVec3>, FloatVarType, FloatVector {
        override fun component3(): FloatVec3 = FloatVec3
        override fun component2(): FloatVec2 = FloatVec2
        override fun component1(): Float = Float
    }

    interface BoolVarType : VarType
    interface BoolVector : BoolVarType
    data object Bool : AbstractVarType(VarKind.Bool), BoolVarType
    data object BoolVec2 : AbstractVarType(VarKind.Bool, 2), Vector2<Bool>, BoolVector {
        override fun component1(): Bool = Bool
    }

    data object BoolVec3 : AbstractVarType(VarKind.Bool, 3), Vector3<Bool, BoolVec2>, BoolVector {
        override fun component2(): BoolVec2 = BoolVec2
        override fun component1(): Bool = Bool
    }

    data object BoolVec4 : AbstractVarType(VarKind.Bool, 4), Vector4<Bool, BoolVec2, BoolVec3>, BoolVector {
        override fun component3(): BoolVec3 = BoolVec3
        override fun component2(): BoolVec2 = BoolVec2
        override fun component1(): Bool = Bool
    }

    data object Sampler1D : AbstractVarType(VarKind.Integer)
    data object Sampler2D : AbstractVarType(VarKind.Integer)
    data object Sampler3D : AbstractVarType(VarKind.Integer)
    data object SamplerCube : AbstractVarType(VarKind.Integer)

    interface Mat : VarType
    data object Mat2 : AbstractVarType(VarKind.Float, 4), Mat
    data object Mat3 : AbstractVarType(VarKind.Float, 9), Mat
    data object Mat4 : AbstractVarType(VarKind.Float, 16), Mat
}