package cn.jzl.graph.shader.core

import cn.jzl.shader.Precision
import cn.jzl.shader.PrecisionDeclaration
import cn.jzl.shader.VarType

interface UniformRegistry {

    fun register(registryCallback: RegistryCallback): UniformRegistry

    fun interface RegistryCallback {
        fun GraphProgramRegistry.register(shaderLocationBinder: ShaderLocationBinder)
    }
}

fun UniformRegistry.registerAttribute(
    alias: String,
    componentCount: Int,
): UniformRegistry = register { registerAttribute(alias, componentCount, it.getAttributeLocation(alias)) }

fun UniformRegistry.registerUniform(
    alias: String,
    global: Boolean = true,
    pedantic: Boolean = false,
    uniformSetter: UniformSetter
): UniformRegistry = register { registerUniform(alias, global, it.getUniformLocation(alias, pedantic), uniformSetter) }

fun GraphProgramScope.uniform(
    alias: String,
    value: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Integer> = int.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, value) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Int,
    v2: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.IVec2> = ivec2.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Int,
    v2: Int,
    v3: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.IVec3> = ivec3.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2, v3) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Int,
    v2: Int,
    v3: Int,
    v4: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.IVec4> = ivec4.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2, v3, v4) }

fun GraphProgramScope.uniform1(
    alias: String,
    value: FloatArray,
    offset: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Float> = float.uniform(alias, global, pedantic, location, precision) { program.setUniform1(it, value, offset) }

fun GraphProgramScope.uniform2(
    alias: String,
    value: FloatArray,
    offset: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec2> = vec2.uniform(alias, global, pedantic, location, precision) { program.setUniform2(it, value, offset) }

fun GraphProgramScope.uniform3(
    alias: String,
    value: FloatArray,
    offset: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec3> = vec3.uniform(alias, global, pedantic, location, precision) { program.setUniform3(it, value, offset) }

fun GraphProgramScope.uniform4(
    alias: String,
    value: FloatArray,
    offset: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec4> = vec4.uniform(alias, global, pedantic, location, precision) { program.setUniform4(it, value, offset) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Float,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Float> = float.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Float,
    v2: Float,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec2> = vec2.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Float,
    v2: Float,
    v3: Float,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec3> = vec3.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2, v3) }

fun GraphProgramScope.uniform(
    alias: String,
    v1: Float,
    v2: Float,
    v3: Float,
    v4: Float,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Vec4> = vec4.uniform(alias, global, pedantic, location, precision) { program.setUniform(it, v1, v2, v3, v4) }

fun GraphProgramScope.uniformMatrix4(
    alias: String,
    value: FloatArray,
    offset: Int,
    global: Boolean = true,
    pedantic: Boolean = false,
    location: Int = -1,
    precision: Precision = Precision.Default,
): PrecisionDeclaration<VarType.Mat4> = mat4.uniform(alias, global, pedantic, location, precision) { program.setUniformMatrix4(it, value, offset) }
