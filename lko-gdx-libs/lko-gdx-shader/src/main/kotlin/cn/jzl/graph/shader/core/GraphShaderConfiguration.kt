package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.HierarchicalPropertyContainer
import cn.jzl.graph.common.config.PropertyContainer

class GraphShaderConfiguration(
    private val shader: GraphShader,
    rootPropertyContainer: PropertyContainer
) : ShaderConfiguration {

    override val propertyContainer: PropertyContainer = HierarchicalPropertyContainer(rootPropertyContainer)

    override fun getShaderAttributes(alias: String): GraphShader.Attribute {
        return shader.attributes[alias] ?: throw IllegalArgumentException("Attribute not found for alias: $alias")
    }
}