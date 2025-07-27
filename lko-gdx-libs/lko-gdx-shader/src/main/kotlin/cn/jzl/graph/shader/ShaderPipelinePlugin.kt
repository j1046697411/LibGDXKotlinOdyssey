package cn.jzl.graph.shader

import cn.jzl.di.instance
import cn.jzl.di.module
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.graph.common.rendering.PipelinePlugin
import cn.jzl.graph.common.rendering.PipelineRegistry
import cn.jzl.graph.common.rendering.register
import cn.jzl.graph.shader.core.DefaultShaderGraphType
import cn.jzl.graph.shader.field.DefaultShaderFieldTypeResolver
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.producer.math.*
import cn.jzl.graph.shader.producer.util.Constant
import cn.jzl.graph.shader.producer.util.Merge
import cn.jzl.graph.shader.producer.util.Remap
import cn.jzl.graph.shader.producer.util.RemapValue
import cn.jzl.graph.shader.producer.util.Split
import org.kodein.type.TypeToken

fun shaderPipelineModule() = module(TypeToken.Any, "shaderPipelineModule") {
    this bind singleton { new(::ShaderPipelinePlugin) }
    this bind singleton { new(::DefaultShaderFieldTypeResolver) }
}

class ShaderPipelinePlugin : PipelinePlugin {

    override fun setup(world: World, pipelineRegistry: PipelineRegistry) {
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        pipelineRegistry.registerGraphTypes(DefaultShaderGraphType(shaderFieldTypeResolver, "Model_Shader"))

        // 注册Arithmetics.kt中的节点
        pipelineRegistry.register(Plus())
        pipelineRegistry.register(Times())
        pipelineRegistry.register(Minus())
        pipelineRegistry.register(Div())
        pipelineRegistry.register(Rem())
        pipelineRegistry.register(OneMinus())
        pipelineRegistry.register(Reciprocal())

        // 注册Commons.kt中的节点
        pipelineRegistry.register(Abs())
        pipelineRegistry.register(Ceiling())
        pipelineRegistry.register(Floor())
        pipelineRegistry.register(Clamp())
        pipelineRegistry.register(Fractional())
        pipelineRegistry.register(Lerp())
        pipelineRegistry.register(Maximum())
        pipelineRegistry.register(Minimum())
        pipelineRegistry.register(Modulo())
        pipelineRegistry.register(Saturate())

        // 注册Exponential.kt中的节点
        pipelineRegistry.register(ExponentialBase2())
        pipelineRegistry.register(Exponential())
        pipelineRegistry.register(InverseSquareRoot())
        pipelineRegistry.register(LogarithmBase2())
        pipelineRegistry.register(NaturalLogarithm())
        pipelineRegistry.register(PowerShader())
        pipelineRegistry.register(SquareRoot())

        // 注册Geometrics.kt中的节点
        pipelineRegistry.register(CrossProduct())
        pipelineRegistry.register(Distance())
        pipelineRegistry.register(DotProduct())
        pipelineRegistry.register(Length())
        pipelineRegistry.register(Normalize())

        // 注册Trigonometry.kt中的节点
        pipelineRegistry.register(ArcCos())
        pipelineRegistry.register(ArcSin())
        pipelineRegistry.register(ArcTan2())
        pipelineRegistry.register(ArcTan())
        pipelineRegistry.register(Cos())
        pipelineRegistry.register(Sin())
        pipelineRegistry.register(Degrees())
        pipelineRegistry.register(Radians())
        pipelineRegistry.register(Tan())

        // 注册Utility.kt中的节点
        pipelineRegistry.register(DistanceFromPlane())

        pipelineRegistry.register(Merge())
        pipelineRegistry.register(Split())
        pipelineRegistry.register(Remap())
        pipelineRegistry.register(RemapValue())
        pipelineRegistry.register(Constant())
    }
}