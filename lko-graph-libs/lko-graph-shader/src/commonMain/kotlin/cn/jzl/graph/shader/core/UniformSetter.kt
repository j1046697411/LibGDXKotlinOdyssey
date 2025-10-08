package cn.jzl.graph.shader.core

fun interface UniformSetter {
    fun ShaderContext.set(location: Int)
}