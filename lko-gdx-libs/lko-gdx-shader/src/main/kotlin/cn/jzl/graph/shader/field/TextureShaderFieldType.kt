package cn.jzl.graph.shader.field

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.render.field.TextureType
import cn.jzl.graph.shader.builder.property.PropertyLocation
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.builder.property.TextureShaderPropertySource
import cn.jzl.graph.shader.core.FragmentShaderBuilder
import cn.jzl.graph.shader.core.VertexShaderBuilder
import cn.jzl.graph.shader.core.getGlobalUniforms
import cn.jzl.graph.shader.core.getModelUniforms
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.math.Vector4

internal object TextureShaderFieldType : ShaderFieldType<TextureFieldOutput> {
    override val realFieldType = TextureType
    override val numberOfComponents: Int get() = throw UnsupportedOperationException("TextureShaderFieldType has no number of components")
    override val fieldType: String = "sampler2D"

    override fun addProperty(
        graph: GraphWithProperties,
        graphNode: GraphNode,
        vertexShaderBuilder: VertexShaderBuilder,
        fragmentShaderBuilder: FragmentShaderBuilder,
        fragmentShader: Boolean,
        propertySource: ShaderPropertySource
    ): FieldOutput {
        check(propertySource is TextureShaderPropertySource) { "propertySource must be TextureShaderPropertySource" }
        val textureDescriptor = TextureDescriptor<Texture>()
        textureDescriptor.minFilter = propertySource.minFilter
        textureDescriptor.magFilter = propertySource.magFilter
        textureDescriptor.vWrap = Texture.TextureWrap.ClampToEdge
        textureDescriptor.uWrap = Texture.TextureWrap.ClampToEdge
        val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
        val textureVariableName = "u_texture_${propertySource.propertyName}"
        val propertyKey = realFieldType.createPropertyKey(propertySource.propertyName)
        val global = propertySource.propertyLocation == PropertyLocation.GlobalUniform
        commonShaderBuilder.addUniformVariable(
            textureVariableName,
            fieldType,
            global,
            "${realFieldType.fieldType} texture - ${propertySource.propertyName}"
        ) { shaderContext, shader, location ->
            textureDescriptor.texture = if (global) {
                shaderContext.getGlobalUniforms(shader)[propertyKey]
            } else {
                shaderContext.getModelUniforms(shader, shaderContext.model)[propertyKey]
            }
            shader.setUniform(location, textureDescriptor)
        }
        val uvFieldType = Vector4ShaderFieldType
        val uvName = "v_uv_${propertySource.propertyName}"
        val uvRepresentation = if (propertySource.propertyLocation == PropertyLocation.Attribute) {
            vertexShaderBuilder.addAttribute(
                uvName,
                componentCount = 4,
                type = uvFieldType.fieldType,
                comment = "${realFieldType.fieldType} uv - ${propertySource.propertyName}"
            )
            if (fragmentShader) {
                val uvVariableName = "v_uv_${propertySource.propertyName}"
                vertexShaderBuilder.addVariable(uvVariableName, uvFieldType.fieldType, true)
                fragmentShaderBuilder.addVariable(uvVariableName, uvFieldType.fieldType, true)
                vertexShaderBuilder.addMainLine("${uvFieldType.fieldType} $uvVariableName = ${uvName};")
                uvVariableName
            } else {
                uvName
            }
        } else {
            val uv = Vector4(0f, 0f, 1f, 1f)
            commonShaderBuilder.addUniformVariable(
                uvName,
                uvFieldType.fieldType,
                global,
                "${realFieldType.fieldType} uv - ${propertySource.propertyName}"
            ) { shaderContext, shader, location ->
                shader.setUniform(location, uv)
            }
            uvName
        }
        return DefaultTextureFieldOutput(
            fieldType = this,
            samplerRepresentation = textureVariableName,
            representation = uvRepresentation,
            uWrap = Texture.TextureWrap.ClampToEdge,
            vWrap = Texture.TextureWrap.ClampToEdge,
        )
    }
}