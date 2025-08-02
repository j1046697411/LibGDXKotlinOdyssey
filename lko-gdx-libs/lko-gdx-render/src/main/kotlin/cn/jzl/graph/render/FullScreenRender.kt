package cn.jzl.graph.render

import com.badlogic.gdx.graphics.glutils.ShaderProgram

interface FullScreenRender {
    fun renderFullScreen(shaderProgram: ShaderProgram)
}