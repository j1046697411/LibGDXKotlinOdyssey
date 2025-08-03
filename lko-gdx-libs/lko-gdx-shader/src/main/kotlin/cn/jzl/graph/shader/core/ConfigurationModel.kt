package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.PropertyContainer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

interface ConfigurationModel {
    val position: Vector3
    val worldTransform: Matrix4
    val propertyContainer: PropertyContainer
}