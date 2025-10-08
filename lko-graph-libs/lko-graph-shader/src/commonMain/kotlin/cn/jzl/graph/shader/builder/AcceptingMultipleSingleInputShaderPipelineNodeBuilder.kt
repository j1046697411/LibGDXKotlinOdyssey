package cn.jzl.graph.shader.builder

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AcceptingMultipleSingleInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.output
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType

abstract class AcceptingMultipleSingleInputShaderPipelineNodeBuilder(
    name: String,
    type: String,
    menuLocation: String
) : AcceptingMultipleSingleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type, menuLocation) {
    override val inputs: NamedGraphNodeInput = createNodeInput("input", "Inputs", required = true, acceptingMultiple = true)
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result", required = true)
    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.inputs.required && this.inputs.acceptingMultiple && inputs.isNotEmpty()) {
            "inputs are required and accepting multiple, but inputs is empty"
        }
        return GraphShaderPipelineNode { blackboard, fragmentShader ->
            if (fragmentShader) {
                fragmentShader { buildSingleInputs(graphType, graphNode, blackboard, inputs.asSequence(), output) }
            } else {
                vertexShader { buildSingleInputs(graphType, graphNode, blackboard, inputs.asSequence(), output) }
            }
        }
    }

    private fun ProgramScope.ShaderScope.buildSingleInputs(
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>,
        output: PipelineNodeOutput
    ) {
        val shaderOutput by buildNodeSingleInputs(graphType, blackboard, inputs).property("node_${graphNode.id}")
        graphType.output(graphNode, blackboard, output, shaderOutput)
    }

    protected abstract fun ProgramScope.ShaderScope.buildNodeSingleInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>
    ): Operand<out VarType>
}


