package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext
import cn.jzl.graph.shader.core.getGlobalUniforms
import cn.jzl.graph.shader.core.getModelUniforms

object ColorShaderFieldType : AbstractShaderFieldType<FieldOutput>() {
    override val realFieldType: ColorType = ColorType
    override val numberOfComponents: Int = 4
    override val fieldType: String = "vec4"

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