package cn.jzl.graph.shader.core

interface ShaderLocationBinder {
    fun getAttributeLocation(alias: String): Int

    fun getUniformLocation(alias: String, pedantic: Boolean = false): Int
}