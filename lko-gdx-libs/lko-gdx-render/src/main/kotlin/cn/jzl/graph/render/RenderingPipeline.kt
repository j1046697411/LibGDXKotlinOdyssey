package cn.jzl.graph.render

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.utils.Disposable

interface RenderingPipeline : Disposable {

    fun initializeDefaultBuffer(width: Int, height: Int, format: Pixmap.Format): RenderingPipelineBuffer

    fun getNewFrameBuffer(width: Int, height: Int, format: Pixmap.Format): RenderingPipelineBuffer

    fun returnFrameBuffer(frameBuffer: RenderingPipelineBuffer)

    fun drawTexture(
        paint: RenderingPipelineBuffer,
        canvas: RenderingPipelineBuffer?,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    )

    fun command(command: Command)

    fun executeCommands(): RenderingPipelineBuffer

    fun interface Command {
        fun OpenGLContext.execute(
            canvas: RenderingPipelineBuffer,
            bufferCopyHelper: BufferCopyHelper,
            fullScreenRender: FullScreenRender
        )
    }
}

fun RenderingPipeline.command(
    command: OpenGLContext.() -> Unit
): Unit = command { canvas, bufferCopyHelper, fullScreenRender -> command() }
