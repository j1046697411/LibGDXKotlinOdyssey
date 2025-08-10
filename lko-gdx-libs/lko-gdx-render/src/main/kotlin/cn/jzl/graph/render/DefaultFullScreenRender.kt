package cn.jzl.graph.render

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.glutils.ShaderProgram

internal class DefaultFullScreenRender : FullScreenRender {

    private val mesh by lazy {
        val mesh = Mesh(true, 4, 6, VertexAttributes(VertexAttribute.Position()))
        mesh.setVertices(floatArrayOf(
            0f, 0f, 0f,
            0f, 1f, 0f,
            1f, 0f, 0f,
            1f, 1f, 0f
        ))
        mesh.setIndices(shortArrayOf(0, 2, 1, 2, 3, 1))
        mesh.setAutoBind(true)
        mesh
    }

    override fun renderFullScreen(shaderProgram: ShaderProgram) {
        mesh.render(shaderProgram, GL20.GL_TRIANGLES)
    }
}