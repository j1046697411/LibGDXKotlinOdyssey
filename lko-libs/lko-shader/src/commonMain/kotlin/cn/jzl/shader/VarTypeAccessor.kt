package cn.jzl.shader

interface VarTypeAccessor {

    val int: VarType.Integer get() = VarType.Integer
    val ivec2: VarType.IVec2 get() = VarType.IVec2
    val ivec3: VarType.IVec3 get() = VarType.IVec3
    val ivec4: VarType.IVec4 get() = VarType.IVec4

    val float: VarType.Float get() = VarType.Float
    val vec2: VarType.Vec2 get() = VarType.Vec2
    val vec3: VarType.Vec3 get() = VarType.Vec3
    val vec4: VarType.Vec4 get() = VarType.Vec4

    val bool: VarType.Boolean get() = VarType.Boolean
    val bvec2: VarType.BVec2 get() = VarType.BVec2
    val bvec3: VarType.BVec3 get() = VarType.BVec3
    val bvec4: VarType.BVec4 get() = VarType.BVec4

    val mat2: VarType.Mat2 get() = VarType.Mat2
    val mat3: VarType.Mat3 get() = VarType.Mat3
    val mat4: VarType.Mat4 get() = VarType.Mat4

    val sampler1D: VarType.Sampler1D get() = VarType.Sampler1D
    val sampler2D: VarType.Sampler2D get() = VarType.Sampler2D
    val sampler3D: VarType.Sampler3D get() = VarType.Sampler3D
    val samplerCube: VarType.SamplerCube get() = VarType.SamplerCube
}