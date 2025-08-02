package cn.jzl.graph.render

import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties

interface PipelineRendererLoader {
    fun loadPipelineRenderer(
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        endNodeId: String
    ): PipelineRenderer
}