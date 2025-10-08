package cn.jzl.graph.shader.builder

import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.rendering.PipelineBlackboard
import cn.jzl.graph.shader.ShaderGraphType
import cn.jzl.graph.shader.field.operand
import cn.jzl.shader.*

object Plus : AcceptingMultipleSingleInputShaderPipelineNodeBuilder("Plus", "plus", "Math/Add(+)") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>
    ): Operand<out VarType> {
        return inputs.map {
            graphType.operand<VarType.Computable>(blackboard, it)
        }.reduce { acc, operand -> acc + operand }
    }
}

object OneMinus : SingleInputShaderPipelineNodeBuilder("OneMinus", "oneMinus", "Math/OneMinus(1-x)") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = oneMinus(graphType.operand(blackboard, input))
}

object Reciprocal : SingleInputShaderPipelineNodeBuilder("Reciprocal", "reciprocal", "Math/Reciprocal(1/x)") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = reciprocal(graphType.operand(blackboard, input))
}

object Minus : DualInputShaderPipelineNodeBuilder("Minus", "minus", "Math/Subtract(-)") {
    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> {
        return graphType.operand<VarType.Computable>(blackboard, first) - graphType.operand(blackboard, second)
    }
}

object Times : AcceptingMultipleSingleInputShaderPipelineNodeBuilder("Times", "times", "Math/Times(*)") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>
    ): Operand<out VarType> {
        return inputs.map {
            graphType.operand<VarType.Computable>(blackboard, it)
        }.reduce { acc, operand -> acc * operand }
    }

}

object Div : DualInputShaderPipelineNodeBuilder("Div", "div", "Math/Divide(/)") {
    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> {
        return graphType.operand<VarType.Computable>(blackboard, first) / graphType.operand(blackboard, second)
    }
}

object Rem : DualInputShaderPipelineNodeBuilder("Rem", "rem", "Math/Remainder(%)") {
    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> {
        return graphType.operand<VarType.Computable>(blackboard, first) % graphType.operand(blackboard, second)
    }
}

object Minimum : AcceptingMultipleSingleInputShaderPipelineNodeBuilder("Minimum", "minimum", "Math/Minimum") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>
    ): Operand<out VarType> {
        return inputs.map {
            graphType.operand<VarType.NumberType>(blackboard, it)
        }.reduce { acc, operand -> min(acc, operand) }
    }
}

object Maximum : AcceptingMultipleSingleInputShaderPipelineNodeBuilder("Maximum", "maximum", "Math/Maximum") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        inputs: Sequence<PipelineNodeInput>
    ): Operand<out VarType> {
        return inputs.map {
            graphType.operand<VarType.NumberType>(blackboard, it)
        }.reduce { acc, operand -> max(acc, operand) }
    }
}

object Abs : SingleInputShaderPipelineNodeBuilder("Abs", "abs", "Math/Absolute") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = abs(graphType.operand(blackboard, input))
}


object Ceil : SingleInputShaderPipelineNodeBuilder("Ceil", "ceil", "Math/Ceil") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = ceil(graphType.operand(blackboard, input))
}

object Clamp : TripleInputShaderPipelineNodeBuilder("Clamp", "clamp", "Math/Clamp") {

    override val first = createNodeInput("input", "Input", required = true)
    override val second = createNodeInput("min", "Min", required = true)
    override val third = createNodeInput("max", "Max", required = true)

    override fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType> = clamp(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second),
        graphType.operand(blackboard, third)
    )
}

object Exp : SingleInputShaderPipelineNodeBuilder("Exp", "exp", "Math/Exp") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = exp(graphType.operand(blackboard, input))
}

object Exp2 : SingleInputShaderPipelineNodeBuilder("Exp2", "exp2", "Math/Exp2") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = exp2(graphType.operand(blackboard, input))
}

object Floor : SingleInputShaderPipelineNodeBuilder("Floor", "floor", "Math/Floor") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = floor(graphType.operand(blackboard, input))
}

object Fract : SingleInputShaderPipelineNodeBuilder("Fract", "fract", "Math/Fract") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = fract(graphType.operand(blackboard, input))
}

object InverseSqrt : SingleInputShaderPipelineNodeBuilder("InverseSqrt", "inverseSqrt", "Math/InverseSqrt") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = inverseSqrt(graphType.operand(blackboard, input))
}

object Log : SingleInputShaderPipelineNodeBuilder("Log", "log", "Math/Log") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = log(graphType.operand(blackboard, input))
}

object Log2 : SingleInputShaderPipelineNodeBuilder("Log2", "log2", "Math/Log2") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = log2(graphType.operand(blackboard, input))
}

object Mix : TripleInputShaderPipelineNodeBuilder("Mix", "mix", "Math/Mix") {

    override val first = createNodeInput("x", "X", required = true)
    override val second = createNodeInput("y", "Y", required = true)
    override val third = createNodeInput("a", "A", required = true)

    override fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType> = mix(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second),
        graphType.operand(blackboard, third)
    )
}

object Pow : DualInputShaderPipelineNodeBuilder("Pow", "pow", "Math/Pow") {

    override val first = createNodeInput("x", "Input", required = true)
    override val second = createNodeInput("y", "Y", required = true)

    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> = pow(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second)
    )
}

object Sign : SingleInputShaderPipelineNodeBuilder("Sign", "sign", "Math/Sign") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = sign(graphType.operand(blackboard, input))
}

object Smoothstep : TripleInputShaderPipelineNodeBuilder("Smoothstep", "smoothstep", "Math/Smoothstep") {

    override val first = createNodeInput("edge0", "Edge0", required = true)
    override val second = createNodeInput("edge1", "Edge1", required = true)
    override val third = createNodeInput("x", "X", required = true)

    override fun ProgramScope.ShaderScope.buildNodeTripleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput,
        third: PipelineNodeInput
    ): Operand<out VarType> = smoothstep(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second),
        graphType.operand(blackboard, third)
    )
}

object Sqrt : SingleInputShaderPipelineNodeBuilder("Sqrt", "sqrt", "Math/Sqrt") {
    override fun ProgramScope.ShaderScope.buildNodeSingleInput(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        input: PipelineNodeInput
    ): Operand<out VarType> = sqrt(graphType.operand(blackboard, input))
}

object Step : DualInputShaderPipelineNodeBuilder("Step", "step", "Math/Step") {

    override val first = createNodeInput("edge0", "Edge0", required = true)
    override val second = createNodeInput("x", "X", required = true)

    override fun ProgramScope.ShaderScope.buildNodeDualInputs(
        graphType: ShaderGraphType,
        blackboard: PipelineBlackboard,
        first: PipelineNodeInput,
        second: PipelineNodeInput
    ): Operand<out VarType> = step(
        graphType.operand(blackboard, first),
        graphType.operand(blackboard, second)
    )
}
