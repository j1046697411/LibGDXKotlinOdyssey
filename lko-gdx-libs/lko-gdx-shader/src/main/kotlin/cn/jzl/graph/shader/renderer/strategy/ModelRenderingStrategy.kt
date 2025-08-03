package cn.jzl.graph.shader.renderer.strategy

import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import com.badlogic.gdx.graphics.Camera

fun interface ModelRenderingStrategy {

    fun processModels(renderingPipeline: RenderingPipeline, configuration: ShaderRendererConfiguration<Any>, shaders: Sequence<GraphShader>, camera: Camera, callback: StrategyCallback)

    interface StrategyCallback {
        fun begin()
        fun process(renderingPipeline: RenderingPipeline, camera: Camera, shader: GraphShader, model: Any)
        fun end()
    }
}

