package cn.jzl.graph.shader.builder

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.DualInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType

abstract class DualInputShaderPipelineNodeBuilder(
    name: String,
    type: String,
    menuLocation: String
) : DualInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type, menuLocation) {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A", required = true)
    override val second: NamedGraphNodeInput = createNodeInput("b", "B", required = true)
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result", required = true)

    override fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.first.required && first != null) { "first input is required" }
        check(this.second.required && second != null) { "second input is required" }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { buildDualInputs(graphType, graphNode, blackboard, first, second, output) }
            } else {
                vertexShader { buildDualInputs(graphType, graphNode, blackboard, first, second, output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.buildDualInputs(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        output: PipelineNodeOutput
    ) {
        val shaderOutput by buildNodeDualInputs(graphType, blackboard, first, second).property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, shaderOutput)
    }


    protected abstract fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
    ): Operand<out VarType>
}

