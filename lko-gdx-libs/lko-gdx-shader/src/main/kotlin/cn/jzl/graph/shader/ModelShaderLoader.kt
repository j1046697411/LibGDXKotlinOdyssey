package cn.jzl.graph.shader

import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.shader.core.GraphShader

interface ModelShaderLoader {
    fun loadShader(graph: GraphWithProperties, configuration: GraphPipelineConfiguration, tag: String, endNodeId: String): GraphShader
}

