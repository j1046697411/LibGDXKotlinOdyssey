package cn.jzl.graph.shader.builder

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.AcceptingMultipleSingleInputPipelineNodeProducer
import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

abstract class AcceptingMultipleSingleInputShaderPipelineNodeProducer(
    name: String,
    type: String
) : AcceptingMultipleSingleInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type) {

    override val inputs = createNodeInput("input", "Input", required = true, acceptingMultiple = true)
    override val output = createNodeOutput("output", "Result", required = true)

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
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
            val outputFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(output.outputType)
            val representation = buildFragmentNodeSingleInputs(
                commonShaderBuilder,
                inputs.asSequence().map {
                    val shaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(it.outputType)
                    blackboard[it.fromGraphNode, it.fromOutput, shaderFieldType]
                }
            )
            val result = "name_${graphNode.id}"
            commonShaderBuilder.addMainLine("// ${this.configuration.type} node")
            commonShaderBuilder.addMainLine("${outputFieldType.fieldType} $result = $representation;")
            blackboard[graphNode, output.output, outputFieldType] = createFieldOutput(outputFieldType, result)
        }
    }

    protected open fun createFieldOutput(
        shaderFieldType: ShaderFieldType<out FieldOutput>,
        representation: String
    ): FieldOutput = DefaultFieldOutput(shaderFieldType, representation)

    protected abstract fun buildFragmentNodeSingleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        inputs: Sequence<FieldOutput>
    ): String
}

