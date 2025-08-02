package cn.jzl.graph.common.producer.general

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.DualInputCalculator
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.DualInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class DualInputGeneralPipelineNodeProducer(
    name: String,
    type: String,
) : DualInputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>(name, type) {
    override fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        check(this.first.required && first != null) { "first input is required" }
        check(this.second.required && second != null) { "second input is required" }
        val calculator by world.instance<DualInputCalculator>()
        return GeneralPipelineNode { blackboard ->
            val firstValue = blackboard[first]
            val secondValue = blackboard[second]
            check(firstValue != null && secondValue != null)
            blackboard[graphNode, output] = calculator.calculate(firstValue, secondValue, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(a: Float, b: Float): Float
}