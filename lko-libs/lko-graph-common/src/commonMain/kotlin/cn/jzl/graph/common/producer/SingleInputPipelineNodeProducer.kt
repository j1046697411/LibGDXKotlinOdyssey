package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class SingleInputPipelineNodeProducer<PN : PipelineNode>(
    name: String,
    type: String
) : SingleOutputPipelineNodeProducer<PN>(name, type) {

    protected abstract val input: NamedGraphNodeInput

    final override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN = createSingleInputNode(
        world,
        graph,
        graphNode,
        inputs.single { it.input == input },
        output
    )

    protected abstract fun createSingleInputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        input: PipelineNodeInput,
        output: PipelineNodeOutput
    ): PN
}