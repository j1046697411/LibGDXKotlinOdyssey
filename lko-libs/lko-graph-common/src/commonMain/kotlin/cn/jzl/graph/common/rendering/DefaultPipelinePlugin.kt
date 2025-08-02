package cn.jzl.graph.common.rendering

import cn.jzl.ecs.World
import cn.jzl.graph.common.calculator.FloatCalculator
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.general.math.*
import cn.jzl.graph.common.producer.general.provided.Constant
import cn.jzl.graph.common.producer.general.provided.Provider
import cn.jzl.graph.common.producer.general.provided.Time

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
        pipelineRegistry.register(Constant())
        pipelineRegistry.register(Time())

        pipelineRegistry.registerFieldTypes(PrimitiveFieldTypes.FloatFieldType)
        pipelineRegistry.registerFieldTypes(PrimitiveFieldTypes.BooleanFieldType)

        pipelineRegistry.addDefaultFieldType(PrimitiveFieldTypes.FloatFieldType)

        pipelineRegistry.register(Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, FloatCalculator)
        pipelineRegistry.register(Float::class, Float::class, Float::class, FloatCalculator)
    }
}