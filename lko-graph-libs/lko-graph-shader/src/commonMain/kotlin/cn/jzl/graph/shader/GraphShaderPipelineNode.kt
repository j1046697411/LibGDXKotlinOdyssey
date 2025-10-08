package cn.jzl.graph.shader

import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.core.GraphProgramScope

fun interface GraphShaderPipelineNode : PipelineNode {
    fun GraphProgramScope.build(blackboard: PipelineBlackboard, fragmentShader: Boolean)
}