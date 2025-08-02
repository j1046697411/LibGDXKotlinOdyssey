package cn.jzl.graph.shader.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleInputPipelineNodeProducer
import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

abstract class SingleInputShaderPipelineNodeProducer(
    name: String,
    type: String
) : SingleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type) {

    override val input = createNodeInput("input", "Input", required = true)
    override val output = createNodeOutput("output", "Output", required = true)
    override fun createSingleInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        input: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        check(this.input.required && input != null) {}
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
            val inputFieldOutputType = shaderFieldTypeResolver.resolve<FieldOutput>(input.outputType)
            val fieldOutputType = shaderFieldTypeResolver.resolve<FieldOutput>(output.outputType)
            val inputFieldOutput = blackboard[input.fromGraphNode, input.fromOutput, inputFieldOutputType]
            val outputFieldOutput = blackboard[graphNode, output.output, fieldOutputType]
            val representation = buildFragmentNodeSingleInput(
                commonShaderBuilder,
                inputFieldOutput,
                outputFieldOutput
            )
            val result = "name_${graphNode.id}"
            commonShaderBuilder.addMainLine("// ${this.configuration.type} node")
            commonShaderBuilder.addMainLine("${fieldOutputType.fieldType} $result = $representation;")
            blackboard[graphNode, output.output, fieldOutputType] = createFieldOutput(fieldOutputType, result)
        }
    }

    protected open fun createFieldOutput(
        shaderFieldType: ShaderFieldType<out FieldOutput>,
        representation: String
    ): FieldOutput = DefaultFieldOutput(shaderFieldType, representation)

    protected abstract fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String
}