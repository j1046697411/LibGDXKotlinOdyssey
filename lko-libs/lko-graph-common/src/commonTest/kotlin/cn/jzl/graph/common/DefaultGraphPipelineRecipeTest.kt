package cn.jzl.graph.common;

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.world
import cn.jzl.graph.*
import cn.jzl.graph.common.data.DefaultGraphWithProperties
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.PipelineFieldTypeResolver
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.common.rendering.DefaultPipelineBlackboard
import cn.jzl.graph.common.rendering.GraphPipelineService
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.common.rendering.PipelinePlugin
import cn.jzl.graph.common.rendering.PipelineRegistry
import cn.jzl.graph.common.rendering.RenderingPipelineNode
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.impl.DefaultGraphConnection
import cn.jzl.graph.impl.DefaultGraphNode
import cn.jzl.graph.impl.DefaultGraphNodeOutput
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefaultGraphPipelineRecipeTest {

    private lateinit var pipelineFieldTypeResolver: PipelineFieldTypeResolver
    private lateinit var recipe: DefaultGraphPipelineRecipe
    private lateinit var world: World
    private lateinit var graph: GraphWithProperties
    private lateinit var resolver: PipelineNodeProducerResolver<PipelineNode>

    @BeforeEach
    fun setUp() {
        pipelineFieldTypeResolver = mockk()
        recipe = DefaultGraphPipelineRecipe(pipelineFieldTypeResolver)
        world = mockk()
        graph = mockk()
        resolver = mockk()
    }

    @Test
    fun `buildGraphPipeline should throw exception when end node producer not found`() {
        // Arrange
        val endNodeId = "endNode"
        val nodes = listOf<GraphNode>()
        every { graph.nodes } returns nodes.asSequence()
        every { graph.connections } returns emptyList<GraphConnection>().asSequence()

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            recipe.buildGraphPipeline(world, graph, endNodeId, emptyArray(), resolver)
        }
    }

    @Test
    fun `buildGraphPipeline should build simple linear pipeline`() {
        // Arrange
        val node1 = mockk<GraphNode> { every { id } returns "node1" }
        val node2 = mockk<GraphNode> { every { id } returns "node2" }
        val connection = mockk<GraphConnection> {
            every { nodeFrom } returns "node1"
            every { nodeTo } returns "node2"
            every { fieldFrom } returns "output1"
            every { fieldTo } returns "input1"
        }

        val producer1 = mockk<PipelineNodeProducer<PipelineNode>>()
        val producer2 = mockk<PipelineNodeProducer<PipelineNode>>()
        val pipelineNode1 = mockk<PipelineNode>()
        val pipelineNode2 = mockk<PipelineNode>()

        every { graph.nodes } returns listOf(node1, node2).asSequence()
        every { graph.connections } returns listOf(connection).asSequence()
        every { resolver.resolve(node1) } returns producer1
        every { resolver.resolve(node2) } returns producer2

        val config1 = mockk<NodeConfiguration>()
        val config2 = mockk<NodeConfiguration>()
        every { producer1.configuration } returns config1
        every { producer2.configuration } returns config2

        val output1 = mockk<GraphNodeOutput> { every { fieldId } returns "output1" }
        val input1 = mockk<GraphNodeInput> { every { fieldId } returns "input1" }
        every { config1.nodeOutputs } returns listOf(output1)
        every { config2.nodeInputs } returns listOf(input1)

        val fieldType = mockk<FieldType<out Any>>()
        every { pipelineFieldTypeResolver.resolve(any()) } returns fieldType
        every { producer2.getOutputTypes(any(), any(), any()) } returns mapOf("output2" to "type")

        every { producer1.createNode(any(), any(), any(), any(), any()) } returns pipelineNode1
        every { producer2.createNode(any(), any(), any(), any(), any()) } returns pipelineNode2

        // Act
        val result = recipe.buildGraphPipeline(world, graph, "node2", emptyArray(), resolver)

        // Assert
        assertEquals(2, result.size)
        assertEquals(pipelineNode1, result[0])
        assertEquals(pipelineNode2, result[1])
    }

    @Test
    fun `buildGraphPipeline should filter inputs when inputFields specified`() {
        // Arrange
        val node1 = mockk<GraphNode> { every { id } returns "node1" }
        val node2 = mockk<GraphNode> { every { id } returns "node2" }
        val connection1 = mockk<GraphConnection> {
            every { nodeFrom } returns "node1"
            every { nodeTo } returns "node2"
            every { fieldFrom } returns "output1"
            every { fieldTo } returns "input1"
        }
        val connection2 = mockk<GraphConnection> {
            every { nodeFrom } returns "node1"
            every { nodeTo } returns "node2"
            every { fieldFrom } returns "output2"
            every { fieldTo } returns "input2"
        }

        val producer1 = mockk<PipelineNodeProducer<PipelineNode>>()
        val producer2 = mockk<PipelineNodeProducer<PipelineNode>>()
        val pipelineNode1 = mockk<PipelineNode>()
        val pipelineNode2 = mockk<PipelineNode>()

        every { graph.nodes } returns listOf(node1, node2).asSequence()
        every { graph.connections } returns listOf(connection1, connection2).asSequence()
        every { resolver.resolve(node1) } returns producer1
        every { resolver.resolve(node2) } returns producer2

        val config1 = mockk<NodeConfiguration>()
        val config2 = mockk<NodeConfiguration>()
        every { producer1.configuration } returns config1
        every { producer2.configuration } returns config2

        val output1 = mockk<GraphNodeOutput> { every { fieldId } returns "output1" }
        val output2 = mockk<GraphNodeOutput> { every { fieldId } returns "output2" }
        val input1 = mockk<GraphNodeInput> { every { fieldId } returns "input1" }
        val input2 = mockk<GraphNodeInput> { every { fieldId } returns "input2" }
        every { config1.nodeOutputs } returns listOf(output1, output2)
        every { config2.nodeInputs } returns listOf(input1, input2)

        val fieldType = mockk<FieldType<out Any>>()
        every { pipelineFieldTypeResolver.resolve(any()) } returns fieldType
        every { producer2.getOutputTypes(any(), any(), any()) } returns mapOf("output2" to "type")

        every { producer1.createNode(any(), any(), any(), any(), any()) } returns pipelineNode1
        every { producer2.createNode(any(), any(), any(), any(), any()) } returns pipelineNode2

        // Act
        val result = recipe.buildGraphPipeline(world, graph, "node2", arrayOf("input1"), resolver)

        // Assert
        // Verify that only input1 was processed (input2 should be filtered out)
        verify {
            producer2.getOutputTypes(any(), any(), withArg {
                assertEquals(1, it.size)
                assertEquals("input1", it[0].input.fieldId)
            })
        }
    }

    @Test
    fun `buildGraphPipeline should handle diamond-shaped dependency graph`() {
        // Arrange
        val nodeA = mockk<GraphNode> { every { id } returns "A" }
        val nodeB = mockk<GraphNode> { every { id } returns "B" }
        val nodeC = mockk<GraphNode> { every { id } returns "C" }
        val nodeD = mockk<GraphNode> { every { id } returns "D" }

        val connectionAB = mockk<GraphConnection> {
            every { nodeFrom } returns "A"
            every { nodeTo } returns "B"
            every { fieldFrom } returns "out1"
            every { fieldTo } returns "in1"
        }
        val connectionAC = mockk<GraphConnection> {
            every { nodeFrom } returns "A"
            every { nodeTo } returns "C"
            every { fieldFrom } returns "out1"
            every { fieldTo } returns "in1"
        }
        val connectionBD = mockk<GraphConnection> {
            every { nodeFrom } returns "B"
            every { nodeTo } returns "D"
            every { fieldFrom } returns "out1"
            every { fieldTo } returns "in1"
        }
        val connectionCD = mockk<GraphConnection> {
            every { nodeFrom } returns "C"
            every { nodeTo } returns "D"
            every { fieldFrom } returns "out1"
            every { fieldTo } returns "in2"
        }

        val producerA = mockk<PipelineNodeProducer<PipelineNode>>()
        val producerB = mockk<PipelineNodeProducer<PipelineNode>>()
        val producerC = mockk<PipelineNodeProducer<PipelineNode>>()
        val producerD = mockk<PipelineNodeProducer<PipelineNode>>()
        val pipelineNodeA = mockk<PipelineNode>()
        val pipelineNodeB = mockk<PipelineNode>()
        val pipelineNodeC = mockk<PipelineNode>()
        val pipelineNodeD = mockk<PipelineNode>()

        every { graph.nodes } returns listOf(nodeA, nodeB, nodeC, nodeD).asSequence()
        every { graph.connections } returns listOf(connectionAB, connectionAC, connectionBD, connectionCD).asSequence()
        every { resolver.resolve(nodeA) } returns producerA
        every { resolver.resolve(nodeB) } returns producerB
        every { resolver.resolve(nodeC) } returns producerC
        every { resolver.resolve(nodeD) } returns producerD

        val configA = mockk<NodeConfiguration>()
        val configB = mockk<NodeConfiguration>()
        val configC = mockk<NodeConfiguration>()
        val configD = mockk<NodeConfiguration>()
        every { producerA.configuration } returns configA
        every { producerB.configuration } returns configB
        every { producerC.configuration } returns configC
        every { producerD.configuration } returns configD

        val outputA = mockk<GraphNodeOutput> { every { fieldId } returns "out1" }
        val outputB = mockk<GraphNodeOutput> { every { fieldId } returns "out1" }
        val outputC = mockk<GraphNodeOutput> { every { fieldId } returns "out1" }
        val inputB = mockk<GraphNodeInput> { every { fieldId } returns "in1" }
        val inputC = mockk<GraphNodeInput> { every { fieldId } returns "in1" }
        val inputD1 = mockk<GraphNodeInput> { every { fieldId } returns "in1" }
        val inputD2 = mockk<GraphNodeInput> { every { fieldId } returns "in2" }
        every { configA.nodeOutputs } returns listOf(outputA)
        every { configB.nodeOutputs } returns listOf(outputB)
        every { configC.nodeOutputs } returns listOf(outputC)
        every { configB.nodeInputs } returns listOf(inputB)
        every { configC.nodeInputs } returns listOf(inputC)
        every { configD.nodeInputs } returns listOf(inputD1, inputD2)

        val fieldType = mockk<FieldType<out Any>>()
        every { pipelineFieldTypeResolver.resolve(any()) } returns fieldType
        every { producerD.getOutputTypes(any(), any(), any()) } returns emptyMap()

        every { producerA.createNode(any(), any(), any(), any(), any()) } returns pipelineNodeA
        every { producerB.createNode(any(), any(), any(), any(), any()) } returns pipelineNodeB
        every { producerC.createNode(any(), any(), any(), any(), any()) } returns pipelineNodeC
        every { producerD.createNode(any(), any(), any(), any(), any()) } returns pipelineNodeD

        // Act
        val result = recipe.buildGraphPipeline(world, graph, "D", emptyArray(), resolver)

        // Assert
        assertEquals(4, result.size)
        // Verify correct order (A should be first, D last)
        assertEquals(pipelineNodeA, result[0])
        assertTrue(result.indexOf(pipelineNodeB) in 1..2)
        assertTrue(result.indexOf(pipelineNodeC) in 1..2)
        assertEquals(pipelineNodeD, result[3])
    }

    @Test
    fun calculateTwoMultiplyThreePlusFourDivideTwoPlusFive() {
        // 创建世界并注册模块
        val world = world {
            module(pipelineModule())
            this bind singleton { new(::TestPipelinePlugin) }
        }
        val pipelineService = GraphPipelineService(world)

        // 创建计算图
        val graph = DefaultGraphWithProperties("expressionTest")

        // 创建常量节点 (使用 provider 类型和 payloads)
        val const5 = DefaultGraphNode("const5", "provider", hashMapOf("provider" to 5f))
        val const6 = DefaultGraphNode("const6", "provider", hashMapOf("provider" to 6f))
        val const15 = DefaultGraphNode("const15", "provider", hashMapOf("provider" to 15f))
        val const3 = DefaultGraphNode("const3", "provider", hashMapOf("provider" to 3f))

        // 创建运算节点
        val addNode = DefaultGraphNode("add", "plus")
        val multiplyNode = DefaultGraphNode("multiply", "times")
        val divideNode = DefaultGraphNode("divide", "div")
        val subtractNode = DefaultGraphNode("subtract", "minus")
        val result = DefaultGraphNode("result", "Result")

        // 添加节点到图中
        graph.addGraphNode(const5)
        graph.addGraphNode(const6)
        graph.addGraphNode(const3)
        graph.addGraphNode(const15)
        graph.addGraphNode(addNode)
        graph.addGraphNode(multiplyNode)
        graph.addGraphNode(divideNode)
        graph.addGraphNode(subtractNode)
        graph.addGraphNode(result)

        // 创建连接
        // 5 + 6
        graph.addGraphConnection(DefaultGraphConnection("const5", "output", "add", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("const6", "output", "add", "inputs"))

        // (5 + 6) * 15
        graph.addGraphConnection(DefaultGraphConnection("add", "output", "multiply", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("const15", "output", "multiply", "inputs"))

        // 15 / 3
        graph.addGraphConnection(DefaultGraphConnection("const15", "output", "divide", "a"))
        graph.addGraphConnection(DefaultGraphConnection("const3", "output", "divide", "b"))

        // (5 + 6) * 15 - 15 / 3
        graph.addGraphConnection(DefaultGraphConnection("multiply", "output", "subtract", "a"))
        graph.addGraphConnection(DefaultGraphConnection("divide", "output", "subtract", "b"))

        graph.addGraphConnection(DefaultGraphConnection("subtract", "output", "result", "input"))

        // 构建并执行管道
         val pipelineNodes = pipelineService.buildGraphPipeline(graph, "result")
        val resultRenderingPipelineNode = pipelineNodes.filterIsInstance<ResultRenderingPipelineNode>().last()

        // 创建黑板并执行计算
        val blackboard = DefaultPipelineBlackboard()

        // 执行管道中的所有节点
        pipelineNodes.forEach { it.executeNode(blackboard) }
        val value = resultRenderingPipelineNode.getResult(blackboard)

        // 验证结果: (5 + 6) * 15 - 15 / 3 = 11 * 15 - 5 = 165 - 5 = 160
        assertEquals(160f, value)
    }

    @Test
    fun testCalculateComplexExpressionWithMultiplyAddDivide() {
        // 创建世界并注册模块
        val world = world {
            module(pipelineModule())
            this bind singleton { new(::TestPipelinePlugin) }
        }
        val pipelineService = GraphPipelineService(world)

        // 创建计算图
        val graph = DefaultGraphWithProperties("expressionTest2")

        // 创建常量节点
        val const2 = DefaultGraphNode("const2", "provider", hashMapOf("provider" to 2f))
        val const3 = DefaultGraphNode("const3", "provider", hashMapOf("provider" to 3f))
        val const4 = DefaultGraphNode("const4", "provider", hashMapOf("provider" to 4f))
        val const5 = DefaultGraphNode("const5", "provider", hashMapOf("provider" to 5f))

        // 创建运算节点
        val addNode = DefaultGraphNode("add", "plus")
        val multiplyNode1 = DefaultGraphNode("multiply1", "times")
        val divideNode = DefaultGraphNode("divide", "div")
        val multiplyNode2 = DefaultGraphNode("multiply2", "times")
        val addFinalNode = DefaultGraphNode("addFinal", "plus")
        val result = DefaultGraphNode("result", "Result")

        // 添加节点到图中
        graph.addGraphNode(const2)
        graph.addGraphNode(const3)
        graph.addGraphNode(const4)
        graph.addGraphNode(const5)
        graph.addGraphNode(addNode)
        graph.addGraphNode(multiplyNode1)
        graph.addGraphNode(divideNode)
        graph.addGraphNode(multiplyNode2)
        graph.addGraphNode(addFinalNode)
        graph.addGraphNode(result)

        // 创建连接
        // 3 + 4
        graph.addGraphConnection(DefaultGraphConnection("const3", "output", "add", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("const4", "output", "add", "inputs"))

        // 2 * (3 + 4)
        graph.addGraphConnection(DefaultGraphConnection("const2", "output", "multiply1", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("add", "output", "multiply1", "inputs"))

        // (2 * (3 + 4)) / 2
        graph.addGraphConnection(DefaultGraphConnection("multiply1", "output", "divide", "a"))
        graph.addGraphConnection(DefaultGraphConnection("const2", "output", "divide", "b"))

        // ((2 * (3 + 4)) / 2) + 5
        graph.addGraphConnection(DefaultGraphConnection("divide", "output", "addFinal", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("const5", "output", "addFinal", "inputs"))

        graph.addGraphConnection(DefaultGraphConnection("addFinal", "output", "result", "input"))
        // 构建并执行管道
        val pipelineNodes = pipelineService.buildGraphPipeline(graph, "result")
        val resultRenderingPipelineNode = pipelineNodes.filterIsInstance<ResultRenderingPipelineNode>().last()

        // 创建黑板并执行计算
        val blackboard = DefaultPipelineBlackboard()

        // 执行管道中的所有节点
        pipelineNodes.forEach { it.executeNode(blackboard) }
        val value = resultRenderingPipelineNode.getResult(blackboard)

        // 验证结果: 2 * (3 + 4) / 2 + 5 = 2 * 7 / 2 + 5 = 14 / 2 + 5 = 7 + 5 = 12
        assertEquals(12f, value)
    }

    class TestPipelinePlugin : PipelinePlugin {
        override fun setup(pipelineRegistry: PipelineRegistry) {
            pipelineRegistry.register(ResultRenderingPipelineNodeProducer())
        }
    }

    interface ResultRenderingPipelineNode : RenderingPipelineNode {
        fun getResult(blackboard: PipelineBlackboard) : Float
    }

    private class ResultRenderingPipelineNodeProducer : AbstractPipelineNodeProducer<ResultRenderingPipelineNode>("Result", "Result") {
        private val input = createNodeInput("input", "input")
        override fun createNode(
            world: World,
            graph: GraphWithProperties,
            graphNode: GraphNode,
            inputs: List<PipelineNodeInput>,
            outputs: Map<String, PipelineNodeOutput>
        ): ResultRenderingPipelineNode {
            val input = inputs.single { it.input == input }
            return object : ResultRenderingPipelineNode {
                override fun getResult(blackboard: PipelineBlackboard): Float {
                    return blackboard[input.fromGraphNode, input.fromOutput, PrimitiveFieldTypes.FloatFieldType]
                }

                override fun executeNode(blackboard: PipelineBlackboard) {
                }
            }
        }
    }
}