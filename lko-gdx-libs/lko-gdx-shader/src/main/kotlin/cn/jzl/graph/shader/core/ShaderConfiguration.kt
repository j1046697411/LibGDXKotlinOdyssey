package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.PropertyContainer

interface ShaderConfiguration {

    val propertyContainer: PropertyContainer

    fun getShaderAttributes(alias: String): GraphShader.Attribute
}