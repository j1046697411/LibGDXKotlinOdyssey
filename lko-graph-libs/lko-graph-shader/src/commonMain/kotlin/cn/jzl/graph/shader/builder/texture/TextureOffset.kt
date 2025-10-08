package cn.jzl.graph.shader.builder.texture

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.builder.TripleInputShaderPipelineNodeBuilder
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType
import cn.jzl.shader.textureOffset

object TextureOffset : TripleInputShaderPipelineNodeBuilder("TextureOffset", "textureOffset", "Texture/TextureOffset") {
    override val first = createNodeInput("texture", "Texture", required = true)
    override val second = createNodeInput("uv", "UV", required = true)
    override val third = createNodeInput("offset", "Offset", required = true)

    override val output = createNodeOutput("offset", "Offset")

    override fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType> = textureOffset(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second),
        graphType.operand(blackboard, third)
    )
}