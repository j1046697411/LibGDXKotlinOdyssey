package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.Matrix4Type
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext
import cn.jzl.graph.shader.core.getGlobalUniforms
import cn.jzl.graph.shader.core.getModelUniforms

internal object Matrix4ShaderFieldType : AbstractShaderFieldType<FieldOutput>() {
    override val realFieldType = Matrix4Type
    override val fieldType: String = "mat4"
    override val numberOfComponents: Int = 16

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