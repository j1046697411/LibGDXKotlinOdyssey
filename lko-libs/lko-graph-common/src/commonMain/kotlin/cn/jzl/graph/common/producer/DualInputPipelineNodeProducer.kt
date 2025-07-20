package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class DualInputPipelineNodeProducer<PN : PipelineNode>(
    name: String,
    type: String
) : SingleOutputPipelineNodeProducer<PN>(name, type) {
    protected abstract val first: NamedGraphNodeInput
    protected abstract val second: NamedGraphNodeInput

    final override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN = createDualInputNode(
        world,
        graph,
        graphNode,
        inputs.single { it.input == first },
        inputs.single { it.input == second },
        output
    )

    protected abstract fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        output: PipelineNodeOutput,
    ): PN
}