package cn.jzl.graph.common.rendering.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.DualInputCalculator
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.DualInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.RenderGraphType
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class DualInputRenderingPipelineNodeProducer(
    name: String,
    type: String,
) : DualInputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>(name, type) {
    override fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        check(this.first.required && first != null) { "first input is required" }
        check(this.second.required && second != null) { "second input is required" }
        val calculator by world.instance<DualInputCalculator>()
        return RenderingPipelineNode { blackboard ->
            val firstValue = blackboard[first]
            val secondValue = blackboard[second]
            check(firstValue != null && secondValue != null)
            blackboard[graphNode, output] = calculator.calculate(firstValue, secondValue, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(a: Float, b: Float): Float
}