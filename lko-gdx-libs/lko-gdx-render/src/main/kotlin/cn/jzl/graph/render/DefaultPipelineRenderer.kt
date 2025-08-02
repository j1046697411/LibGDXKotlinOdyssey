package cn.jzl.graph.render

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.common.rendering.PipelineBlackboard

class DefaultPipelineRenderer(
    world: World,
    private val preparedRenderingPipeline: PreparedRenderingPipeline
) : PipelineRenderer {

    private val blackboard by world.instance<PipelineBlackboard>()
    private val openGLContext by world.instance<OpenGLContext>()
    private val fullScreenRender by world.instance<FullScreenRender>()

    override fun render(renderOutput: RenderOutput) {
        preparedRenderingPipeline.begin()
        val renderingPipeline = preparedRenderingPipeline.execute(blackboard)
        openGLContext.begin()
        renderOutput.output(openGLContext, fullScreenRender, renderingPipeline)
        openGLContext.end()
        preparedRenderingPipeline.end()
    }

    override fun dispose() {
        preparedRenderingPipeline.dispose()
    }
}