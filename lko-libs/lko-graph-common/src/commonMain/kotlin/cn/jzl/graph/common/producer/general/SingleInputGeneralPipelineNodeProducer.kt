package cn.jzl.graph.common.producer.general

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.SingleInputCalculator
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleInputPipelineNodeProducer
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class SingleInputGeneralPipelineNodeProducer(
    name: String,
    type: String,
    menuLocation: String,
) : SingleInputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>(name, type, menuLocation) {

    override fun createSingleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        input: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        check(this.input.required && input != null) { "input is required" }
        val calculator by world.instance<SingleInputCalculator>()
        return GeneralPipelineNode { blackboard ->
            val value = blackboard[input]
            check(value != null) { "input value is null" }
            blackboard[graphNode, output] = calculator.calculate(value, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(value: Float): Float
}

