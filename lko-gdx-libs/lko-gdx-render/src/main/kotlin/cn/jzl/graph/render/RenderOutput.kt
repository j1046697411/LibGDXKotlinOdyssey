package cn.jzl.graph.render

import com.badlogic.gdx.Gdx

interface RenderOutput {

    fun output(openGLContext: OpenGLContext, fullScreenRender: FullScreenRender, renderingPipeline: RenderingPipeline)

    companion object : RenderOutput {
        override fun output(
            openGLContext: OpenGLContext,
            fullScreenRender: FullScreenRender,
            renderingPipeline: RenderingPipeline
        ) = renderingPipeline.drawTexture(
            renderingPipeline.executeCommands(),
            null,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )
    }
}