package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.GraphConfiguration
import cn.jzl.graph.common.config.PropertyContainer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

interface ShaderRendererConfiguration<M> : ModelContainer<M>, GraphConfiguration {

    fun registerShader(shader: GraphShader)

    fun getGlobalUniforms(shader: GraphShader) : PropertyContainer

    fun getPosition(shader: GraphShader, model: M): Vector3

    fun getWorldTransform(shader: GraphShader, model: M): Matrix4

    fun getModelUniforms(shader: GraphShader, model: M) : PropertyContainer

    fun isRendered(shader: GraphShader, camera: Camera, model: M): Boolean

    fun render(shader: ShaderContext, shaderProgram: ShaderProgram, model: M, propertyToLocationMapping: (String) -> Int)
}