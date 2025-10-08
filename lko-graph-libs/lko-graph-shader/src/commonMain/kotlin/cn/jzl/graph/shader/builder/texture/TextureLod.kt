package cn.jzl.graph.shader.builder.texture

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.builder.TripleInputShaderPipelineNodeBuilder
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType
import cn.jzl.shader.textureLod

object TextureLod : TripleInputShaderPipelineNodeBuilder("TextureLod", "textureLod", "Texture/TextureLod") {
    override val first = createNodeInput("texture", "Texture", required = true)
    override val second = createNodeInput("uv", "UV", required = true)
    override val third = createNodeInput("lod", "LOD", required = true)

    override val output = createNodeOutput("lod", "LOD")

    override fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType> = textureLod(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second),
        graphType.operand(blackboard, third)
    )
}