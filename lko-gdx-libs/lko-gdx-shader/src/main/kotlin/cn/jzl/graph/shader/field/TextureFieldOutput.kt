package cn.jzl.graph.shader.field

import com.badlogic.gdx.graphics.Texture

interface TextureFieldOutput : FieldOutput {
    val samplerRepresentation: String
    val uWrap: Texture.TextureWrap
    val vWrap: Texture.TextureWrap
}