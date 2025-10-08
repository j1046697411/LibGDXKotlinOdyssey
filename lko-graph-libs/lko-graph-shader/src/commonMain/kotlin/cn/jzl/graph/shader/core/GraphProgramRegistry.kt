package cn.jzl.graph.shader.core

interface GraphProgramRegistry {
    fun registerAttribute(alias: String, componentCount: Int, location: Int)

    fun registerUniform(alias: String, global: Boolean, location: Int, setter: UniformSetter)
}