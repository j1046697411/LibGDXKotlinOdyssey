package cn.jzl.graph.render

import com.badlogic.gdx.utils.Disposable

interface PipelineRenderer : Disposable {
    fun render(renderOutput: RenderOutput)
}