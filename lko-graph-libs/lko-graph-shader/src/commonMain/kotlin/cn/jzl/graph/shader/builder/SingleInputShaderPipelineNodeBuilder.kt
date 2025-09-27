package cn.jzl.graph.shader.builder

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.shader.field.GraphShaderPipelineNode
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.field.ShaderGraphType
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType

abstract class SingleInputShaderPipelineNodeBuilder(
    name: String,
    type: String,
    menuLocation: String
) : SingleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type, menuLocation) {
    override val input: NamedGraphNodeInput = createNodeInput("input", "Input", required = true)
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result", required = true)

    override fun createSingleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        input: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.input.required && input != null) {
            "input is required"
        }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { buildSingleInput(graphType, graphNode, blackboard, input, output) }
            } else {
                vertexShader { buildSingleInput(graphType, graphNode, blackboard, input, output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.buildSingleInput(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput,
        output: PipelineNodeOutput
    ) {
        val shaderOutput by buildNodeSingleInput(graphType, blackboard, input).property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, shaderOutput)
    }

    protected abstract fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType>
}

