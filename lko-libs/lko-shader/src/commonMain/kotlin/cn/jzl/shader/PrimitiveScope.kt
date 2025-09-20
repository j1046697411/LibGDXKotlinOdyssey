package cn.jzl.shader

import kotlin.reflect.KProperty

interface PrimitiveScope : VarTypeAccessor {

    val Float.lit: Operand.Literal<Float, VarType.Float> get() = Operand.Literal.FloatLiteral(this)
    val Int.lit: Operand.Literal<Int, VarType.Integer> get() = Operand.Literal.IntLiteral(this)
    val Boolean.lit: Operand.Literal<Boolean, VarType.Boolean> get() = Operand.Literal.BooleanLiteral(this)


    operator fun <T : VarType> Operand<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>>

    operator fun <T : VarType> T.invoke(operand: Operand<T>): Operand<T> = operand

    operator fun VarType.Float.invoke(value: Float): Operand<VarType.Float> = value.lit
    operator fun VarType.Integer.invoke(value: Int): Operand<VarType.Integer> = value.lit
    operator fun VarType.Boolean.invoke(value: Boolean): Operand<VarType.Boolean> = value.lit

    operator fun VarType.Vec2.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Vec2> {
        check(args.size <= 2) { "vec2 only accept 2 arguments" }
        return Operand.SystemFunction("vec2", vec2, args.toList())
    }

    operator fun VarType.Vec2.invoke(vararg args: Float): Operand<VarType.Vec2> {
        check(args.size <= 2) { "vec2 only accept 2 arguments" }
        return Operand.SystemFunction("vec2", vec2, args.map { it.lit })
    }

    operator fun VarType.Vec3.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Vec3> {
        check(args.size <= 3) { "vec3 only accept 3 arguments" }
        return Operand.SystemFunction("vec3", vec3, args.toList())
    }

    operator fun VarType.Vec3.invoke(vararg args: Float): Operand<VarType.Vec3> {
        check(args.size <= 3) { "vec3 only accept 3 arguments" }
        return Operand.SystemFunction("vec3", vec3, args.map { it.lit })
    }

    operator fun VarType.Vec4.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Vec4> {
        check(args.size <= 4) { "vec4 only accept 4 arguments" }
        return Operand.SystemFunction("vec4", vec4, args.toList())
    }

    operator fun VarType.Vec4.invoke(vararg args: Float): Operand<VarType.Vec4> {
        check(args.size <= 4) { "vec4 only accept 4 arguments" }
        return Operand.SystemFunction("vec4", vec4, args.map { it.lit })
    }

    operator fun VarType.IVec2.invoke(vararg args: Operand<out VarType.IntegerType>): Operand<VarType.IVec2> {
        check(args.size <= 2) { "ivec2 only accept 2 arguments" }
        return Operand.SystemFunction("ivec2", ivec2, args.toList())
    }

    operator fun VarType.IVec2.invoke(vararg args: Int): Operand<VarType.IVec2> {
        check(args.size <= 2) { "ivec2 only accept 2 arguments" }
        return Operand.SystemFunction("ivec2", ivec2, args.map { it.lit })
    }

    operator fun VarType.IVec3.invoke(vararg args: Operand<out VarType.IntegerType>): Operand<VarType.IVec3> {
        check(args.size <= 3) { "ivec3 only accept 3 arguments" }
        return Operand.SystemFunction("ivec3", ivec3, args.toList())
    }

    operator fun VarType.IVec3.invoke(vararg args: Int): Operand<VarType.IVec3> {
        check(args.size <= 3) { "ivec3 only accept 3 arguments" }
        return Operand.SystemFunction("ivec3", ivec3, args.map { it.lit })
    }

    operator fun VarType.IVec4.invoke(vararg args: Operand<out VarType.IntegerType>): Operand<VarType.IVec4> {
        check(args.size <= 4) { "ivec4 only accept 4 arguments" }
        return Operand.SystemFunction("ivec4", ivec4, args.toList())
    }

    operator fun VarType.IVec4.invoke(vararg args: Int): Operand<VarType.IVec4> {
        check(args.size <= 4) { "ivec4 only accept 4 arguments" }
        return Operand.SystemFunction("ivec4", ivec4, args.map { it.lit })
    }

    operator fun VarType.BVec2.invoke(vararg args: Operand<out VarType.BooleanType>): Operand<VarType.BVec2> {
        check(args.size <= 2) { "bvec2 only accept 2 arguments" }
        return Operand.SystemFunction("bvec2", bvec2, args.toList())
    }

    operator fun VarType.BVec2.invoke(vararg args: Boolean): Operand<VarType.BVec2> {
        check(args.size <= 2) { "bvec2 only accept 2 arguments" }
        return Operand.SystemFunction("bvec2", bvec2, args.map { it.lit })
    }

    operator fun VarType.BVec3.invoke(vararg args: Operand<out VarType.BooleanType>): Operand<VarType.BVec3> {
        check(args.size <= 3) { "bvec3 only accept 3 arguments" }
        return Operand.SystemFunction("bvec3", bvec3, args.toList())
    }

    operator fun VarType.BVec3.invoke(vararg args: Boolean): Operand<VarType.BVec3> {
        check(args.size <= 3) { "bvec3 only accept 3 arguments" }
        return Operand.SystemFunction("bvec3", bvec3, args.map { it.lit })
    }

    operator fun VarType.BVec4.invoke(vararg args: Operand<out VarType.BooleanType>): Operand<VarType.BVec4> {
        check(args.size <= 4) { "bvec4 only accept 4 arguments" }
        return Operand.SystemFunction("bvec4", bvec4, args.toList())
    }

    operator fun VarType.BVec4.invoke(vararg args: Boolean): Operand<VarType.BVec4> {
        check(args.size <= 4) { "bvec4 only accept 4 arguments" }
        return Operand.SystemFunction("bvec4", bvec4, args.map { it.lit })
    }

    operator fun VarType.Mat2.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Mat2> {
        check(args.size <= 4) { "mat2 only accept 4 arguments" }
        return Operand.SystemFunction("mat2", mat2, args.toList())
    }

    operator fun VarType.Mat2.invoke(vararg args: Float): Operand<VarType.Mat2> {
        check(args.size <= 4) { "mat2 only accept 4 arguments" }
        return Operand.SystemFunction("mat2", mat2, args.map { it.lit })
    }

    operator fun VarType.Mat3.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Mat3> {
        check(args.size <= 9) { "mat3 only accept 9 arguments" }
        return Operand.SystemFunction("mat3", mat3, args.toList())
    }

    operator fun VarType.Mat3.invoke(vararg args: Float): Operand<VarType.Mat3> {
        check(args.size <= 9) { "mat3 only accept 9 arguments" }
        return Operand.SystemFunction("mat3", mat3, args.map { it.lit })
    }

    operator fun VarType.Mat4.invoke(vararg args: Operand<out VarType.FloatType>): Operand<VarType.Mat4> {
        check(args.size <= 16) { "mat4 only accept 16 arguments" }
        return Operand.SystemFunction("mat4", mat4, args.toList())
    }

    operator fun VarType.Mat4.invoke(vararg args: Float): Operand<VarType.Mat4> {
        check(args.size <= 16) { "mat4 only accept 16 arguments" }
        return Operand.SystemFunction("mat4", mat4, args.map { it.lit })
    }

    fun Operand<out VarType.BooleanVector>.all(): Operand<VarType.Boolean> {
        return Operand.SystemFunction("all", bool, listOf(this))
    }

    fun Operand<out VarType.BooleanVector>.any(): Operand<VarType.Boolean> {
        return Operand.SystemFunction("any", bool, listOf(this))
    }
}