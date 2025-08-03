package cn.jzl.graph.shader.renderer.strategy

import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import cn.jzl.graph.shader.renderer.strategy.Order
import com.badlogic.gdx.graphics.Camera

class ShaderSortModelRenderingStrategy(private val order: Order = Order.BackToFront) : ModelRenderingStrategy {

    private val models = ktx.collections.GdxArray<Any>()

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
                if (configuration.isRendered(shader, camera, model)) models.add(model)
            }
            models.sort { model1, model2 ->
                val distance1 = configuration.getPosition(shader, model1).dst2(camera.position)
                val distance2 = configuration.getPosition(shader, model2).dst2(camera.position)
                order.result(distance1 * 1000f - distance2 * 1000f)
            }
            models.forEach { model -> callback.process(renderingPipeline, camera, shader, model) }
            models.clear()
        }
        callback.end()
    }

}