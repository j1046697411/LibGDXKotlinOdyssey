package cn.jzl.graph.shader.builder

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.field.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType
import cn.jzl.shader.determinant
import cn.jzl.shader.inverse
import cn.jzl.shader.matrixCompMult
import cn.jzl.shader.transpose

object Determinant : SingleInputShaderPipelineNodeBuilder("Determinant", "determinant", "Matrix/Determinant") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = determinant(graphType.operand(blackboard, input))
}

object Inverse : SingleInputShaderPipelineNodeBuilder("Inverse", "inverse", "Matrix/Inverse") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = inverse(graphType.operand(blackboard, input))
}

object MatrixCompMult : DualInputShaderPipelineNodeBuilder("MatrixCompMult", "matrixCompMult", "Matrix/MatrixCompMult") {
    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> = matrixCompMult(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second)
    )
}

object Transpose : SingleInputShaderPipelineNodeBuilder("Transpose", "transpose", "Matrix/Transpose") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = transpose(graphType.operand(blackboard, input))
}
