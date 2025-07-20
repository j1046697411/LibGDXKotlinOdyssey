package cn.jzl.graph.common.rendering.producer.provided

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.rendering.RenderingPipelineNode

class Time : AbstractPipelineNodeProducer<RenderingPipelineNode>("time", "time") {

    private val time = createNodeOutput(
        fieldId = "time",
        fieldName = "time",
        producedTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT
    )

    private val deltaTime = createNodeOutput(
        fieldId = "deltaTime",
        fieldName = "deltaTime",
        producedTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT
    )

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): RenderingPipelineNode {
        val timeProvider by world.instance<TimeProvider>()
        return RenderingPipelineNode { blackboard ->
            blackboard[graphNode, time, PrimitiveFieldTypes.FloatFieldType] = timeProvider.time
            blackboard[graphNode, deltaTime, PrimitiveFieldTypes.FloatFieldType] = timeProvider.deltaTime
        }
    }
}