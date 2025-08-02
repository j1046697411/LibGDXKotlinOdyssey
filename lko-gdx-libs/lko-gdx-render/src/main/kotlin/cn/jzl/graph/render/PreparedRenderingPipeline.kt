package cn.jzl.graph.render

import cn.jzl.graph.common.rendering.PipelineBlackboard
import com.badlogic.gdx.utils.Disposable

interface PreparedRenderingPipeline : Disposable {

    fun begin()

    fun execute(blackboard: PipelineBlackboard): RenderingPipeline

    fun end()
}