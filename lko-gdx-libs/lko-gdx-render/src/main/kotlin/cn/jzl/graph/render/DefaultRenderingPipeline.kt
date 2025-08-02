package cn.jzl.graph.render

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import ktx.collections.GdxArray
import ktx.collections.isNotEmpty

internal class DefaultRenderingPipeline(
    private val openGLContext: OpenGLContext,
    private val textureFrameBufferCache: TextureFrameBufferCache,
    private val bufferCopyHelper: BufferCopyHelper,
    private val fullScreenRender: FullScreenRender
) : RenderingPipeline {

    private var renderingPipelineBuffer: RenderingPipelineBuffer? = null
    private val commands = GdxArray<RenderingPipeline.Command>()

    override fun initializeDefaultBuffer(
        width: Int,
        height: Int,
        format: Pixmap.Format
    ): RenderingPipelineBuffer {
        val frameBuffer = renderingPipelineBuffer ?: getNewFrameBuffer(width, height, format)
        this.renderingPipelineBuffer = frameBuffer
        return frameBuffer
    }

    override fun getNewFrameBuffer(
        width: Int,
        height: Int,
        format: Pixmap.Format
    ): RenderingPipelineBuffer {
        return DefaultRenderingPipelineBuffer(textureFrameBufferCache.obtainFrameBuffer(width, height, format))
    }

    override fun returnFrameBuffer(frameBuffer: RenderingPipelineBuffer) {
        check(frameBuffer is DefaultRenderingPipelineBuffer) { "frameBuffer must be DefaultRenderingPipelineBuffer" }
        textureFrameBufferCache.freeFrameBuffer(frameBuffer.textureFrameBuffer)
    }

    override fun drawTexture(
        paint: RenderingPipelineBuffer,
        canvas: RenderingPipelineBuffer?,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        check(paint is DefaultRenderingPipelineBuffer) { "paint must be DefaultRenderingPipelineBuffer" }
        val canvasBuffer = canvas as? DefaultRenderingPipelineBuffer
        bufferCopyHelper.copy(
            paint.textureFrameBuffer,
            canvasBuffer?.textureFrameBuffer,
            openGLContext,
            fullScreenRender,
            x,
            y,
            width,
            height
        )
    }

    override fun command(command: RenderingPipeline.Command) = commands.add(command)

    override fun executeCommands(): RenderingPipelineBuffer {
        val defaultBuffer = checkNotNull(this.renderingPipelineBuffer) { "defaultBuffer is not initialized" }
        if (commands.isNotEmpty()) {
            defaultBuffer.begin()
            commands.forEach { it.run { openGLContext.execute(defaultBuffer, bufferCopyHelper, fullScreenRender) } }
            defaultBuffer.end()
            commands.clear()
        }
        return defaultBuffer
    }

    override fun dispose() {
        renderingPipelineBuffer?.let { returnFrameBuffer(it) }
        renderingPipelineBuffer = null
    }

    private class DefaultRenderingPipelineBuffer(val textureFrameBuffer: TextureFrameBuffer) : RenderingPipelineBuffer {
        override val colorBufferTexture: Texture get() = textureFrameBuffer.colorBufferTexture
        override val width: Int get() = textureFrameBuffer.width
        override val height: Int get() = textureFrameBuffer.height

        override fun begin() {
            textureFrameBuffer.begin()
        }

        override fun end() {
            textureFrameBuffer.end()
        }
    }
}