package cn.jzl.graph.common.rendering

import cn.jzl.di.TagAll
import cn.jzl.di.allInstance
import cn.jzl.di.instance
import cn.jzl.ecs.System
import cn.jzl.ecs.World
import cn.jzl.graph.common.DefaultGraphPipelineRecipe
import cn.jzl.graph.common.GraphPipelineRecipe
import cn.jzl.graph.common.data.GraphWithProperties

class GraphPipelineService(world: World) : System(world) {

    private val graphPipelineRecipe by world.instance<GraphPipelineRecipe>()

    init {
        val pipelineRegistry by world.instance<PipelineRegistry>(TAG_PIPELINE_REGISTRY)
        val pipelinePlugins by world.allInstance<PipelinePlugin>(TagAll)
        pipelinePlugins.forEach { plugin -> plugin.setup(world, pipelineRegistry) }
    }

    fun buildGraphPipeline(
        graph: GraphWithProperties,
        endNodeId: String,
        inputFields: Array<String> = DefaultGraphPipelineRecipe.EMPTY_INPUT_FIELDS
    ): List<RenderingPipelineNode> {
        return graphPipelineRecipe.buildGraphPipeline(world, graph, endNodeId, inputFields)
    }
}

