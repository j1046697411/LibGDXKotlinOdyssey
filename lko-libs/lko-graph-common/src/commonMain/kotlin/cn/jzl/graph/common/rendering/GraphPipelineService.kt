package cn.jzl.graph.common.rendering

import cn.jzl.di.allInstance
import cn.jzl.di.instance
import cn.jzl.ecs.System
import cn.jzl.ecs.World
import cn.jzl.graph.common.DefaultGraphPipelineRecipe
import cn.jzl.graph.common.data.GraphWithProperties

class GraphPipelineService(world: World) : System(world) {

    private val pipelineRegistry by world.instance<DefaultPipelineRegistry>()
    private val graphPipelineRecipe by lazy {
        DefaultGraphPipelineRecipe(pipelineRegistry)
    }

    init {
        val pipelinePlugins by world.allInstance<PipelinePlugin>()
        pipelinePlugins.forEach { plugin -> plugin.setup(pipelineRegistry) }
    }

    fun buildGraphPipeline(
        graph: GraphWithProperties,
        endNodeId: String,
        inputFields: Array<String> = DefaultGraphPipelineRecipe.EMPTY_INPUT_FIELDS
    ): List<RenderingPipelineNode> {
        return graphPipelineRecipe.buildGraphPipeline(world, graph, endNodeId, inputFields, pipelineRegistry)
    }
}

