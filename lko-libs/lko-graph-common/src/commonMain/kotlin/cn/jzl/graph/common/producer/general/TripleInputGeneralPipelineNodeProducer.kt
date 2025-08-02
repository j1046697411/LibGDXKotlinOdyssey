package cn.jzl.graph.common.producer.general

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.TripleInputCalculator
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.TripleInputPipelineNodeProducer
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class TripleInputGeneralPipelineNodeProducer(
    name: String,
    type: String,
) : TripleInputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>(name, type) {

    override fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        third: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        check(this.first.required && first != null) { "first input is required" }
        check(this.second.required && second != null) { "second input is required" }
        check(this.third.required && third != null) { "third input is required" }
        val calculator by world.instance<TripleInputCalculator>()
        return GeneralPipelineNode { blackboard ->
            val firstValue = blackboard[first]
            val secondValue = blackboard[second]
            val thirdValue = blackboard[third]
            check(firstValue != null && secondValue != null && thirdValue != null)
            blackboard[graphNode, output] = calculator.calculate(firstValue, secondValue, thirdValue, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(first: Float, second: Float, third: Float): Float
}