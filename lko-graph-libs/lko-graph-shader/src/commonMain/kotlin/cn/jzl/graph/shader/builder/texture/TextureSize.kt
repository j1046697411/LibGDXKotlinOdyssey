package cn.jzl.graph.shader.builder.texture

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.builder.DualInputShaderPipelineNodeBuilder
import cn.jzl.graph.shader.field.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType
import cn.jzl.shader.textureSize

object TextureSize : DualInputShaderPipelineNodeBuilder("TextureSize", "textureSize", "Texture/TextureSize") {
    override val first = createNodeInput("texture", "Texture", required = true)
    override val second = createNodeInput("uv", "UV", required = true)

    override val output = createNodeOutput("size", "Size")

    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> = textureSize(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second)
    )
}