package cn.jzl.graph.shader.builder.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.SingleInputShaderPipelineNodeProducer

class ExponentialBase2: SingleInputShaderPipelineNodeProducer("ExponentialBase2", "ExponentialBase2") {

    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "exp2(${inputFieldOutput.representation})"
}

class Exponential : SingleInputShaderPipelineNodeProducer("Exponential", "Exponential") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "exp(${inputFieldOutput.representation})"
}

class InverseSquareRoot : SingleInputShaderPipelineNodeProducer("InverseSquareRoot", "InverseSquareRoot") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "inversesqrt(${inputFieldOutput.representation})"
}

class LogarithmBase2 : SingleInputShaderPipelineNodeProducer("LogarithmBase2", "LogarithmBase2") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "log2(${inputFieldOutput.representation})"
}

class NaturalLogarithm : SingleInputShaderPipelineNodeProducer("NaturalLogarithm", "NaturalLogarithm") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "log(${inputFieldOutput.representation})"
}

class PowerShader : DualInputShaderPipelineNodeProducer("Power", "Power") {
    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "pow(${firstFieldOutput.representation}, ${secondFieldOutput.representation})"
}


class SquareRoot : SingleInputShaderPipelineNodeProducer("SquareRoot", "SquareRoot") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "sqrt(${inputFieldOutput.representation})"
}
