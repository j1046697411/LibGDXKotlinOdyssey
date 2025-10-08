package cn.jzl.graph.shader.builder

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.TripleInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType

abstract class TripleInputShaderPipelineNodeBuilder(
    name: String,
    type: String,
    menuLocation: String
) : TripleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type, menuLocation) {

    override val first = createNodeInput("a", "A", required = true)
    override val second = createNodeInput("b", "B", required = true)
    override val third = createNodeInput("c", "C", required = true)
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result", required = true)

    override fun createTripleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        third: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.first.required && first != null) { "first is required" }
        check(this.second.required && second != null) { "second is required" }
        check(this.third.required && third != null) { "third is required" }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { buildTripleInput(graphType, graphNode, blackboard, first, second, third, output) }
            } else {
                vertexShader { buildTripleInput(graphType, graphNode, blackboard, first, second, third, output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.buildTripleInput(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput,
        output: PipelineNodeOutput
    ) {
        val shaderOutput by buildNodeTripleInput(graphType, blackboard, first, second, third).property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, shaderOutput)
    }

    protected abstract fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType>
}