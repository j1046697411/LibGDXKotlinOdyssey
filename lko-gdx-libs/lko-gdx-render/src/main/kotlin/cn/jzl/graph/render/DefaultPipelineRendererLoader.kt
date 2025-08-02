package cn.jzl.graph.render

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.general.GeneralPipelineNode
import cn.jzl.graph.common.rendering.GraphPipelineService

internal class DefaultPipelineRendererLoader(private val world: World) : PipelineRendererLoader {

    private val graphPipelineService by world.instance<GraphPipelineService>()

    override fun loadPipelineRenderer(
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        endNodeId: String
    ): PipelineRenderer {
        val pipelineNodes = graphPipelineService.buildGraphPipeline<GeneralPipelineNode>(graph, configuration, endNodeId)
        val preparedRenderingPipeline = GraphPreparedRenderingPipeline(pipelineNodes.asSequence())
        return DefaultPipelineRenderer(world, preparedRenderingPipeline)
    }
}