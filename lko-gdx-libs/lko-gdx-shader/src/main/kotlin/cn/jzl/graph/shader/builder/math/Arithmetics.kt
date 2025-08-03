package cn.jzl.graph.shader.builder.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.AcceptingMultipleSingleInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.SingleInputShaderPipelineNodeProducer

class Plus : AcceptingMultipleSingleInputShaderPipelineNodeProducer("Plus", "Plus") {

    override val inputs = createNodeInput("inputs", "inputs", required = true, acceptingMultiple = true)
    override val output = createNodeOutput("output", "output", required = true)

    override fun buildFragmentNodeSingleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        inputs: Sequence<FieldOutput>
    ): String = inputs.joinToString(" + ") { it.representation }
}


class Times : AcceptingMultipleSingleInputShaderPipelineNodeProducer("Times", "Times") {

    override val inputs = createNodeInput("inputs", "inputs", required = true, acceptingMultiple = true)
    override val output = createNodeOutput("output", "output", required = true)

    override fun buildFragmentNodeSingleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        inputs: Sequence<FieldOutput>
    ): String = inputs.joinToString(" * ") { it.representation }
}

class Minus : DualInputShaderPipelineNodeProducer("Minus", "Minus") {
    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "${firstFieldOutput.representation} - ${secondFieldOutput.representation}"
}

class Div : DualInputShaderPipelineNodeProducer("Div", "Div") {
    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "${firstFieldOutput.representation} / ${secondFieldOutput.representation}"
}

class Rem : DualInputShaderPipelineNodeProducer("Rem", "Rem") {
    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "${firstFieldOutput.representation} % ${secondFieldOutput.representation}"
}

class OneMinus : SingleInputShaderPipelineNodeProducer("OneMinus", "OneMinus") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "1.0 - ${inputFieldOutput.representation}"
}


class Reciprocal : SingleInputShaderPipelineNodeProducer("Reciprocal", "Reciprocal") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "1.0 / ${inputFieldOutput.representation}"
}
