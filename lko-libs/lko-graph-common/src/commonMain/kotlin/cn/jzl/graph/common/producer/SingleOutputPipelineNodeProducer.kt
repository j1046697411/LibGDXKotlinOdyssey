package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeOutput

abstract class SingleOutputPipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>>(
    name: String,
    type: String
) : AbstractPipelineNodeProducer<PN, GT>(name, type) {
    protected abstract val output: NamedGraphNodeOutput

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): PN = createSingleOutputNode(
        world = world,
        graph = graph,
        graphType = graphType,
        graphNode = graphNode,
        inputs = inputs,
        output = requireNotNull(outputs[output.fieldId])
    )

    protected abstract fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN
}