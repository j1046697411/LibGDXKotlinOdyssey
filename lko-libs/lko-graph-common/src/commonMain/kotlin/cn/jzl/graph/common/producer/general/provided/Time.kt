package cn.jzl.graph.common.producer.general.provided

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.producer.general.GeneralGraphType
import cn.jzl.graph.common.producer.general.GeneralPipelineNode
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.DurationUnit

class Time : AbstractPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>("time", "time", "Provided/Time") {

    private val time = createNodeOutput(
        fieldId = "time",
        fieldName = "time",
        producedTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT
    )

    private val sinTime = createNodeOutput(
        fieldId = "sinTime",
        fieldName = "sinTime",
        producedTypes = PrimitiveFieldTypes.FIELD_TYPE_FLOAT
    )

    private val cosTime = createNodeOutput(
        fieldId = "cosTime",
        fieldName = "cosTime",
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
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GeneralPipelineNode {
        val timeProvider = configuration.timeProvider
        return GeneralPipelineNode { blackboard ->
            val time = timeProvider.time.toDouble(DurationUnit.SECONDS).toFloat()
            val deltaTime = timeProvider.deltaTime.toDouble(DurationUnit.SECONDS).toFloat()
            blackboard[graphNode, this.time, PrimitiveFieldTypes.FloatFieldType] = time
            blackboard[graphNode, this.deltaTime, PrimitiveFieldTypes.FloatFieldType] = deltaTime
            blackboard[graphNode, sinTime, PrimitiveFieldTypes.FloatFieldType] = sin(time)
            blackboard[graphNode, cosTime, PrimitiveFieldTypes.FloatFieldType] = cos(time)
        }
    }
}