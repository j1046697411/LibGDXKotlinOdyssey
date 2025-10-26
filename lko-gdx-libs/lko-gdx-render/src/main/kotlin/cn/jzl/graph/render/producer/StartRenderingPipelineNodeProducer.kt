package cn.jzl.graph.render.producer

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.producer.DualInputPipelineNodeProducer
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.render.RenderGraphType
import cn.jzl.graph.render.RenderingPipelineNode
import cn.jzl.graph.render.RenderingPipeline
import cn.jzl.graph.render.command
import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.render.field.RenderingPipelineType
import cn.jzl.graph.render.field.Vector2Type
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils

class StartRenderingPipelineNodeProducer :
    DualInputPipelineNodeProducer<RenderingPipelineNode, RenderGraphType>("Pipeline Start", "PipelineStart", "Render/StartRendering") {

    override val first = createNodeInput("background", "background", ColorType)
    override val second = createNodeInput("size", "size", Vector2Type)
    override val output = createNodeOutput("output", "RenderingPipeline", RenderingPipelineType)

    override fun createDualInputNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: RenderGraphType,
        graphNode: GraphNode,
        first: PipelineNodeInput?,
        second: PipelineNodeInput?,
        output: PipelineNodeOutput
    ): RenderingPipelineNode {
        val renderingPipeline by world.instance<RenderingPipeline>()
        return object : RenderingPipelineNode, Disposable {
            override fun executeNode(blackboard: PipelineBlackboard) {
                val background = first?.let { blackboard[it.fromGraphNode, it.fromOutput, ColorType] } ?: Color.BLACK
                val size = second?.let { blackboard[it.fromGraphNode, it.fromOutput, Vector2Type] }
                val width = size?.let { MathUtils.round(it.x) } ?: Gdx.graphics.width
                val height = size?.let { MathUtils.round(it.y) } ?: Gdx.graphics.height
                renderingPipeline.initializeDefaultBuffer(width, height, Pixmap.Format.RGB888)
                renderingPipeline.command { ScreenUtils.clear(background) }
                blackboard[graphNode, output.output, RenderingPipelineType] = renderingPipeline
            }

            override fun dispose() {
                renderingPipeline.dispose()
            }
        }
    }
}