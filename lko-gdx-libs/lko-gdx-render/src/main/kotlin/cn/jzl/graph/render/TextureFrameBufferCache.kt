package cn.jzl.graph.render

import com.badlogic.gdx.graphics.Pixmap

interface TextureFrameBufferCache {

    fun obtainFrameBuffer(width: Int, height: Int, format: Pixmap.Format): TextureFrameBuffer

    fun freeFrameBuffer(frameBuffer: TextureFrameBuffer)
}