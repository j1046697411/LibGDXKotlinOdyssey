package cn.jzl.graph.shader.builder.property

import com.badlogic.gdx.graphics.Texture

interface TextureShaderPropertySource : ShaderPropertySource {
    val minFilter: Texture.TextureFilter
    val magFilter: Texture.TextureFilter
}