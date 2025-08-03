package cn.jzl.graph.shader.core

import cn.jzl.graph.common.config.CompositePropertyContainer
import cn.jzl.graph.common.config.PropertyContainer
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3

class SimpleShaderRendererConfiguration(
    private val rootPropertyContainer: PropertyContainer
) : ShaderRendererConfiguration<RenderableModel>, ShaderInformation {

    private val shaderConfigurations = mutableMapOf<String, ShaderConfiguration>()
    private val models = mutableSetOf<RenderableModel>()

    override fun registerShader(shader: GraphShader) {
        shaderConfigurations[shader.tag] = GraphShaderConfiguration(shader, rootPropertyContainer)
    }

    override fun getGlobalUniforms(shader: GraphShader): PropertyContainer {
        return getShaderConfiguration(shader.tag).propertyContainer
    }

    override fun getPosition(shader: GraphShader, model: RenderableModel): Vector3 {
        return model.position
    }

    override fun getWorldTransform(shader: GraphShader, model: RenderableModel): Matrix4 {
        return model.worldTransform
    }

    override fun getModelUniforms(shader: GraphShader, model: RenderableModel): PropertyContainer {
        return CompositePropertyContainer(model.propertyContainer, getShaderConfiguration(shader.tag).propertyContainer)
    }

    override fun getShaderConfiguration(tag: String): ShaderConfiguration {
        val shaderConfiguration = shaderConfigurations[tag]
        checkNotNull(shaderConfiguration) { "Shader configuration not found for tag: $tag" }
        return shaderConfiguration
    }

    override fun isRendered(
        shader: GraphShader,
        camera: Camera,
        model: RenderableModel
    ): Boolean = model.isRendered(shader, camera)

    override fun render(
        shader: ShaderContext,
        model: RenderableModel,
        propertyToLocationMapping: (String) -> Int
    ) {
        TODO("Not yet implemented")
    }

    override fun minusAssign(model: RenderableModel) {
        this.models -= model
    }

    override fun plusAssign(model: RenderableModel) {
        this.models += model
    }

    override fun clear() {
        this.models.clear()
    }

    override fun iterator(): Iterator<RenderableModel> = models.iterator()
}