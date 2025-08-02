package cn.jzl.graph.render

import com.badlogic.gdx.graphics.Texture

interface RenderingPipelineBuffer {
    val colorBufferTexture: Texture
    val width: Int
    val height: Int

    fun begin()

    fun end()
}