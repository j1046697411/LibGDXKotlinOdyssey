package cn.jzl.graph.common

import cn.jzl.ecs.World
import cn.jzl.graph.GraphConnection
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.FieldTypeResolver

class DefaultGraphPipelineRecipe(
    private val graphTypeResolver: GraphTypeResolver,
    private val fieldTypeResolver: FieldTypeResolver,
    private val pipelineNodeProducerResolver: PipelineNodeProducerResolver
) : GraphPipelineRecipe {

    override fun <PN : PipelineNode> buildGraphPipeline(
        world: World,
        configuration: GraphPipelineConfiguration,
        graph: GraphWithProperties,
        endNodeId: String,
        inputFields: Array<String>,
    ): List<PN> {
        val graphType = graphTypeResolver.resolve<PN>(graph.type)
        // 预先构建节点ID到生产者的映射
        val pipelineNodeProducers = graph.nodes.associate { node ->
            node.id to (node to pipelineNodeProducerResolver.resolve(graphType, node))
        }

        // 预先构建连接映射: [目标节点ID -> [目标字段ID -> 连接列表]]
        val connectionsByTarget = graph.connections
            .groupBy { it.nodeTo }
            .mapValues { (_, connections) -> connections.groupBy { it.fieldTo } }

        val preparedGraphNodes = mutableMapOf<String, PreparedGraphNode<PN>>()
        populatePreparedGraphNodes(
            graph = graph,
            graphNodeId = endNodeId,
            inputFields = inputFields,
            pipelineNodeProducers = pipelineNodeProducers,
            preparedGraphNodes = preparedGraphNodes,
            connectionsByTarget = connectionsByTarget
        )
        return createPipelineNodes(world, graph, configuration, graphType, preparedGraphNodes)
    }

    private fun <PN : PipelineNode> populatePreparedGraphNodes(
        graph: GraphWithProperties,
        graphNodeId: String,
        inputFields: Array<String>,
        pipelineNodeProducers: Map<String, Pair<GraphNode, PipelineNodeProducer<PN, GraphType<PN>>>>,
        preparedGraphNodes: MutableMap<String, PreparedGraphNode<PN>>,
        connectionsByTarget: Map<String, Map<String, List<GraphConnection>>>
    ) {
        // 检查节点是否已处理
        if (graphNodeId in preparedGraphNodes) return

        // 获取当前节点信息（含错误详情）
        val (graphNode, pipelineNodeProducer) = pipelineNodeProducers[graphNodeId]
            ?: throw IllegalArgumentException("No producer found for node $graphNodeId")

        val nodeConfiguration = pipelineNodeProducer.configuration
        val nodeInputs = if (inputFields.isNotEmpty()) {
            nodeConfiguration.nodeInputs.asSequence().filter { it.fieldId in inputFields }
        } else {
            nodeConfiguration.nodeInputs.asSequence()
        }
        // 处理输入连接（使用预计算的连接映射）
        val inputs = mutableListOf<PipelineNodeInput>()
        for (input in nodeInputs) {
            // 获取该输入字段的所有连接
            val connections = connectionsByTarget[graphNodeId]?.get(input.fieldId).orEmpty()
            for (connection in connections) {
                // 递归处理源节点
                populatePreparedGraphNodes(
                    graph = graph,
                    graphNodeId = connection.nodeFrom,
                    EMPTY_INPUT_FIELDS,
                    pipelineNodeProducers = pipelineNodeProducers,
                    preparedGraphNodes = preparedGraphNodes,
                    connectionsByTarget = connectionsByTarget
                )

                // 获取源节点输出（使用快速查找）
                val sourceNode = preparedGraphNodes[connection.nodeFrom]
                    ?: throw IllegalStateException("Source node ${connection.nodeFrom} not prepared")

                val sourceOutput = sourceNode.outputs[connection.fieldFrom]
                    ?: throw IllegalStateException("Output field ${connection.fieldFrom} not found in node ${connection.nodeFrom}")

                inputs += PipelineNodeInput(
                    sourceNode.graphNode,
                    sourceOutput.output,
                    input,
                    sourceOutput.outputType
                )
            }
        }
        // 计算输出类型
        val outputTypes = pipelineNodeProducer.getOutputTypes(graph, graphNode, inputs)
        // 构建输出列表及快速查找映射
        val outputs = outputTypes.mapValues { (fieldId, fieldType) ->
            val output = nodeConfiguration.nodeOutputs.first { it.fieldId == fieldId }
            val pipelineFieldType = fieldTypeResolver.resolve(fieldType) as FieldType<Any>
            PipelineNodeOutput(output, pipelineFieldType)
        }
        // 存储处理后的节点
        preparedGraphNodes[graphNodeId] = PreparedGraphNode(
            index = preparedGraphNodes.size,
            graphNode = graphNode,
            pipelineNodeProducer = pipelineNodeProducer,
            inputs = inputs,
            outputs = outputs,
        )
    }

    private fun <PN : PipelineNode> createPipelineNodes(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: GraphType<PN>,
        preparedGraphNodes: Map<String, PreparedGraphNode<PN>>
    ): List<PN> {
        return preparedGraphNodes.values.sortedBy { it.index }.map { node ->
            node.pipelineNodeProducer.createNode(
                world,
                graph,
                configuration,
                graphType,
                node.graphNode,
                node.inputs,
                node.outputs
            )
        }
    }

    data class PreparedGraphNode<PN : PipelineNode>(
        val index: Int,
        val graphNode: GraphNode,
        val pipelineNodeProducer: PipelineNodeProducer<PN, GraphType<PN>>,
        val inputs: List<PipelineNodeInput>,
        val outputs: Map<String, PipelineNodeOutput>,
    )

    internal companion object {
        val EMPTY_INPUT_FIELDS = arrayOf<String>()
    }
}