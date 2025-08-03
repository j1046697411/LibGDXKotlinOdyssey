package cn.jzl.graph.shader.renderer

import cn.jzl.graph.shader.renderer.strategy.ModelWithShader
import com.badlogic.gdx.utils.Pool

internal object ModelWithShaderPool : Pool<ModelWithShader>() {
    override fun newObject(): ModelWithShader {
        return ModelWithShader()
    }
}