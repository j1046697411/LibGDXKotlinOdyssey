package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.PropertyContainer
import cn.jzl.graph.common.time.TimeProvider
import com.badlogic.gdx.graphics.Camera

interface ShaderContext {
    val shader: GraphShader
    val timeProvider: TimeProvider
    val shaderRendererConfiguration: ShaderRendererConfiguration<Any>
    val model: Any
    val camera: Camera
}

fun ShaderContext.getGlobalUniforms(shader: GraphShader): PropertyContainer {
    return shaderRendererConfiguration.getGlobalUniforms(shader)
}

fun ShaderContext.getModelUniforms(shader: GraphShader, model: Any): PropertyContainer {
    return shaderRendererConfiguration.getModelUniforms(shader, model)
}
