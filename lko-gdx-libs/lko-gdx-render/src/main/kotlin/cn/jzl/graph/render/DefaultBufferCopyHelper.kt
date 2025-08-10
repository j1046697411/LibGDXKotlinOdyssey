package cn.jzl.graph.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram

internal class DefaultBufferCopyHelper : BufferCopyHelper {

    private val shaderProgram by lazy { ShaderProgram(VART_DRAW_TEXTURE, FRAG_DRAW_TEXTURE) }
    override fun copy(
        from: FrameBuffer,
        to: FrameBuffer?,
        renderContext: OpenGLContext,
        fullScreenRender: FullScreenRender,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        to?.begin()
        val bufferWidth = to?.width ?: Gdx.graphics.width
        val bufferHeight = to?.height ?: Gdx.graphics.height

        renderContext.setDepthTest(0)
        renderContext.setDepthMask(false)
        renderContext.setBlending(false, 0, 0)
        renderContext.setCullFace(GL20.GL_BACK)
        shaderProgram.bind()
        shaderProgram.setUniformi("u_sourceTexture", renderContext.bindTexture(from.colorBufferTexture))
        shaderProgram.setUniformf("u_sourcePosition", 0f, 0f)
        shaderProgram.setUniformf("u_sourceSize", 1f, 1f)
        shaderProgram.setUniformf("u_targetPosition", x / bufferWidth, y / bufferHeight)
        shaderProgram.setUniformf("u_targetSize", width / bufferWidth, height / bufferHeight)

        fullScreenRender.renderFullScreen(shaderProgram)
        to?.end()
    }

    companion object {
        private const val VART_DRAW_TEXTURE = """
            attribute vec3 a_position;

            uniform vec2 u_targetPosition;
            uniform vec2 u_targetSize;

            varying vec2 v_position;

            void main() {
                v_position = a_position.xy;
                vec2 result = u_targetPosition + a_position.xy * u_targetSize;
                gl_Position = vec4((result * 2.0 - 1.0), 1.0, 1.0);
            }
        """
        private const val FRAG_DRAW_TEXTURE = """
            #ifdef GL_ES
            precision mediump float;
            #endif

            uniform sampler2D u_sourceTexture;
            uniform vec2 u_sourcePosition;
            uniform vec2 u_sourceSize;

            varying vec2 v_position;

            void main() {
                vec2 sourcePosition = u_sourcePosition + v_position * u_sourceSize;
                vec4 color = texture2D(u_sourceTexture, sourcePosition);
                gl_FragColor = color;
            }
        """
    }
}