package cn.jzl.graph.shader.builder

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.field.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.*

object ACos : SingleInputShaderPipelineNodeBuilder("ACos", "acos", "Angles/ACos") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = acos(graphType.operand(blackboard, input))
}

object ACosh : SingleInputShaderPipelineNodeBuilder("ACosh", "acosh", "Angles/ACosh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = acosh(graphType.operand(blackboard, input))
}

object ASin : SingleInputShaderPipelineNodeBuilder("ASin", "asin", "Angles/ASin") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = asin(graphType.operand(blackboard, input))
}

object ASinh : SingleInputShaderPipelineNodeBuilder("ASinh", "asinh", "Angles/ASinh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = asinh(graphType.operand(blackboard, input))
}

object ATan : SingleInputShaderPipelineNodeBuilder("ATan", "atan", "Angles/ATan") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = atan(graphType.operand(blackboard, input))
}

object ATanh : SingleInputShaderPipelineNodeBuilder("ATanh", "atanh", "Angles/ATanh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = atanh(graphType.operand(blackboard, input))
}

object Cos : SingleInputShaderPipelineNodeBuilder("Cos", "cos", "Angles/Cos") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = cos(graphType.operand(blackboard, input))
}

object Cosh : SingleInputShaderPipelineNodeBuilder("Cosh", "cosh", "Angles/Cosh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = cosh(graphType.operand(blackboard, input))
}

object Degrees : SingleInputShaderPipelineNodeBuilder("Degrees", "degrees", "Angles/Degrees") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = degrees(graphType.operand(blackboard, input))
}

object Radians : SingleInputShaderPipelineNodeBuilder("Radians", "radians", "Angles/Radians") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = radians(graphType.operand(blackboard, input))
}

object Sin : SingleInputShaderPipelineNodeBuilder("Sin", "sin", "Angles/Sin") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = sin(graphType.operand(blackboard, input))
}

object Sinh : SingleInputShaderPipelineNodeBuilder("Sinh", "sinh", "Angles/Sinh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = sinh(graphType.operand(blackboard, input))
}

object Tan : SingleInputShaderPipelineNodeBuilder("Tan", "tan", "Angles/Tan") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = tan(graphType.operand(blackboard, input))
}

object Tanh : SingleInputShaderPipelineNodeBuilder("Tanh", "tanh", "Angles/Tanh") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = tanh(graphType.operand(blackboard, input))
}
