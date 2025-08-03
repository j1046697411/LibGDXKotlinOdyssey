package cn.jzl.graph.shader.core

interface Shader {
    fun registerAttribute(alias: String, componentCount: Int, location: Int)
    fun registerUniform(alias: String, global: Boolean, location: Int, uniformSetter: UniformRegistry.UniformSetter)
    fun registerStructArrayUniform(
        alias: String,
        global: Boolean,
        fieldNames: Array<String>,
        startIndex: Int,
        size: Int,
        fieldOffsets: IntArray,
        setter: UniformRegistry.StructArrayUniformSetter
    )
}

