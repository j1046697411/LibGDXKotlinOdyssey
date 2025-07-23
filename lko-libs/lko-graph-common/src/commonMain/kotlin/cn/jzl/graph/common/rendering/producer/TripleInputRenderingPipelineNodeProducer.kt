package cn.jzl.graph.common.rendering.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.TripleInputCalculator
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.TripleInputPipelineNodeProducer
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.rendering.RenderGraphType
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class TripleInputRenderingPipelineNodeProducer(
    name: String,
    type: String,
) : TripleInputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>(name, type) {

    override fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        val calculator by world.instance<TripleInputCalculator>()
        return RenderingPipelineNode { blackboard ->
            val firstValue = blackboard[first]
            val secondValue = blackboard[second]
            val thirdValue = blackboard[third]
            check(firstValue != null && secondValue != null && thirdValue != null)
            blackboard[graphNode, output] = calculator.calculate(firstValue, secondValue, thirdValue, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(first: Float, second: Float, third: Float): Float
}