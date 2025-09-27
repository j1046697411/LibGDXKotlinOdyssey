package cn.jzl.graph.common.producer.general.provided

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldTypeValidator
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.common.producer.general.GeneralGraphType
import cn.jzl.graph.common.producer.general.GeneralPipelineNode

class Constant : SingleOutputPipelineNodeProducer<GeneralPipelineNode, GeneralGraphType>("constant", "constant", "Provided/Constant") {

    override val output = createNodeOutput("output", "output")

    override fun getOutputTypes(world: World, graph: GraphWithProperties, graphNode: GraphNode, inputs: List<PipelineNodeInput>): Map<String, String> {
        val fieldTypeValidator by world.instance<FieldTypeValidator>()
        val constant = graphNode.payloads.getValue(KEY_CONSTANT)
        return mapOf(output.fieldId to fieldTypeValidator.validate(constant).fieldType)
    }

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GeneralGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): GeneralPipelineNode {
        val constant = graphNode.payloads.getValue(KEY_CONSTANT)
        return GeneralPipelineNode { blackboard -> blackboard[graphNode, output.output, output.outputType] = constant }
    }

    companion object {
        const val KEY_CONSTANT = "constant"
    }
}

