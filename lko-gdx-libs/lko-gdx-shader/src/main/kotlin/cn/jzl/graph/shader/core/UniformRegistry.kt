package cn.jzl.graph.shader.core

interface UniformRegistry {

    fun registerAttribute(alias: String, componentCount: Int)

    fun registerGlobalUniform(alias: String, setter: UniformSetter)

    fun registerLocalUniform(alias: String, setter: UniformSetter)

    fun registerGlobalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: StructArrayUniformSetter)

    fun registerLocalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: StructArrayUniformSetter)

    fun interface UniformSetter {
        fun set(shaderContext: ShaderContext, shader: Shader, location: Int)
    }

    fun interface StructArrayUniformSetter {
        fun set(
            shaderContext: ShaderContext,
            shader: Shader,
            startingLocation: Int,
            fieldOffsets: IntArray,
            structSize: Int
        )
    }
}