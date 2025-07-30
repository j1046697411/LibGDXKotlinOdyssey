package cn.jzl.graph.common.rendering

import cn.jzl.ecs.World
import cn.jzl.graph.common.calculator.FloatCalculator
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.general.math.Abs
import cn.jzl.graph.common.producer.general.math.Arccos
import cn.jzl.graph.common.producer.general.math.Arcsin
import cn.jzl.graph.common.producer.general.math.Arctan
import cn.jzl.graph.common.producer.general.math.Ceiling
import cn.jzl.graph.common.producer.general.math.Clamp
import cn.jzl.graph.common.producer.general.math.Cos
import cn.jzl.graph.common.producer.general.math.Degrees
import cn.jzl.graph.common.producer.general.math.Div
import cn.jzl.graph.common.producer.general.math.Exponential
import cn.jzl.graph.common.producer.general.math.ExponentialBase2
import cn.jzl.graph.common.producer.general.math.Floor
import cn.jzl.graph.common.producer.general.math.FractionalPart
import cn.jzl.graph.common.producer.general.math.InverseSqrt
import cn.jzl.graph.common.producer.general.math.Lerp
import cn.jzl.graph.common.producer.general.math.LogarithmBase2
import cn.jzl.graph.common.producer.general.math.Max
import cn.jzl.graph.common.producer.general.math.Min
import cn.jzl.graph.common.producer.general.math.Minus
import cn.jzl.graph.common.producer.general.math.Modulo
import cn.jzl.graph.common.producer.general.math.NaturalLogarithm
import cn.jzl.graph.common.producer.general.math.OneMinus
import cn.jzl.graph.common.producer.general.math.Plus
import cn.jzl.graph.common.producer.general.math.Power
import cn.jzl.graph.common.producer.general.math.Radians
import cn.jzl.graph.common.producer.general.math.Reciprocal
import cn.jzl.graph.common.producer.general.math.Rem
import cn.jzl.graph.common.producer.general.math.Saturate
import cn.jzl.graph.common.producer.general.math.Signum
import cn.jzl.graph.common.producer.general.math.Sin
import cn.jzl.graph.common.producer.general.math.Smoothstep
import cn.jzl.graph.common.producer.general.math.Square
import cn.jzl.graph.common.producer.general.math.Step
import cn.jzl.graph.common.producer.general.math.Tan
import cn.jzl.graph.common.producer.general.math.Times
import cn.jzl.graph.common.producer.general.provided.Provider

class DefaultPipelinePlugin : PipelinePlugin {
    override fun setup(world: World, pipelineRegistry: PipelineRegistry) {
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

        pipelineRegistry.registerFieldTypes(PrimitiveFieldTypes.FloatFieldType)
        pipelineRegistry.registerFieldTypes(PrimitiveFieldTypes.BooleanFieldType)

        pipelineRegistry.addDefaultFieldType(PrimitiveFieldTypes.FloatFieldType)

        pipelineRegistry.register(Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, Float::class, FloatCalculator)
    }
}