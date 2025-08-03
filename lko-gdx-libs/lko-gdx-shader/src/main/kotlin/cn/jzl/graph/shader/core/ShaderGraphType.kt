package cn.jzl.graph.shader.core

import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.rendering.GraphPipelineService
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode

interface ShaderGraphType : GraphType<GraphShaderPipelineNode> {
    val shaderFieldTypeResolver: ShaderFieldTypeResolver

    fun buildShaderGraphPipeline(
        graphPipelineService: GraphPipelineService,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        tag: String,
        endNodeId: String,
    ): GraphShader
}

