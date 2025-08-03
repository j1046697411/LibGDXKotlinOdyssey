package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.CompositePropertyContainer
import cn.jzl.graph.common.config.PropertyContainer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

class DefaultConfigurationModel(
    shaderConfiguration: ShaderConfiguration,
    private val renderableModel: RenderableModel
) : ConfigurationModel {
    override val position: Vector3 get() = renderableModel.position
    override val worldTransform: Matrix4 get() = renderableModel.worldTransform
    override val propertyContainer: PropertyContainer = CompositePropertyContainer(
        renderableModel.propertyContainer,
        shaderConfiguration.propertyContainer
    )
}