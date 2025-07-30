package cn.jzl.graph.common.producer.general.provided

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.producer.general.GeneralGraphType
import cn.jzl.graph.common.producer.general.GeneralPipelineNode

class Time : AbstractPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>("time", "time") {

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
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GeneralPipelineNode {
        val timeProvider by world.instance<TimeProvider>()
        return GeneralPipelineNode { blackboard ->
            blackboard[graphNode, time, PrimitiveFieldTypes.FloatFieldType] = timeProvider.time
            blackboard[graphNode, deltaTime, PrimitiveFieldTypes.FloatFieldType] = timeProvider.deltaTime
        }
    }
}