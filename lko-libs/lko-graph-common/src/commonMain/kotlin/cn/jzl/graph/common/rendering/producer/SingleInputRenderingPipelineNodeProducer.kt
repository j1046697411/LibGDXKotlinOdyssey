package cn.jzl.graph.common.rendering.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.calculator.SingleInputCalculator
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleInputPipelineNodeProducer
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.rendering.RenderGraphType
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.common.rendering.get
import cn.jzl.graph.common.rendering.set

abstract class SingleInputRenderingPipelineNodeProducer(
    name: String,
    type: String
) : SingleInputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>(name, type) {

    override fun createSingleInputNode(
        world: World,
        graph: GraphWithProperties,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        input: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        check(this.input.required && input != null) { "input is required" }
        val calculator by world.instance<SingleInputCalculator>()
        return RenderingPipelineNode { blackboard ->
            val value = blackboard[input]
            check(value != null) { "input value is null" }
            blackboard[graphNode, output] = calculator.calculate(value, ::executeFunction)
        }
    }

    protected abstract fun executeFunction(value: Float): Float
}

