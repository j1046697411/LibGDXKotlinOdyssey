package cn.jzl.graph.shader.renderer

import cn.jzl.graph.common.time.TimeProvider
import cn.jzl.graph.render.OpenGLContext
import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import com.badlogic.gdx.graphics.Camera

internal class GraphShaderContext(
    override val shaderRendererConfiguration: ShaderRendererConfiguration<Any>,
    override val timeProvider: TimeProvider,
    private val openGLContext: OpenGLContext
) : ShaderContext {
    private var graphShader: GraphShader? = null
    private lateinit var renderingPipeline: RenderingPipeline
    override lateinit var model: Any
    override val shader: GraphShader get() = graphShader!!
    override lateinit var camera: Camera

    fun bind(renderingPipeline: RenderingPipeline, shader: GraphShader, camera: Camera, model: Any) {
        if (graphShader != shader) {
            this.graphShader?.end()
            shader.begin(this, openGLContext)
            this.graphShader = shader
        }
        this.renderingPipeline = renderingPipeline
        this.camera = camera
        this.model = model
    }
}