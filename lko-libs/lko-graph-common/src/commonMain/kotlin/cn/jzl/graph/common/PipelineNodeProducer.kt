package cn.jzl.graph.common

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.NodeConfiguration
import cn.jzl.graph.common.data.GraphWithProperties

interface PipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>> {

    val configuration: NodeConfiguration

    fun getOutputTypes(
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>
    ): Map<String, String>

    fun createNode(
        world: World,
        graph: GraphWithProperties,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): PN
}

