package cn.jzl.graph.shader.renderer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphConfiguration
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.TripleInputPipelineNodeProducer
import cn.jzl.graph.render.*
import cn.jzl.graph.render.field.CameraType
import cn.jzl.graph.render.field.RenderingPipelineType
import cn.jzl.graph.shader.ModelShaderLoader
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import cn.jzl.graph.shader.renderer.strategy.ModelRenderingStrategy
import cn.jzl.graph.shader.renderer.strategy.Order
import cn.jzl.graph.shader.renderer.strategy.ShaderSortModelRenderingStrategy
import com.badlogic.gdx.graphics.Camera

class ShaderRendererPipelineNodeProducer : TripleInputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>("Model Shaders", "GraphShaderRenderer", "Renderer/ShaderRenderer") {
    override val first = createNodeInput(
        fieldId = "enabled",
        fieldName = "enabled",
        required = true,
        fieldTypes = arrayOf(PrimitiveFieldTypes.BooleanFieldType)
    )
    override val second = createNodeInput(
        fieldId = "camera",
        fieldName = "camera",
        required = true,
        fieldTypes = arrayOf(CameraType)
    )
    override val third = createNodeInput(
        fieldId = "input",
        fieldName = "input",
        required = true,
        fieldTypes = arrayOf(RenderingPipelineType)
    )

    override val output = createNodeOutput("output", "output", RenderingPipelineType)

    @Suppress("UNCHECKED_CAST")
    override fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        third: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        checkNotNull(second) { "camera is null" }
        checkNotNull(third) { "input is null" }
        val modelShaderLoader by world.instance<ModelShaderLoader>()
        val openGLContext by world.instance<OpenGLContext>()
        val graph = graphNode.payloads.getValue("shaders") as GraphWithProperties
        val tag = graphNode.payloads.getValue("tag") as String
        val endNodeId = graphNode.payloads.getValue("endNodeId") as String
        val shader = modelShaderLoader.loadShader(graph, configuration, tag, endNodeId)
        val shaderConfiguration: ShaderRendererConfiguration<Any> = configuration.getConfiguration()
        val shaderContext = GraphShaderContext(shaderConfiguration, configuration.timeProvider, openGLContext)
        val renderingStrategy = ShaderSortModelRenderingStrategy(Order.FrontToBack)
        val strategyCallback = RenderingStrategyCallback(shaderContext, shaderConfiguration)
        shaderConfiguration.registerShader(shader)
        return RenderingPipelineNode { blackboard ->
            val enabled = first?.let { blackboard[it.fromGraphNode, it.fromOutput, PrimitiveFieldTypes.BooleanFieldType] } ?: true
            val renderPipeline = blackboard[third.fromGraphNode, third.fromOutput, RenderingPipelineType]
            if (enabled) {
                val camera = blackboard[second.fromGraphNode, second.fromOutput, CameraType]
                renderPipeline.command {
                    renderingStrategy.processModels(renderPipeline, shaderConfiguration, sequenceOf(shader), camera, strategyCallback)
                }
            }
            blackboard[graphNode, output.output, output.outputType] = renderPipeline
        }
    }

    private inline fun <reified C : GraphConfiguration> GraphPipelineConfiguration.getConfiguration(): C = getConfiguration(C::class)

    private class RenderingStrategyCallback(
        private val shaderContext: GraphShaderContext,
        private val shaderRendererConfiguration: ShaderRendererConfiguration<Any>
    ) : ModelRenderingStrategy.StrategyCallback {

        override fun begin() {
        }

        override fun process(renderingPipeline: RenderingPipeline, camera: Camera, shader: GraphShader, model: Any) {
            shaderContext.bind(renderingPipeline, shader, camera, model)
            shader.render(shaderContext, shaderRendererConfiguration)
        }

        override fun end() {
            shaderContext.unbind()
        }
    }
}

