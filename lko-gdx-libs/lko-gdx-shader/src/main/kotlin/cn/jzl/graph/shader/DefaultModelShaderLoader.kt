package cn.jzl.graph.shader

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.GraphTypeResolver
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.rendering.GraphPipelineService
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode

internal class DefaultModelShaderLoader(world: World) : ModelShaderLoader {

    private val graphPipelineService by world.instance<GraphPipelineService>()
    private val graphTypeResolver by world.instance<GraphTypeResolver>()

    override fun loadShader(graph: GraphWithProperties, configuration: GraphPipelineConfiguration, tag: String, endNodeId: String): GraphShader {
        val shaderGraphType = graphTypeResolver.resolve<GraphShaderPipelineNode>(graph.type)
        check(shaderGraphType is ShaderGraphType) { "Graph type must be ShaderGraphType" }
        return shaderGraphType.buildShaderGraphPipeline(graphPipelineService, graph, configuration, tag, endNodeId)
    }
}