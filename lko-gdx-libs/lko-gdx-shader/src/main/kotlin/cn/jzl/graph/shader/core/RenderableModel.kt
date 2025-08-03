package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.PropertyContainer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

interface RenderableModel {
    val position: Vector3
    val worldTransform: Matrix4
    val propertyContainer: PropertyContainer
    fun isRendered(shader: GraphShader, camera: Camera): Boolean
    fun render(camera: Camera, shaderProgram: ShaderProgram, propertyToLocationMapping: (String) -> Int)
}