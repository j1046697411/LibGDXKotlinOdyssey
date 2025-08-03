package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext
import cn.jzl.graph.shader.core.getGlobalUniforms
import cn.jzl.graph.shader.core.getModelUniforms

internal object Vector2ShaderFieldType : AbstractShaderFieldType<FieldOutput>() {
    override val realFieldType = Vector2Type
    override val numberOfComponents: Int = 2
    override val fieldType: String = "vec2"
    override fun setModelUniform(
        shaderContext: ShaderContext,
        shader: GraphShader,
        location: Int,
        propertySource: ShaderPropertySource
    ) {
        val propertyKey = realFieldType.createPropertyKey(propertySource.propertyName)
        shader.setUniform(location, shaderContext.getModelUniforms(shader, shaderContext.model)[propertyKey])
    }

    override fun setGlobalUniform(
        shaderContext: ShaderContext,
        shader: GraphShader,
        location: Int,
        propertySource: ShaderPropertySource
    ) {
        val propertyKey = realFieldType.createPropertyKey(propertySource.propertyName)
        shader.setUniform(location, shaderContext.getGlobalUniforms(shader)[propertyKey])
    }
}