package cn.jzl.graph.shader.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.DualInputPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.DefaultFieldOutput
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

abstract class DualInputShaderPipelineNodeProducer(
    name: String,
    type: String
) : DualInputPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>(name, type) {

    override val first: NamedGraphNodeInput = createNodeInput("a", "A", required = true)
    override val second = createNodeInput("b", "B", required = true)
    override val output = createNodeOutput("output", "output", required = true)

    override fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): GraphShaderPipelineNode {
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        check(this.first.required && first != null) { "First input is required" }
        check(this.second.required && second != null) { "Second input is required" }
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
            val firstShaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(first.outputType)
            val secondShaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(second.outputType)
            val outputFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(output.outputType)
            val firstFieldOutput = blackboard[first.fromGraphNode, first.fromOutput, firstShaderFieldType]
            val secondFieldOutput = blackboard[second.fromGraphNode, second.fromOutput, secondShaderFieldType]
            val representation = buildFragmentNodeDualInputs(commonShaderBuilder, firstFieldOutput, secondFieldOutput)
            val result = "name_${graphNode.id}"
            commonShaderBuilder.addMainLine("// ${configuration.type} node")
            commonShaderBuilder.addMainLine("${outputFieldType.fieldType} $result = $representation;")
            blackboard[graphNode, output.output, outputFieldType] = createFieldOutput(outputFieldType, result)
        }
    }

    protected open fun createFieldOutput(
        shaderFieldType: ShaderFieldType<out FieldOutput>,
        representation: String
    ): FieldOutput = DefaultFieldOutput(shaderFieldType, representation)

    protected abstract fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String
}