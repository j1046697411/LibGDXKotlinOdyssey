package cn.jzl.graph.shader.core

import cn.jzl.di.instance
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.rendering.GraphPipelineService
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.render.OpenGLContext
import cn.jzl.graph.shader.DefaultGraphShader
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import cn.jzl.graph.validator.GraphValidationResult
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import ktx.log.logger

class ModelShaderGraphType(
    shaderFieldTypeResolver: ShaderFieldTypeResolver,
    type: String
) : AbstractShaderGraphType(shaderFieldTypeResolver, type) {

    override fun validate(graph: GraphWithProperties): GraphValidationResult {
        TODO("Not yet implemented")
    }

    override fun buildShaderGraphPipeline(
        graphPipelineService: GraphPipelineService,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        tag: String,
        endNodeId: String
    ): GraphShader {
        val blackboard by graphPipelineService.world.instance<PipelineBlackboard>()
        val openGLContext by graphPipelineService.world.instance<OpenGLContext>()
        val vertexShaderNodes = graphPipelineService.buildGraphPipeline<GraphShaderPipelineNode>(graph, configuration, endNodeId, arrayOf("position"))
        val fragmentShaderNodes = graphPipelineService.buildGraphPipeline<GraphShaderPipelineNode>(graph, configuration, endNodeId, arrayOf("color", "alpha", "discardValue"))
        val uniformRegistry = DefaultUniformRegistry()
        val vertexShaderBuilder = VertexShaderBuilder(uniformRegistry)
        val fragmentShaderBuilder = FragmentShaderBuilder(uniformRegistry)
        vertexShaderNodes.forEach { it.buildGraphShader(blackboard, vertexShaderBuilder, fragmentShaderBuilder, false) }
        fragmentShaderNodes.forEach { it.buildGraphShader(blackboard, vertexShaderBuilder, fragmentShaderBuilder, true) }
        val vertexShaderProgram = vertexShaderBuilder.buildProgram()
        val fragmentShaderProgram = fragmentShaderBuilder.buildProgram()
        log.debug { "Vertex shader program:\n$vertexShaderProgram" }
        log.debug { "Fragment shader program:\n$fragmentShaderProgram" }
        val shaderProgram = ShaderProgram(vertexShaderProgram, fragmentShaderProgram)
        val graphShader = DefaultGraphShader(openGLContext,uniformRegistry, shaderProgram, tag)
        graphShader.initialize()
        log.debug { "Shader attributes:\n${graphShader.attributes}" }
        return graphShader
    }

    private companion object {
        val log = logger<ModelShaderGraphType>()
    }
}