package cn.jzl.graph.shader.renderer.strategy

import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import com.badlogic.gdx.graphics.Camera

class ShaderUnorderedModelRenderingStrategy : ModelRenderingStrategy {
    override fun processModels(
        renderingPipeline: RenderingPipeline,
        configuration: ShaderRendererConfiguration<Any>,
        shaders: Sequence<GraphShader>,
        camera: Camera,
        callback: ModelRenderingStrategy.StrategyCallback
    ) {
        callback.begin()
        for (shader in shaders) {
            for (model in configuration) {
                if (configuration.isRendered(shader, camera, model)) callback.process(renderingPipeline, camera, shader, model)
            }
        }
        callback.end()
    }
}