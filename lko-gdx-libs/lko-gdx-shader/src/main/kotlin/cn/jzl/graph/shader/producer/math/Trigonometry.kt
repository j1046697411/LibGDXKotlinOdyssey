package cn.jzl.graph.shader.producer.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.producer.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.producer.SingleInputShaderPipelineNodeProducer

class ArcCos : SingleInputShaderPipelineNodeProducer("ArcCos", "ArcCos") {

    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "acos(${inputFieldOutput.representation})"
}

class ArcSin : SingleInputShaderPipelineNodeProducer("ArcSin", "ArcSin") {

    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "asin(${inputFieldOutput.representation})"
}

class ArcTan2 : DualInputShaderPipelineNodeProducer("ArcTan2", "ArcTan2") {

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "atan2(${firstFieldOutput.representation}.y, ${firstFieldOutput.representation}.x)"
}

class ArcTan : SingleInputShaderPipelineNodeProducer("ArcTan", "ArcTan") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "atan(${inputFieldOutput.representation})"
}

class Cos : SingleInputShaderPipelineNodeProducer("Cos", "Cos") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "cos(${inputFieldOutput.representation})"
}

class Sin : SingleInputShaderPipelineNodeProducer("Sin", "Sin") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "sin(${inputFieldOutput.representation})"
}

class Degrees : SingleInputShaderPipelineNodeProducer("Degrees", "Degrees") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "degrees(${inputFieldOutput.representation})"
}

class Radians : SingleInputShaderPipelineNodeProducer("Radians", "Radians") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "radians(${inputFieldOutput.representation})"
}

class Tan : SingleInputShaderPipelineNodeProducer("Tan", "Tan") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput,
        outputFieldOutput: FieldOutput
    ): String = "tan(${inputFieldOutput.representation})"
}
