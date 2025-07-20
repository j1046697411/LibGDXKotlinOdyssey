package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeOutput

abstract class SingleOutputPipelineNodeProducer<PN : PipelineNode>(
    name: String,
    type: String
) : AbstractPipelineNodeProducer<PN>(name, type) {
    protected abstract val output: NamedGraphNodeOutput

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): PN = createSingleOutputNode(
        world = world,
        graph = graph,
        graphNode = graphNode,
        inputs = inputs,
        output = requireNotNull(outputs[output.fieldId])
    )

    protected abstract fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN
}