package cn.jzl.graph.render.producer

import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeInputSide
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.SingleOutputPipelineNodeProducer
import cn.jzl.graph.render.RenderGraphType
import cn.jzl.graph.render.RenderingPipelineNode
import cn.jzl.graph.render.field.RenderingPipelineType
import cn.jzl.graph.render.field.Vector2Type
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import ktx.log.logger

class PipelineRendererNodeProducer :
    SingleOutputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>("Pipeline Renderer", "PipelineRenderer") {

    private val first = createNodeInput(
        fieldId = "pipeline",
        fieldName = "pipeline",
        required = true,
        side = GraphNodeInputSide.Top,
        fieldTypes = arrayOf(RenderingPipelineType)
    )
    private val second = createNodeInput(
        fieldId = "input",
        fieldName = "input",
        required = true,
        fieldTypes = arrayOf(RenderingPipelineType)
    )
    private val third = createNodeInput("size", "size", Vector2Type)
    private val fourth = createNodeInput("position", "position", Vector2Type)

    override val output = createNodeOutput("output", "RenderingPipeline", RenderingPipelineType)

    override fun createSingleOutputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        val pipeline = inputs.firstOrNull { it.input == first }
        requireNotNull(pipeline) { "RenderingPipeline is null" }
        val input = inputs.firstOrNull { it.input == second }
        requireNotNull(input) { "RenderingPipeline is null" }
        val size = inputs.firstOrNull { it.input == third }
        val position = inputs.firstOrNull { it.input == fourth }
        return RenderingPipelineNode { blackboard ->
            val renderingPipeline = blackboard[pipeline.fromGraphNode, pipeline.fromOutput, RenderingPipelineType]
            val input = blackboard[input.fromGraphNode, input.fromOutput, RenderingPipelineType]
            val size = size?.let { blackboard[it.fromGraphNode, it.fromOutput, Vector2Type] }
            val position = position?.let { blackboard[it.fromGraphNode, it.fromOutput, Vector2Type] } ?: Vector2.Zero
            val width = size?.x ?: Gdx.graphics.width.toFloat()
            val height = size?.y ?: Gdx.graphics.height.toFloat()
            renderingPipeline.drawTexture(
                input.executeCommands(),
                renderingPipeline.executeCommands(),
                position.x,
                position.y,
                width,
                height
            )
            log.debug { "draw texture $position $width, $height" }
            blackboard[graphNode, output.output, RenderingPipelineType] = renderingPipeline
        }
    }
    companion object {
        val log = logger<PipelineRendererNodeProducer>()
    }
}