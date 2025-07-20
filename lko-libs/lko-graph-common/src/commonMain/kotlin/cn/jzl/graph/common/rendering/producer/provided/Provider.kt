package cn.jzl.graph.common.rendering.producer.provided

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.common.rendering.set

class Provider : SingleOutputPipelineNodeProducer<RenderingPipelineNode>("provider", "provider") {

    override val output = createNodeOutput(
        "output",
        "output",
        connectableFieldTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT.toList()
    )

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): RenderingPipelineNode = RenderingPipelineNode { blackboard ->
        blackboard[graphNode, output] = graphNode.payloads["provider"] ?: 0f
    }
}