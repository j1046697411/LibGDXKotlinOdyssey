package cn.jzl.graph.common.producer.general

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.DualInputCalculator
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AcceptingMultipleSingleInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class AcceptingMultipleSingleInputGeneralPipelineNodeProducer(
    name: String,
    type: String
) : AcceptingMultipleSingleInputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>(name, type) {
    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        check(this.inputs.required && this.inputs.acceptingMultiple && inputs.isNotEmpty()) {
            "${this.configuration.name} inputs are required and accepting multiple, but inputs is empty"
        }
        val calculator by world.instance<DualInputCalculator>()
        return GeneralPipelineNode { blackboard ->
            val outputValue = inputs.map {
                val result = blackboard[it]
                check(result != null)
                result
            }.reduce { acc, any -> calculator.calculate(acc, any, ::executeFunction) }
            blackboard[graphNode, output] = outputValue
        }
    }

    protected abstract fun executeFunction(a: Float, b: Float): Float
}