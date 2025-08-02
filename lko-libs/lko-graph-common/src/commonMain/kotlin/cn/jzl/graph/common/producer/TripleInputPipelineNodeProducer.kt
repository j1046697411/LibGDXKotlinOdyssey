package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class TripleInputPipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>>(
    name: String,
    type: String
) : SingleOutputPipelineNodeProducer<PN, GT>(name, type) {

    protected abstract val first: NamedGraphNodeInput
    protected abstract val second: NamedGraphNodeInput
    protected abstract val third: NamedGraphNodeInput


    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN = createTripleInputNode(
        world,
        graph,
        configuration,
        graphType,
        graphNode,
        inputs.firstOrNull { it.input == first },
        inputs.firstOrNull { it.input == second },
        inputs.firstOrNull { it.input == third },
        output
    )

    protected abstract fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        third: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): PN
}