package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.calculator.FloatCalculator
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.rendering.producer.math.Abs
import cn.jzl.graph.common.rendering.producer.math.Arccos
import cn.jzl.graph.common.rendering.producer.math.Arcsin
import cn.jzl.graph.common.rendering.producer.math.Arctan
import cn.jzl.graph.common.rendering.producer.math.Ceiling
import cn.jzl.graph.common.rendering.producer.math.Clamp
import cn.jzl.graph.common.rendering.producer.math.Cos
import cn.jzl.graph.common.rendering.producer.math.Degrees
import cn.jzl.graph.common.rendering.producer.math.Div
import cn.jzl.graph.common.rendering.producer.math.Exponential
import cn.jzl.graph.common.rendering.producer.math.ExponentialBase2
import cn.jzl.graph.common.rendering.producer.math.Floor
import cn.jzl.graph.common.rendering.producer.math.FractionalPart
import cn.jzl.graph.common.rendering.producer.math.InverseSqrt
import cn.jzl.graph.common.rendering.producer.math.Lerp
import cn.jzl.graph.common.rendering.producer.math.LogarithmBase2
import cn.jzl.graph.common.rendering.producer.math.Max
import cn.jzl.graph.common.rendering.producer.math.Min
import cn.jzl.graph.common.rendering.producer.math.Minus
import cn.jzl.graph.common.rendering.producer.math.Modulo
import cn.jzl.graph.common.rendering.producer.math.NaturalLogarithm
import cn.jzl.graph.common.rendering.producer.math.OneMinus
import cn.jzl.graph.common.rendering.producer.math.Plus
import cn.jzl.graph.common.rendering.producer.math.Power
import cn.jzl.graph.common.rendering.producer.math.Radians
import cn.jzl.graph.common.rendering.producer.math.Reciprocal
import cn.jzl.graph.common.rendering.producer.math.Rem
import cn.jzl.graph.common.rendering.producer.math.Saturate
import cn.jzl.graph.common.rendering.producer.math.Signum
import cn.jzl.graph.common.rendering.producer.math.Sin
import cn.jzl.graph.common.rendering.producer.math.Smoothstep
import cn.jzl.graph.common.rendering.producer.math.Square
import cn.jzl.graph.common.rendering.producer.math.Step
import cn.jzl.graph.common.rendering.producer.math.Tan
import cn.jzl.graph.common.rendering.producer.math.Times
import cn.jzl.graph.common.rendering.producer.provided.Provider

class DefaultPipelinePlugin : PipelinePlugin {
    override fun setup(pipelineRegistry: PipelineRegistry) {
        pipelineRegistry.register(Div())
        pipelineRegistry.register(Minus())
        pipelineRegistry.register(OneMinus())
        pipelineRegistry.register(Plus())
        pipelineRegistry.register(Reciprocal())
        pipelineRegistry.register(Rem())
        pipelineRegistry.register(Times())

        pipelineRegistry.register(Abs())
        pipelineRegistry.register(Ceiling())
        pipelineRegistry.register(Clamp())
        pipelineRegistry.register(Floor())
        pipelineRegistry.register(FractionalPart())
        pipelineRegistry.register(Lerp())
        pipelineRegistry.register(Max())
        pipelineRegistry.register(Min())
        pipelineRegistry.register(Modulo())
        pipelineRegistry.register(Saturate())
        pipelineRegistry.register(Signum())
        pipelineRegistry.register(Smoothstep())
        pipelineRegistry.register(Step())

        pipelineRegistry.register(Exponential())
        pipelineRegistry.register(ExponentialBase2())
        pipelineRegistry.register(InverseSqrt())
        pipelineRegistry.register(LogarithmBase2())
        pipelineRegistry.register(NaturalLogarithm())
        pipelineRegistry.register(Power())
        pipelineRegistry.register(Square())

        pipelineRegistry.register(Arccos())
        pipelineRegistry.register(Arcsin())
        pipelineRegistry.register(Arctan())
        pipelineRegistry.register(Cos())
        pipelineRegistry.register(Degrees())
        pipelineRegistry.register(Radians())
        pipelineRegistry.register(Sin())
        pipelineRegistry.register(Tan())

        pipelineRegistry.register(Provider())

        pipelineRegistry.register(PrimitiveFieldTypes.FloatFieldType)
        pipelineRegistry.register(PrimitiveFieldTypes.BooleanFieldType)

        pipelineRegistry.addDefaultFieldType(PrimitiveFieldTypes.FloatFieldType)

        pipelineRegistry.register(Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, Float::class, FloatCalculator)
    }
}