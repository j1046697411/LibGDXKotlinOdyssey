package cn.jzl.shader

fun ExpressionScope.texture(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "texture", vec4, listOf(sampler, coordinate))
}

fun ExpressionScope.texture(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, bias: Operand<VarType.Float>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "texture", vec4, listOf(sampler, coordinate, bias))
}

fun <C : VarType.FloatVector> ExpressionScope.textureProj(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<C>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "textureProj", vec4, listOf(sampler, coordinate))
}

fun ExpressionScope.textureLod(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, lod: Operand<VarType.Float>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "textureLod", vec4, listOf(sampler, coordinate, lod))
}

fun ExpressionScope.textureSize(sampler: Operand<VarType.Sampler2D>, lod: Operand<VarType.Integer>): Operand<VarType.IVec2> {
    return Operand.SystemFunction(this, "textureSize", ivec2, listOf(sampler, lod))
}

fun ExpressionScope.textureGrad(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, dPdx: Operand<VarType.Vec2>, dPdy: Operand<VarType.Vec2>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "textureGrad", vec4, listOf(sampler, coordinate, dPdx, dPdy))
}

fun ExpressionScope.textureOffset(sampler: Operand<VarType.Sampler2D>, coordinate: Operand<VarType.Vec2>, offset: Operand<VarType.IVec2>): Operand<VarType.Vec4> {
    return Operand.SystemFunction(this, "textureOffset", vec4, listOf(sampler, coordinate, offset))
}