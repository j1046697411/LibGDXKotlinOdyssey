package cn.jzl.graph.shader.renderer.strategy

import cn.jzl.graph.shader.core.GraphShader
import com.badlogic.gdx.utils.Pool

data class ModelWithShader(var model: Any? = null, var shader: GraphShader? = null) : Pool.Poolable {
    override fun reset() {
        model = null
        shader = null
    }
}