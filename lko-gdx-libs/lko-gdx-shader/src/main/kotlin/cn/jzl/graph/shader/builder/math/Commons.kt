package cn.jzl.graph.shader.builder.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.AcceptingMultipleSingleInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.SingleInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.TripleInputShaderPipelineNodeProducer

class Abs : SingleInputShaderPipelineNodeProducer("Abs", "Abs") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "abs(${inputFieldOutput.representation})"
}

class Ceiling : SingleInputShaderPipelineNodeProducer("Ceiling", "Ceiling") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "ceil(${inputFieldOutput.representation})"
}

class Floor : SingleInputShaderPipelineNodeProducer("Floor", "Floor") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "floor(${inputFieldOutput.representation})"
}

class Clamp : TripleInputShaderPipelineNodeProducer("Clamp", "Clamp") {

    override val first = createNodeInput("input", "Input", required = true)
    override val second = createNodeInput("min", "Min", required = true)
    override val third = createNodeInput("max", "Max", required = true)

    override fun buildFragmentNodeTripleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput,
        thirdFieldOutput: FieldOutput
    ): String = "clamp(${firstFieldOutput.representation}, ${secondFieldOutput.representation}, ${thirdFieldOutput.representation})"
}

class Fractional : SingleInputShaderPipelineNodeProducer("Fractional", "Fractional") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "fract(${inputFieldOutput.representation})"
}

class Lerp : TripleInputShaderPipelineNodeProducer("Lerp", "Lerp") {
    override val first = createNodeInput("a", "A", required = true)
    override val second = createNodeInput("b", "B", required = true)
    override val third = createNodeInput("t", "T", required = true)

    override fun buildFragmentNodeTripleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput,
        thirdFieldOutput: FieldOutput
    ): String = "mix(${firstFieldOutput.representation}, ${secondFieldOutput.representation}, ${thirdFieldOutput.representation})"
}

class Maximum : AcceptingMultipleSingleInputShaderPipelineNodeProducer("Max", "Max") {
    override fun buildFragmentNodeSingleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        inputs: Sequence<FieldOutput>
    ): String = inputs.map { it.representation }.reduce { acc, output -> "max($acc, $output)" }
}

class Minimum : AcceptingMultipleSingleInputShaderPipelineNodeProducer("Min", "Min") {
    override fun buildFragmentNodeSingleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        inputs: Sequence<FieldOutput>
    ): String = inputs.map { it.representation }.reduce { acc, output -> "min($acc, $output)" }
}

class Modulo : DualInputShaderPipelineNodeProducer("Modulo", "Modulo") {

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "mod(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}

class Saturate : SingleInputShaderPipelineNodeProducer("Saturate", "Saturate") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "clamp(${inputFieldOutput.representation}, 0.0, 1.0)"
}


class Sign : SingleInputShaderPipelineNodeProducer("Sign", "Sign") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "sign(${inputFieldOutput.representation})"
}

class SmoothStep : TripleInputShaderPipelineNodeProducer("SmoothStep", "SmoothStep") {
    override val first = createNodeInput("edge0", "Edge0", required = true)
    override val second = createNodeInput("edge1", "Edge1", required = true)
    override val third = createNodeInput("x", "X", required = true)

    override fun buildFragmentNodeTripleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput,
        thirdFieldOutput: FieldOutput
    ): String = "smoothstep(${firstFieldOutput.representation}, ${secondFieldOutput.representation}, ${thirdFieldOutput.representation})"
}

class Step : DualInputShaderPipelineNodeProducer("Step", "Step") {

    override val first = createNodeInput("input", "input", required = true)
    override val second = createNodeInput("edge", "edge", required = true)

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "step(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}
