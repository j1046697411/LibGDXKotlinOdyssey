package cn.jzl.graph.common

import cn.jzl.ecs.World
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties

interface GraphPipelineRecipe {
    fun <PN : PipelineNode> buildGraphPipeline(
        world: World,
        configuration: GraphPipelineConfiguration,
        graph: GraphWithProperties,
        endNodeId: String,
        inputFields: Array<String>
    ): List<PN>
}