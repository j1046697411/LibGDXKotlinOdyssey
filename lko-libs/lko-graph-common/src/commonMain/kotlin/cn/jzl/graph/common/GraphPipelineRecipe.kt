package cn.jzl.graph.common

import cn.jzl.ecs.World
import cn.jzl.graph.common.data.GraphWithProperties

interface GraphPipelineRecipe {
    fun <PN : PipelineNode> buildGraphPipeline(
        world: World,
        graph: GraphWithProperties,
        endNodeId: String,
        inputFields: Array<String>,
        resolver: PipelineNodeProducerResolver<out PN>
    ): List<PN>
}