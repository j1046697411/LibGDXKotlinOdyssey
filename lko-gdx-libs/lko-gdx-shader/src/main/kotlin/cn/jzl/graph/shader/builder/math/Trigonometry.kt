package cn.jzl.graph.shader.builder.math

import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.DualInputShaderPipelineNodeProducer
import cn.jzl.graph.shader.builder.SingleInputShaderPipelineNodeProducer

class ArcCos : SingleInputShaderPipelineNodeProducer("ArcCos", "ArcCos", "Math/ArcCos") {

    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "acos(${inputFieldOutput.representation})"
}

class ArcSin : SingleInputShaderPipelineNodeProducer("ArcSin", "ArcSin", "Math/ArcSin") {

    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "asin(${inputFieldOutput.representation})"
}

class ArcTan2 : DualInputShaderPipelineNodeProducer("ArcTan2", "ArcTan2", "Math/ArcTan2") {

    override fun buildFragmentNodeDualInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput
    ): String = "atan2(${firstFieldOutput.representation}.y, ${firstFieldOutput.representation}.x)"
}

class ArcTan : SingleInputShaderPipelineNodeProducer("ArcTan", "ArcTan", "Math/ArcTan") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "atan(${inputFieldOutput.representation})"
}

class Cos : SingleInputShaderPipelineNodeProducer("Cos", "Cos", "Math/Cos") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "cos(${inputFieldOutput.representation})"
}

class Sin : SingleInputShaderPipelineNodeProducer("Sin", "Sin", "Math/Sin") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "sin(${inputFieldOutput.representation})"
}

class Degrees : SingleInputShaderPipelineNodeProducer("Degrees", "Degrees", "Math/Degrees") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "degrees(${inputFieldOutput.representation})"
}

class Radians : SingleInputShaderPipelineNodeProducer("Radians", "Radians", "Math/Radians") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "radians(${inputFieldOutput.representation})"
}

class Tan : SingleInputShaderPipelineNodeProducer("Tan", "Tan", "Math/Tan") {
    override fun buildFragmentNodeSingleInput(
        commonShaderBuilder: CommonShaderBuilder,
        inputFieldOutput: FieldOutput
    ): String = "tan(${inputFieldOutput.representation})"
}
