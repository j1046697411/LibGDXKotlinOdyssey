package cn.jzl.shader

import cn.jzl.shader.operand.Literal
import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Operator
import cn.jzl.shader.operand.Swizzle
import cn.jzl.shader.operand.SystemFunc

interface ExpressionScope {

    val Int.lit: Literal.IntLiteral get() = Literal.IntLiteral(this)
    val Float.lit: Literal.FloatLiteral get() = Literal.FloatLiteral(this)
    val Boolean.lit: Literal.BooleanLiteral get() = Literal.BooleanLiteral(this)

    fun <T : VarType.FloatVarType> radians(radians: Operand<T>): Operand<T> = SystemFunc("radians", listOf(radians), radians.type)
    fun <T : VarType.FloatVarType> degrees(degrees: Operand<T>): Operand<T> = SystemFunc("degrees", listOf(degrees), degrees.type)

    fun <T : VarType.FloatVarType> sin(x: Operand<T>): Operand<T> = SystemFunc("sin", listOf(x), x.type)
    fun <T : VarType.FloatVarType> cos(x: Operand<T>): Operand<T> = SystemFunc("cos", listOf(x), x.type)
    fun <T : VarType.FloatVarType> tan(x: Operand<T>): Operand<T> = SystemFunc("tan", listOf(x), x.type)
    fun <T : VarType.FloatVarType> asin(x: Operand<T>): Operand<T> = SystemFunc("asin", listOf(x), x.type)
    fun <T : VarType.FloatVarType> acos(x: Operand<T>): Operand<T> = SystemFunc("acos", listOf(x), x.type)
    fun <T : VarType.FloatVarType> atan(x: Operand<T>): Operand<T> = SystemFunc("atan", listOf(x), x.type)
    fun <T : VarType.FloatVarType> atan(y: Operand<T>, x: Operand<out T>): Operand<T> = SystemFunc("atan", listOf(y, x), y.type)
    fun <T : VarType.FloatVarType> sinh(x: Operand<T>): Operand<T> = SystemFunc("sinh", listOf(x), x.type)
    fun <T : VarType.FloatVarType> cosh(x: Operand<T>): Operand<T> = SystemFunc("cosh", listOf(x), x.type)
    fun <T : VarType.FloatVarType> tanh(x: Operand<T>): Operand<T> = SystemFunc("tanh", listOf(x), x.type)
    fun <T : VarType.FloatVarType> asinh(x: Operand<T>): Operand<T> = SystemFunc("asinh", listOf(x), x.type)
    fun <T : VarType.FloatVarType> acosh(x: Operand<T>): Operand<T> = SystemFunc("acosh", listOf(x), x.type)
    fun <T : VarType.FloatVarType> atanh(x: Operand<T>): Operand<T> = SystemFunc("atanh", listOf(x), x.type)

    fun <T : VarType.FloatVarType> pow(x: Operand<T>, y: Operand<T>): Operand<T> = SystemFunc("pow", listOf(x, y), x.type)
    fun <T : VarType.FloatVarType> exp(x: Operand<T>): Operand<T> = SystemFunc("exp", listOf(x), x.type)
    fun <T : VarType.FloatVarType> exp2(x: Operand<T>): Operand<T> = SystemFunc("exp2", listOf(x), x.type)
    fun <T : VarType.FloatVarType> log(x: Operand<T>): Operand<T> = SystemFunc("log", listOf(x), x.type)
    fun <T : VarType.FloatVarType> log2(x: Operand<T>): Operand<T> = SystemFunc("log2", listOf(x), x.type)
    fun <T : VarType.FloatVarType> log10(x: Operand<T>): Operand<T> = SystemFunc("log10", listOf(x), x.type)
    fun <T : VarType.FloatVarType> sqrt(x: Operand<T>): Operand<T> = SystemFunc("sqrt", listOf(x), x.type)
    fun <V : VarType.FloatVarType> inverseSqrt(x: Operand<V>): Operand<V> = SystemFunc("inversesqrt", listOf(x), x.type)

    fun <T : VarType.FloatVector> length(x: Operand<T>): Operand<VarType.Float> = SystemFunc("length", listOf(x), VarType.Float)
    fun <T : VarType.FloatVector> distance(x: Operand<T>, y: Operand<T>): Operand<VarType.Float> = SystemFunc("distance", listOf(x, y), VarType.Float)
    fun <T : VarType.FloatVector> dot(x: Operand<T>, y: Operand<T>): Operand<VarType.Float> = SystemFunc("dot", listOf(x, y), VarType.Float)
    fun <T : VarType.FloatVector> cross(x: Operand<T>, y: Operand<T>): Operand<T> = SystemFunc("cross", listOf(x, y), x.type)
    fun <T : VarType.FloatVector> normalize(x: Operand<T>): Operand<T> = SystemFunc("normalize", listOf(x), x.type)
    fun <T : VarType.FloatVector> faceforward(n: Operand<T>, i: Operand<T>, nref: Operand<T>): Operand<T> = SystemFunc("faceforward", listOf(n, i, nref), n.type)
    fun <T : VarType.FloatVector> reflect(i: Operand<T>, n: Operand<T>): Operand<T> = SystemFunc("reflect", listOf(i, n), n.type)
    fun <T : VarType.FloatVector> refract(i: Operand<T>, n: Operand<T>, eta: Operand<VarType.Float>): Operand<T> = SystemFunc("refract", listOf(i, n, eta), i.type)

    fun <T : VarType.Mat> matrixCompMult(a: Operand<T>, b: Operand<T>): Operand<T> = SystemFunc("matrixCompMult", listOf(a, b), a.type)
    fun <T : VarType.Mat> determinant(x: Operand<T>): Operand<VarType.Float> = SystemFunc("determinant", listOf(x), VarType.Float)
    fun <T : VarType.Mat> inverse(x: Operand<T>): Operand<T> = SystemFunc("inverse", listOf(x), x.type)
    fun <T : VarType.Mat> transpose(x: Operand<T>): Operand<T> = SystemFunc("transpose", listOf(x), x.type)

    fun <T : VarType.FloatVector> step(edge: Operand<T>, x: Operand<T>): Operand<T> = SystemFunc("step", listOf(edge, x), x.type)
    fun <T : VarType.FloatVector> smoothstep(edge0: Operand<T>, edge1: Operand<T>, x: Operand<T>): Operand<T> = SystemFunc("smoothstep", listOf(edge0, edge1, x), x.type)

    fun <T : VarType.FloatVector> abs(x: Operand<T>): Operand<T> = SystemFunc("abs", listOf(x), x.type)
    fun <T : VarType.FloatVector> sign(x: Operand<T>): Operand<T> = SystemFunc("sign", listOf(x), x.type)
    fun <T : VarType.FloatVector> floor(x: Operand<T>): Operand<T> = SystemFunc("floor", listOf(x), x.type)
    fun <T : VarType.FloatVector> ceil(x: Operand<T>): Operand<T> = SystemFunc("ceil", listOf(x), x.type)
    fun <T : VarType.FloatVector> round(x: Operand<T>): Operand<T> = SystemFunc("round", listOf(x), x.type)
    fun <T : VarType.FloatVector> roundEven(x: Operand<T>): Operand<T> = SystemFunc("roundEven", listOf(x), x.type)
    fun <T : VarType.FloatVector> fract(x: Operand<T>): Operand<T> = SystemFunc("fract", listOf(x), x.type)
    fun <T : VarType.FloatVector> mod(x: Operand<T>, y: Operand<VarType.Float>): Operand<T> = SystemFunc("mod", listOf(x, y), x.type)
    fun <T : VarType.FloatVector> min(x: Operand<T>, y: Operand<T>): Operand<T> = SystemFunc("min", listOf(x, y), x.type)
    fun <T : VarType.FloatVector> max(x: Operand<T>, y: Operand<T>): Operand<T> = SystemFunc("max", listOf(x, y), x.type)
    fun <T : VarType.FloatVector> clamp(x: Operand<T>, minVal: Operand<T>, maxVal: Operand<T>): Operand<T> = SystemFunc("clamp", listOf(x, minVal, maxVal), x.type)
    fun <T : VarType.FloatVector> mix(x: Operand<T>, y: Operand<T>, a: Operand<T>): Operand<T> = SystemFunc("mix", listOf(x, y, a), x.type)

    fun vec2(vararg ops: Operand<VarType.Float>): Operand<VarType.FloatVec2> = SystemFunc("vec2", ops.toList(), VarType.FloatVec2)
    fun vec3(vararg ops: Operand<VarType.Float>): Operand<VarType.FloatVec3> = SystemFunc("vec3", ops.toList(), VarType.FloatVec3)
    fun vec4(vararg ops: Operand<VarType.Float>): Operand<VarType.FloatVec4> = SystemFunc("vec4", ops.toList(), VarType.FloatVec4)
    fun vec2(vararg ops: Float): Operand<VarType.FloatVec2> = SystemFunc("vec2", ops.map { it.lit }, VarType.FloatVec2)
    fun vec3(vararg ops: Float): Operand<VarType.FloatVec3> = SystemFunc("vec3", ops.map { it.lit }, VarType.FloatVec3)
    fun vec4(vararg ops: Float): Operand<VarType.FloatVec4> = SystemFunc("vec4", ops.map { it.lit }, VarType.FloatVec4)

    fun ivec2(vararg ops: Operand<VarType.Integer>): Operand<VarType.IntVec2> = SystemFunc("ivec2", ops.toList(), VarType.IntVec2)
    fun ivec3(vararg ops: Operand<VarType.Integer>): Operand<VarType.IntVec3> = SystemFunc("ivec3", ops.toList(), VarType.IntVec3)
    fun ivec4(vararg ops: Operand<VarType.Integer>): Operand<VarType.IntVec4> = SystemFunc("ivec4", ops.toList(), VarType.IntVec4)
    fun ivec2(vararg ops: Int): Operand<VarType.IntVec2> = SystemFunc("ivec2", ops.map { it.lit }, VarType.IntVec2)
    fun ivec3(vararg ops: Int): Operand<VarType.IntVec3> = SystemFunc("ivec3", ops.map { it.lit }, VarType.IntVec3)
    fun ivec4(vararg ops: Int): Operand<VarType.IntVec4> = SystemFunc("ivec4", ops.map { it.lit }, VarType.IntVec4)

    fun bvec2(vararg ops: Operand<VarType.Bool>): Operand<VarType.BoolVec2> = SystemFunc("bvec2", ops.toList(), VarType.BoolVec2)
    fun bvec3(vararg ops: Operand<VarType.Bool>): Operand<VarType.BoolVec3> = SystemFunc("bvec3", ops.toList(), VarType.BoolVec3)
    fun bvec4(vararg ops: Operand<VarType.Bool>): Operand<VarType.BoolVec4> = SystemFunc("bvec4", ops.toList(), VarType.BoolVec4)
    fun bvec2(vararg ops: Boolean): Operand<VarType.BoolVec2> = SystemFunc("bvec2", ops.map { it.lit }, VarType.BoolVec2)
    fun bvec3(vararg ops: Boolean): Operand<VarType.BoolVec3> = SystemFunc("bvec3", ops.map { it.lit }, VarType.BoolVec3)
    fun bvec4(vararg ops: Boolean): Operand<VarType.BoolVec4> = SystemFunc("bvec4", ops.map { it.lit }, VarType.BoolVec4)

    fun mat2(vararg ops: Operand<VarType.Float>): Operand<VarType.Mat2> = SystemFunc("mat2", ops.toList(), VarType.Mat2)
    fun mat3(vararg ops: Operand<VarType.Float>): Operand<VarType.Mat3> = SystemFunc("mat3", ops.toList(), VarType.Mat3)
    fun mat4(vararg ops: Operand<VarType.Float>): Operand<VarType.Mat4> = SystemFunc("mat4", ops.toList(), VarType.Mat4)
    fun mat2(vararg ops: Float): Operand<VarType.Mat2> = SystemFunc("mat2", ops.map { it.lit }, VarType.Mat2)
    fun mat3(vararg ops: Float): Operand<VarType.Mat3> = SystemFunc("mat3", ops.map { it.lit }, VarType.Mat3)
    fun mat4(vararg ops: Float): Operand<VarType.Mat4> = SystemFunc("mat4", ops.map { it.lit }, VarType.Mat4)

    operator fun <T : VarType> Operand<T>.unaryPlus(): Operand<T> = Operator.UnaryOperator("+", this)
    operator fun <T : VarType> Operand<T>.unaryMinus(): Operand<T> = Operator.UnaryOperator("-", this)

    operator fun <T : VarType> Operand<T>.plus(other: Operand<T>): Operand<T> = Operator.BinaryOperator(this, "+", other)
    operator fun <T : VarType> Operand<T>.minus(other: Operand<T>): Operand<T> = Operator.BinaryOperator(this, "-", other)
    operator fun <T : VarType> Operand<T>.times(other: Operand<T>): Operand<T> = Operator.BinaryOperator(this, "*", other)
    operator fun <T : VarType> Operand<T>.div(other: Operand<T>): Operand<T> = Operator.BinaryOperator(this, "/", other)
    operator fun <T : VarType> Operand<T>.rem(other: Operand<T>): Operand<T> = Operator.BinaryOperator(this, "%", other)

    fun <T : VarType> ternary(condition: Operand<*>, left: Operand<T>, right: Operand<T>): Operand<T> = Operator.TernaryOperator(condition, left, right)

    fun <T : VarType> Operand<VarType.Vector2<T>>.swizzle1(swizzle: String): Operand<T> = Swizzle(this, swizzle, type.component1())
    fun <T : VarType, T2 : VarType.Vector2<T>> Operand<VarType.Vector3<T, T2>>.swizzle2(swizzle: String): Operand<T2> = Swizzle(this, swizzle, type.component2())
    fun <T : VarType, T2 : VarType.Vector2<T>, T3 : VarType.Vector3<T, T2>> Operand<VarType.Vector4<T, T2, T3>>.swizzle3(swizzle: String): Operand<T3> = Swizzle(this, swizzle, type.component3())

    val <T : VarType, V : VarType.Vector2<T>> Operand<out V>.x: Operand<T> get() = Swizzle(this, "x", type.component1())
    val <T : VarType, V : VarType.Vector2<T>> Operand<out V>.y: Operand<T> get() = Swizzle(this, "y", type.component1())
    val <T : VarType, V : VarType.Vector3<T, *>> Operand<out V>.z: Operand<T> get() = Swizzle(this, "z", type.component1())
    val <T : VarType, V : VarType.Vector4<T, *, *>> Operand<out V>.w: Operand<T> get() = Swizzle(this, "w", type.component1())

    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector3<T1, T2>> Operand<out V>.xy: Operand<T2> get() = Swizzle(this, "xy", type.component2())
    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector3<T1, T2>> Operand<out V>.xz: Operand<T2> get() = Swizzle(this, "yz", type.component2())
    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector4<T1, T2, *>> Operand<out V>.xw: Operand<T2> get() = Swizzle(this, "xw", type.component2())
    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector3<T1, T2>> Operand<out V>.yz: Operand<T2> get() = Swizzle(this, "zy", type.component2())
    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector4<T1, T2, *>> Operand<out V>.yw: Operand<T2> get() = Swizzle(this, "yw", type.component2())
    val <T1 : VarType, T2 : VarType.Vector2<T1>, V : VarType.Vector4<T1, T2, *>> Operand<out V>.zw: Operand<T2> get() = Swizzle(this, "zw", type.component2())


    val <T : VarType, V : VarType.Vector2<T>> Operand<out V>.r: Operand<T> get() = Swizzle(this, "r", type.component1())
    val <T : VarType, V : VarType.Vector2<T>> Operand<out V>.g: Operand<T> get() = Swizzle(this, "g", type.component1())
    val <T : VarType, V : VarType.Vector3<T, *>> Operand<out V>.b: Operand<T> get() = Swizzle(this, "b", type.component1())
    val <T : VarType, V : VarType.Vector4<T, *, *>> Operand<out V>.a: Operand<T> get() = Swizzle(this, "a", type.component1())
}