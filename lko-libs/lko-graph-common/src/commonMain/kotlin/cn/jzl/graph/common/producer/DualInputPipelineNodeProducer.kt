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

abstract class DualInputPipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>>(
    name: String,
    type: String,
    menuLocation: String,
) : SingleOutputPipelineNodeProducer<PN, GT>(name, type, menuLocation) {

    protected abstract val first: NamedGraphNodeInput
    protected abstract val second: NamedGraphNodeInput

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN = createDualInputNode(
        world,
        graph,
        configuration,
        graphType,
        graphNode,
        inputs.firstOrNull { it.input == first },
        inputs.firstOrNull { it.input == second },
        output
    )

    protected abstract fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput,
    ): PN
}