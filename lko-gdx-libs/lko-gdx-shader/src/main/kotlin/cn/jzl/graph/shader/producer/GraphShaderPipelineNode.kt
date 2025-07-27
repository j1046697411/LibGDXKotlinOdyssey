package cn.jzl.graph.shader.producer

import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.core.FragmentShaderBuilder
import cn.jzl.graph.shader.core.VertexShaderBuilder

fun interface GraphShaderPipelineNode : PipelineNode {
    fun buildGraphShader(
        blackboard: PipelineBlackboard,
        vertexShaderBuilder: VertexShaderBuilder,
        fragmentShaderBuilder: FragmentShaderBuilder,
        fragmentShader: Boolean
    )
}

