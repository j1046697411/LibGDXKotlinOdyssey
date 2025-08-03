package cn.jzl.graph.shader.field

import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext

internal class DefaultShaderFieldType<FO : FieldOutput>(
    override val realFieldType: FieldType<*>
) : AbstractShaderFieldType<FO>() {
    override val fieldType: String get() = realFieldType.fieldType
    override val numberOfComponents: Int get() = throw UnsupportedOperationException("DefaultShaderFieldType has no number of components")
    override fun setModelUniform(
        shaderContext: ShaderContext,
        shader: GraphShader,
        location: Int,
        propertySource: ShaderPropertySource
    ) {
        TODO("Not yet implemented")
    }

    override fun setGlobalUniform(
        shaderContext: ShaderContext,
        shader: GraphShader,
        location: Int,
        propertySource: ShaderPropertySource
    ) {
        TODO("Not yet implemented")
    }
}

