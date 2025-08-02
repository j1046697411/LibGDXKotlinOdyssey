package cn.jzl.graph.render

import com.badlogic.gdx.graphics.glutils.FrameBuffer

interface BufferCopyHelper {
    fun copy(
        from: FrameBuffer,
        to: FrameBuffer?,
        renderContext: OpenGLContext,
        fullScreenRender: FullScreenRender,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    )
}