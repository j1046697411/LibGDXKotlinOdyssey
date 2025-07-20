package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class TripleInputPipelineNodeProducer<PN : PipelineNode>(
    name: String,
    type: String
) : SingleOutputPipelineNodeProducer<PN>(name, type) {

    protected abstract val first: NamedGraphNodeInput
    protected abstract val second: NamedGraphNodeInput
    protected abstract val third: NamedGraphNodeInput

    final override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN = createTripleInputNode(
        world,
        graph,
        graphNode,
        inputs.single { it.input == first },
        inputs.single { it.input == second },
        inputs.single { it.input == third },
        output
    )

    protected abstract fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput,
        output: PipelineNodeOutput
    ): PN
}