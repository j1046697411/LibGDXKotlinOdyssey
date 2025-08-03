package cn.jzl.graph.shader.builder.util

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.SingleInputShaderPipelineNodeProducer

class RemapValue : SingleInputShaderPipelineNodeProducer("RemapValue", "RemapValue") {
    override val input = createNodeInput("input", "Input")
    override val output = createNodeOutput("output", "Output")
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = TODO()
}