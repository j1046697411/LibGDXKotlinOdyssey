package cn.jzl.graph.render.test

import cn.jzl.graph.common.config.DefaultPropertyContainer
import cn.jzl.graph.common.config.PropertyContainer
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.RenderableModel
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import ktx.log.logger

class TestRenderableModel : RenderableModel {

    private val mesh = Mesh(
        true,
        4,  // 4个顶点
        6,  // 6个索引
        VertexAttribute(VertexAttributes.Usage.Position, 3, "a_property_position"),
        VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_property_uv")
    )
    private val vertices = floatArrayOf(
        // 矩形四个顶点的位置和颜色
        -0.5f, -0.5f, 0f, 0f, 0f,
        0.5f, -0.5f, 0f, 1f, 0f,
        0.5f,  0.5f, 0f, 1f, 1f,
        -0.5f,  0.5f, 0f, 0f, 1f,
    )

    init {
        mesh.setVertices(vertices)
        // 两个三角形组成矩形: 左下-右下-右上, 左下-右上-左上
        mesh.setIndices(shortArrayOf(0, 1, 2, 0, 2, 3))
        mesh.setAutoBind(true)
    }


    override val position: Vector3 = Vector3.Zero
    override val worldTransform: Matrix4 = Matrix4().idt()
    override val propertyContainer: PropertyContainer = DefaultPropertyContainer()
    override fun isRendered(shader: GraphShader, camera: Camera): Boolean {
        return true
    }

    override fun render(camera: Camera, shaderProgram: ShaderProgram, propertyToLocationMapping: (String) -> Int) {
        mesh.render(shaderProgram, GL20.GL_TRIANGLES)
    }

    companion object {
        private val log = logger<RenderScreen>()
    }
}