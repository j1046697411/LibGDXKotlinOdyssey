package cn.jzl.shader

interface ExpressionScope : VarTypeAccessor {

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.eq(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "==", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.ne(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "!=", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.lt(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "<", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.gt(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, ">", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.le(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, "<=", other, type.resultType)
    }

    infix fun <R : VarType.BooleanType, T : VarType.Comparable<R>> Operand<T>.ge(other: Operand<T>): Operand<R> {
        return Operand.Operator.BinaryOperator(this, ">=", other, type.resultType)
    }

    fun Operand<out VarType.BooleanVector>.all(): Operand<VarType.Boolean> {
        return Operand.SystemFunction("all", bool, listOf(this))
    }

    fun Operand<out VarType.BooleanVector>.any(): Operand<VarType.Boolean> {
        return Operand.SystemFunction("any", bool, listOf(this))
    }

    fun Operand<out VarType.BooleanVector>.not(): Operand<VarType.BooleanVector> {
        return Operand.SystemFunction("not", type, listOf(this))
    }

    infix operator fun <T : VarType.Computable> Operand<T>.plus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "+", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.minus(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "-", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.times(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "*", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.div(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "/", other, type)
    }

    infix operator fun <T : VarType.Computable> Operand<T>.rem(other: Operand<T>): Operand<T> {
        return Operand.Operator.BinaryOperator(this, "%", other, type)
    }

    fun <T : VarType.FloatType> sin(angle: Operand<T>): Operand<T> {
        return Operand.SystemFunction("sin", angle.type, listOf(angle))
    }

    fun <T : VarType.FloatType> cos(angle: Operand<T>): Operand<T> {
        return Operand.SystemFunction("cos", angle.type, listOf(angle))
    }

    fun <T : VarType.FloatType> tan(angle: Operand<T>): Operand<T> {
        return Operand.SystemFunction("tan", angle.type, listOf(angle))
    }

    fun <T : VarType.FloatType> asin(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("asin", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> acos(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("acos", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> atan(y: Operand<T>, x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("atan", y.type, listOf(y, x))
    }

    fun <T : VarType.FloatType> atan(yOverX: Operand<T>): Operand<T> {
        return Operand.SystemFunction("atan", yOverX.type, listOf(yOverX))
    }

    fun <T : VarType.FloatType> sinh(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("sinh", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> cosh(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("cosh", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> tanh(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("tanh", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> radians(degrees: Operand<T>): Operand<T> {
        return Operand.SystemFunction("radians", degrees.type, listOf(degrees))
    }

    fun <T : VarType.FloatType> degrees(radians: Operand<T>): Operand<T> {
        return Operand.SystemFunction("degrees", radians.type, listOf(radians))
    }

    fun <T : VarType.FloatType> pow(x: Operand<T>, y: Operand<T>): Operand<T> {
        return Operand.SystemFunction("pow", x.type, listOf(x, y))
    }

    fun <T : VarType.FloatType> exp(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("exp", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> log(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("log", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> exp2(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("exp2", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> log2(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("log2", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> sqrt(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("sqrt", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> inverseSqrt(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("inversesqrt", x.type, listOf(x))
    }

    fun <T : VarType.NumberType> abs(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("abs", x.type, listOf(x))
    }

    fun <T : VarType.NumberType> sign(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("sign", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> floor(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("floor", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> ceil(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("ceil", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> fract(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("fract", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> mod(x: Operand<T>, y: Operand<T>): Operand<T> {
        return Operand.SystemFunction("mod", x.type, listOf(x, y))
    }

    fun <T : VarType.NumberType> min(x: Operand<T>, y: Operand<T>): Operand<T> {
        return Operand.SystemFunction("min", x.type, listOf(x, y))
    }

    fun <T : VarType.NumberType> max(x: Operand<T>, y: Operand<T>): Operand<T> {
        return Operand.SystemFunction("max", x.type, listOf(x, y))
    }

    fun <T : VarType.NumberType> clamp(x: Operand<T>, minVal: Operand<T>, maxVal: Operand<T>): Operand<T> {
        return Operand.SystemFunction("clamp", x.type, listOf(x, minVal, maxVal))
    }

    fun <T : VarType.FloatType> mix(x: Operand<T>, y: Operand<T>, a: Operand<T>): Operand<T> {
        return Operand.SystemFunction("mix", x.type, listOf(x, y, a))
    }

    fun <T : VarType.FloatType> step(edge: Operand<T>, x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("step", edge.type, listOf(edge, x))
    }

    fun <T : VarType.FloatType> smoothstep(edge0: Operand<T>, edge1: Operand<T>, x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("smoothstep", edge0.type, listOf(edge0, edge1, x))
    }

    fun <T : VarType.FloatType> length(x: Operand<T>): Operand<VarType.Float> {
        return Operand.SystemFunction("length", float, listOf(x))
    }

    fun <T : VarType.FloatType> distance(p0: Operand<T>, p1: Operand<T>): Operand<VarType.Float> {
        return Operand.SystemFunction("distance", float, listOf(p0, p1))
    }

    fun <T : VarType.FloatType> dot(x: Operand<T>, y: Operand<T>): Operand<VarType.Float> {
        return Operand.SystemFunction("dot", float, listOf(x, y))
    }

    fun cross(x: Operand<VarType.Vec3>, y: Operand<VarType.Vec3>): Operand<VarType.Vec3> {
        return Operand.SystemFunction("cross", vec3, listOf(x, y))
    }

    fun <T : VarType.FloatType> normalize(x: Operand<T>): Operand<T> {
        return Operand.SystemFunction("normalize", x.type, listOf(x))
    }

    fun <T : VarType.FloatType> reflect(I: Operand<T>, N: Operand<T>): Operand<T> {
        return Operand.SystemFunction("reflect", I.type, listOf(I, N))
    }

    fun <T : VarType.FloatType> refract(I: Operand<T>, N: Operand<T>, eta: Operand<VarType.Float>): Operand<T> {
        return Operand.SystemFunction("refract", I.type, listOf(I, N, eta))
    }

    fun <T : VarType.Matrix> matrixCompMult(x: Operand<T>, y: Operand<T>): Operand<T> {
        return Operand.SystemFunction("matrixCompMult", x.type, listOf(x, y))
    }

    fun <T : VarType.Matrix> transpose(m: Operand<T>): Operand<T> {
        return Operand.SystemFunction("transpose", m.type, listOf(m))
    }

    fun <T : VarType.Matrix> determinant(m: Operand<T>): Operand<VarType.Float> {
        return Operand.SystemFunction("determinant", float, listOf(m))
    }

    fun <T : VarType.Matrix> inverse(m: Operand<T>): Operand<T> {
        return Operand.SystemFunction("inverse", m.type, listOf(m))
    }

    fun texture(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("texture", vec4, listOf(sampler, coordinate))
    }

    fun texture(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, bias: Operand<VarType.Float>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("texture", vec4, listOf(sampler, coordinate, bias))
    }

    fun <C : VarType.FloatVector> textureProj(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<C>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("textureProj", vec4, listOf(sampler, coordinate))
    }

    fun textureLod(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, lod: Operand<VarType.Float>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("textureLod", vec4, listOf(sampler, coordinate, lod))
    }

    fun textureSize(sampler: Operand<VarType.Sampler2D>, lod: Operand<VarType.Integer>): Operand<VarType.IVec2> {
        return Operand.SystemFunction("textureSize", ivec2, listOf(sampler, lod))
    }

    fun textureGrad(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, dPdx: Operand<VarType.Vec2>, dPdy: Operand<VarType.Vec2>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("textureGrad", vec4, listOf(sampler, coordinate, dPdx, dPdy))
    }

    fun textureOffset(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, offset: Operand<VarType.IVec2>): Operand<VarType.Vec4> {
        return Operand.SystemFunction("textureOffset", vec4, listOf(sampler, coordinate, offset))
    }

    fun <T : VarType.IntegerType> bitfieldExtract(value: Operand<T>, offset: Operand<T>, bits: Operand<T>): Operand<T> {
        return Operand.SystemFunction("bitfieldExtract", value.type, listOf(value, offset, bits))
    }

    fun <T : VarType.IntegerType> bitfieldInsert(base: Operand<T>, insert: Operand<T>, offset: Operand<T>, bits: Operand<T>): Operand<T> {
        return Operand.SystemFunction("bitfieldInsert", base.type, listOf(base, insert, offset, bits))
    }

    fun <T : VarType.IntegerType> bitfieldReverse(value: Operand<T>): Operand<T> {
        return Operand.SystemFunction("bitfieldReverse", value.type, listOf(value))
    }

    fun <T : VarType.IntegerType> bitCount(value: Operand<T>): Operand<T> {
        return Operand.SystemFunction("bitCount", value.type, listOf(value))
    }

    fun <T : VarType.IntegerType> findLSB(value: Operand<T>): Operand<T> {
        return Operand.SystemFunction("findLSB", value.type, listOf(value))
    }

    fun <T : VarType.IntegerType> findMSB(value: Operand<T>): Operand<T> {
        return Operand.SystemFunction("findMSB", value.type, listOf(value))
    }
}