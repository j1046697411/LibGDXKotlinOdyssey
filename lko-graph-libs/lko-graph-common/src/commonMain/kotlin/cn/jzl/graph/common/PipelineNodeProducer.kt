package cn.jzl.graph.common

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.config.MenuNodeConfiguration
import cn.jzl.graph.common.data.GraphWithProperties

interface PipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>> {

    val configuration: MenuNodeConfiguration

    fun getOutputTypes(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>
    ): Map<String, String>

    fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): PN
}

