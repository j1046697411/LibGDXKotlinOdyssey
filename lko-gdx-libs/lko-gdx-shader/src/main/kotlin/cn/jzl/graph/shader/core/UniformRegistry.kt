package cn.jzl.graph.shader.core

interface UniformRegistry {

    fun registerAttribute(alias: String, componentCount: Int)

    fun registerGlobalUniform(alias: String, setter: UniformSetter)

    fun registerLocalUniform(alias: String, setter: UniformSetter)

    fun registerGlobalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: StructArrayUniformSetter)

    fun registerLocalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: StructArrayUniformSetter)

    fun interface UniformSetter {
        fun set(shaderContext: ShaderContext, shader: GraphShader, location: Int)
    }

    fun interface StructArrayUniformSetter {
        fun set(
            shaderContext: ShaderContext,
            shader: GraphShader,
            startingLocation: Int,
            fieldOffsets: IntArray,
            structSize: Int
        )
    }

    fun apply(shader: Shader, binder: ShaderLocationBinder)
}

