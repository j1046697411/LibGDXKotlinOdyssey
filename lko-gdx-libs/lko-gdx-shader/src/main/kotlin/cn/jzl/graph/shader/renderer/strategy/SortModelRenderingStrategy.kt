package cn.jzl.graph.shader.renderer.strategy

import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import cn.jzl.graph.shader.renderer.strategy.ModelWithShader
import cn.jzl.graph.shader.renderer.ModelWithShaderPool
import cn.jzl.graph.shader.renderer.strategy.Order
import com.badlogic.gdx.graphics.Camera
import ktx.collections.GdxArray
import ktx.collections.isNotEmpty

class SortModelRenderingStrategy(private val order: Order = Order.BackToFront) : ModelRenderingStrategy {
    private val modelWithShaders = ktx.collections.GdxArray<ModelWithShader>()

    override fun processModels(
        renderingPipeline: RenderingPipeline,
        configuration: ShaderRendererConfiguration<Any>,
        shaders: Sequence<GraphShader>,
        camera: Camera,
        callback: ModelRenderingStrategy.StrategyCallback
    ) {
        callback.begin()
        for (model in configuration) {
            for (shader in shaders) {
                if (configuration.isRendered(shader, camera, model)) {
                    val modelWithShader = ModelWithShaderPool.obtain()
                    modelWithShader.model = model
                    modelWithShader.shader = shader
                    modelWithShaders.add(modelWithShader)
                }
            }
        }
        sorted(configuration, camera, modelWithShaders)
        modelWithShaders.forEach { callback.process(renderingPipeline, camera, it.shader!!, it.model!!) }
        clearSortingArray()
        callback.end()
    }

    private fun clearSortingArray() {
        if (modelWithShaders.isNotEmpty()) {
            ModelWithShaderPool.freeAll(modelWithShaders)
            modelWithShaders.clear()
        }
    }

    private fun sorted(configuration: ShaderRendererConfiguration<Any>, camera: Camera, models: GdxArray<ModelWithShader>) {
        models.sort { model1, model2 ->
            val distance1 = configuration.getPosition(model1.shader!!, model1.model!!).dst2(camera.position)
            val distance2 = configuration.getPosition(model2.shader!!, model2.model!!).dst2(camera.position)
            order.result(distance1 * 1000f - distance2 * 1000f)
        }
    }
}