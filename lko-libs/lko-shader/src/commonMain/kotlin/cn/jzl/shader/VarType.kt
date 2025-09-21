package cn.jzl.shader

sealed interface VarType {

    interface Comparable<R : BooleanType> : VarType {
        val resultType: R
    }

    interface Composite : VarType {
        val elementCount: Int
    }

    interface FloatComposite : Composite
    interface IntegerComposite : Composite
    interface BooleanComposite : Composite

    interface Computable : VarType
    interface Vector<V, Vec2, Vec3, Vec4> : VarType, Computable, Composite {
        operator fun component1(): V
        operator fun component2(): Vec2
        operator fun component3(): Vec3
        operator fun component4(): Vec4
    }

    interface Vector2<V, Vec2, Vec3, Vec4> : Vector<V, Vec2, Vec3, Vec4> {
        override val elementCount: Int get() = 2
    }

    interface Vector3<V, Vec2, Vec3, Vec4> : Vector<V, Vec2, Vec3, Vec4> {
        override val elementCount: Int get() = 3
    }

    interface Vector4<V, Vec2, Vec3, Vec4> : Vector<V, Vec2, Vec3, Vec4> {
        override val elementCount: Int get() = 4
    }

    interface NumberType : VarType

    interface IntegerType : VarType, NumberType, Computable
    data object Integer : Comparable<Boolean>, Computable, IntegerType {
        override val resultType: Boolean = Boolean
    }

    abstract class IntegerVector : Vector<Integer, IVec2, IVec3, IVec4>, IntegerType, IntegerComposite {
        override fun component1(): Integer = Integer
        override fun component2(): IVec2 = IVec2
        override fun component3(): IVec3 = IVec3
        override fun component4(): IVec4 = IVec4
    }

    data object IVec2 : IntegerVector(), Vector2<Integer, IVec2, IVec3, IVec4>, Comparable<BVec2> {
        override val resultType: BVec2 = BVec2
    }

    data object IVec3 : IntegerVector(), Vector3<Integer, IVec2, IVec3, IVec4>, Comparable<BVec3> {
        override val resultType: BVec3 = BVec3
    }

    data object IVec4 : IntegerVector(), Vector4<Integer, IVec2, IVec3, IVec4>, Comparable<BVec4> {
        override val resultType: BVec4 = BVec4
    }

    interface FloatType : VarType, NumberType, Computable
    data object Float : Comparable<Boolean>, Computable, FloatType {
        override val resultType: Boolean = Boolean
    }

    abstract class FloatVector : Vector<Float, Vec2, Vec3, Vec4>, FloatType, FloatComposite {
        override fun component1(): Float = Float
        override fun component2(): Vec2 = Vec2
        override fun component3(): Vec3 = Vec3
        override fun component4(): Vec4 = Vec4
    }

    data object Vec2 : FloatVector(), Vector2<Float, Vec2, Vec3, Vec4>, Comparable<BVec2> {
        override val resultType: BVec2 = BVec2
    }

    data object Vec3 : FloatVector(), Vector3<Float, Vec2, Vec3, Vec4>, Comparable<BVec3> {
        override val resultType: BVec3 = BVec3
    }

    data object Vec4 : FloatVector(), Vector4<Float, Vec2, Vec3, Vec4>, Comparable<BVec4> {
        override val resultType: BVec4 = BVec4
    }

    interface BooleanType : VarType
    data object Boolean : Comparable<Boolean>, BooleanType {
        override val resultType: Boolean = Boolean
    }

    abstract class BooleanVector : Vector<Boolean, BVec2, BVec3, BVec4>, BooleanType, BooleanComposite {
        override fun component1(): Boolean = Boolean
        override fun component2(): BVec2 = BVec2
        override fun component3(): BVec3 = BVec3
        override fun component4(): BVec4 = BVec4
    }

    data object BVec2 : BooleanVector(), Vector2<Boolean, BVec2, BVec3, BVec4>, Comparable<BVec2> {
        override val resultType: BVec2 = BVec2
    }

    data object BVec3 : BooleanVector(), Vector3<Boolean, BVec2, BVec3, BVec4>, Comparable<BVec3> {
        override val resultType: BVec3 = BVec3
    }

    data object BVec4 : BooleanVector(), Vector4<Boolean, BVec2, BVec3, BVec4>, Comparable<BVec4> {
        override val resultType: BVec4 = BVec4
    }

    interface Matrix : VarType, FloatComposite

    data object Mat2 : Matrix {
        override val elementCount: Int get() = 4
    }

    data object Mat3 : Matrix {
        override val elementCount: Int get() = 9
    }

    data object Mat4 : Matrix {
        override val elementCount: Int get() = 16
    }

    interface Sampler : VarType
    data object Sampler1D : Sampler
    data object Sampler2D : Sampler
    data object Sampler3D : Sampler
    data object SamplerCube : Sampler


    data object Void : VarType
}