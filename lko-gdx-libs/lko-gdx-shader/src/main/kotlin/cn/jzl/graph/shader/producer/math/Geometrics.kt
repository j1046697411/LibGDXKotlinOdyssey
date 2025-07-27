package cn.jzl.graph.shader.producer.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.producer.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.producer.SingleInputShaderPipelineNodeProducer

class CrossProduct : DualInputShaderPipelineNodeProducer("CrossProduct", "CrossProduct") {
    override val first = createNodeInput("a", "A", required = true)
    override val second = createNodeInput("b", "B", required = true)

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "cross(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}

class Distance : DualInputShaderPipelineNodeProducer("Distance", "Distance") {
    override val first = createNodeInput("p0", "Point 0", required = true)
    override val second = createNodeInput("p1", "Point 1", required = true)

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "distance(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}

class DotProduct : DualInputShaderPipelineNodeProducer("DotProduct", "DotProduct") {
    override val first = createNodeInput("a", "A", required = true)
    override val second = createNodeInput("b", "B", required = true)

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "dot(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}

class Length : SingleInputShaderPipelineNodeProducer("Length", "Length") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "length(${inputFieldOutput.representation})"
}

class Normalize : SingleInputShaderPipelineNodeProducer("Normalize", "Normalize") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "normalize(${inputFieldOutput.representation})"
}
