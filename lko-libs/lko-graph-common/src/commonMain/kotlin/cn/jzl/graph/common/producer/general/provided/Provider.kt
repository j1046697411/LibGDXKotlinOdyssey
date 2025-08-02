package cn.jzl.graph.common.producer.general.provided

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.common.producer.general.GeneralGraphType
import cn.jzl.graph.common.producer.general.GeneralPipelineNode
import cn.jzl.graph.common.rendering.set

class Provider : SingleOutputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>("provider", "provider") {

    override val output = createNodeOutput(
        "output",
        "output",
        connectableFieldTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT.toList()
    )

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GeneralPipelineNode = GeneralPipelineNode { blackboard ->
        blackboard[graphNode, output] = graphNode.payloads["provider"] ?: 0f
    }
}

