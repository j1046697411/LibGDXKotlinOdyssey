package cn.jzl.graph.shader.field

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.shader.Operand
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.Struct
import cn.jzl.shader.VarType

interface ShaderFieldType<T : VarType, O : Operand<out T>> : FieldType<O>

fun <S : Struct<S>> ShaderGraphType.struct(blackboard: PipelineBlackboard, input: PipelineNodeInput): S {
    return blackboard[input.fromGraphNode, input.fromOutput, shaderFieldTypeResolver.resolve(input.outputType)]
}

fun <T : VarType> ShaderGraphType.operand(blackboard: PipelineBlackboard, input: PipelineNodeInput): Operand<T> {
    return blackboard[input.fromGraphNode, input.fromOutput, shaderFieldTypeResolver.resolve(input.outputType)]
}

fun ShaderGraphType.output(graphNode: GraphNode, blackboard: PipelineBlackboard, output: PipelineNodeOutput, value: Operand<out VarType>) {
    val shaderFieldType = shaderFieldTypeResolver.resolve<VarType, Operand<out VarType>>(output.outputType)
    blackboard[graphNode, output.output, shaderFieldType] = value
}

interface ShaderFieldTypeResolver {
    fun <T : VarType, O : Operand<out T>> resolve(fieldType: FieldType<*>): ShaderFieldType<T, O>
}

fun interface GraphShaderPipelineNode : PipelineNode {
    fun ProgramScope.build(blackboard: PipelineBlackboard, fragmentShader: Boolean)
}

interface ShaderGraphType : GraphType<GraphShaderPipelineNode> {
    val shaderFieldTypeResolver: ShaderFieldTypeResolver
}

