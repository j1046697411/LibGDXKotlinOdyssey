package cn.jzl.graph.shader.field

import com.badlogic.gdx.graphics.Texture

data class DefaultTextureFieldOutput(
    override val fieldType: ShaderFieldType<out TextureFieldOutput>,
    override val samplerRepresentation: String,
    override val representation: String,
    override val uWrap: Texture.TextureWrap = Texture.TextureWrap.ClampToEdge,
    override val vWrap: Texture.TextureWrap = Texture.TextureWrap.ClampToEdge
) : TextureFieldOutput