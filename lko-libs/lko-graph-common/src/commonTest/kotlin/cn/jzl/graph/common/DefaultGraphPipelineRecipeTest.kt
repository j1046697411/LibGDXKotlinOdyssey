package cn.jzl.graph.common;

import cn.jzl.di.instance
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.world
import cn.jzl.graph.GraphConnection
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.rendering.PipelinePlugin
import cn.jzl.graph.common.rendering.PipelineRegistry
import cn.jzl.graph.common.rendering.RenderGraphType
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.validator.GraphValidationResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DefaultGraphPipelineRecipeTest {

    private val world: World = world {
        module(pipelineModule())
        this bind singleton {
            object : PipelinePlugin {
                override fun setup(pipelineRegistry: PipelineRegistry) {
                    pipelineRegistry.registerGraphTypes(TestRenderGraphType("testType"))
                }
            }
        }

    }
    private val resolver: PipelineNodeProducerResolver by world.instance()
    private val graphPipelineRecipe: GraphPipelineRecipe by world.instance()

    class TestRenderGraphType(override val type: String) : RenderGraphType {
        override fun validate(graph: GraphWithProperties): GraphValidationResult {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun `buildGraphPipeline should throw exception when end node producer not found`() {
        // Arrange
        val graph = mockk<GraphWithProperties>()
        val endNodeId = "endNode"
        val nodes = listOf<GraphNode>()
        every { graph.nodes } returns nodes.asSequence()
        every { graph.connections } returns emptyList<GraphConnection>().asSequence()
        every { graph.type } returns "testType"

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            graphPipelineRecipe.buildGraphPipeline<PipelineNode>(world, graph, endNodeId, emptyArray())
        }
    }
}