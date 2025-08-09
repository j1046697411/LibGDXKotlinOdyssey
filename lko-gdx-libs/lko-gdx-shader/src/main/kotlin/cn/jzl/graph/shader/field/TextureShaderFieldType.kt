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
import com.badlogic.gdx.math.Vector2

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
        return when (propertySource.propertyLocation) {
            PropertyLocation.Uniform -> {
                val textureVariableName = "u_texture_${propertySource.propertyName}"
                val uvVariableName = "v_uv_${propertySource.propertyName}"
                val propertyKey = realFieldType.createPropertyKey(propertySource.propertyName)
                val uv = Vector2(1f, 1f)
                commonShaderBuilder.addUniformVariable(
                    textureVariableName,
                    fieldType,
                    false,
                    "${realFieldType.fieldType} texture - ${propertySource.propertyName}"
                ) { shaderContext, shader, location ->
                    val value = shaderContext.getModelUniforms(shader, shaderContext.model)[propertyKey]
                    textureDescriptor.texture = value
                    shader.setUniform(location, textureDescriptor)
                }
                commonShaderBuilder.addUniformVariable(
                    uvVariableName,
                    "vec2",
                    false,
                    "vec2 uv - ${propertySource.propertyName}"
                ) { shaderContext, shader, location -> shader.setUniform(location, uv) }
                DefaultTextureFieldOutput(this,textureVariableName, uvVariableName, Texture.TextureWrap.ClampToEdge)
            }

            PropertyLocation.GlobalUniform -> {
                val textureVariableName = "u_texture_${propertySource.propertyName}"
                val uvVariableName = "v_uv_${propertySource.propertyName}"
                val propertyKey = realFieldType.createPropertyKey(propertySource.propertyName)
                val uv = Vector2(1f, 1f)
                commonShaderBuilder.addUniformVariable(
                    textureVariableName,
                    fieldType,
                    true,
                    "${realFieldType.fieldType} texture - ${propertySource.propertyName}"
                ) { shaderContext, shader, location ->
                    val value = shaderContext.getGlobalUniforms(shader)[propertyKey]
                    textureDescriptor.texture = value
                    shader.setUniform(location, textureDescriptor)
                }
                commonShaderBuilder.addUniformVariable(
                    uvVariableName,
                    "vec2",
                    true,
                    "vec2 uv - ${propertySource.propertyName}"
                ) { shaderContext, shader, location -> shader.setUniform(location, uv) }
                DefaultTextureFieldOutput(this,textureVariableName, uvVariableName, Texture.TextureWrap.ClampToEdge)
            }
            else -> throw UnsupportedOperationException("TextureShaderFieldType only supports Uniform and GlobalUniform")
        }
    }
}