package cn.jzl.graph.common.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.impl.NamedGraphNodeOutput

abstract class SingleOutputPipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>>(
    name: String,
    type: String,
    menuLocation: String,
) : AbstractPipelineNodeProducer<PN, GT>(name, type, menuLocation) {
    protected abstract val output: NamedGraphNodeOutput

    final override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): PN {
        return createSingleOutputNode(
            world = world,
            graph = graph,
            configuration = configuration,
            graphType = graphType,
            graphNode = graphNode,
            inputs = inputs,
            output = requireNotNull(outputs[output.fieldId])
        )
    }

    protected abstract fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GT,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): PN
}