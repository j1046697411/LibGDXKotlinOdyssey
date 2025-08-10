package cn.jzl.graph.shader.builder.property

import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.TextureFieldOutput
import com.badlogic.gdx.graphics.Texture

interface TextureShaderPropertySource : ShaderPropertySource {
    val minFilter: Texture.TextureFilter
    val magFilter: Texture.TextureFilter
}

data class DefaultTextureShaderPropertySource(
    override val shaderFieldType: ShaderFieldType<out TextureFieldOutput>,
    override val propertyName: String,
    override val propertyLocation: PropertyLocation,
    override val attributeFunction: String?,
    override val minFilter: Texture.TextureFilter,
    override val magFilter: Texture.TextureFilter,
) : TextureShaderPropertySource
