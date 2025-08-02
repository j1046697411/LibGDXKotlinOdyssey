package cn.jzl.graph.render

import cn.jzl.di.module
import cn.jzl.di.new
import cn.jzl.di.prototype
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.graph.common.rendering.PipelinePlugin
import cn.jzl.graph.common.rendering.PipelineRegistry
import cn.jzl.graph.common.rendering.register
import cn.jzl.graph.render.calculator.*
import cn.jzl.graph.render.field.*
import cn.jzl.graph.render.producer.EndRenderingPipelineNodeProducer
import cn.jzl.graph.render.producer.PipelineRendererNodeProducer
import cn.jzl.graph.render.producer.StartRenderingPipelineNodeProducer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4
import org.kodein.type.TypeToken

fun renderPipelineModule() = module(TypeToken.Any, "RenderPipelineModule") {
    this bind singleton { new(::RenderPipelinePlugin) }

    this bind singleton { new(::DefaultBufferCopyHelper) }
    this bind singleton { new(::DefaultFullScreenRender) }
    this bind singleton { new(::DefaultTextureFrameBufferCache) }
    this bind singleton { new(::StateOpenGLContext) }
    this bind prototype { new(::DefaultRenderingPipeline) }
    this bind singleton { new(::DefaultPipelineRendererLoader) }
}

class RenderPipelinePlugin : PipelinePlugin {

    override fun setup(world: World, pipelineRegistry: PipelineRegistry) {

        pipelineRegistry.register(EndRenderingPipelineNodeProducer())
        pipelineRegistry.register(StartRenderingPipelineNodeProducer())
        pipelineRegistry.register(PipelineRendererNodeProducer())

        pipelineRegistry.registerGraphTypes(DefaultRenderGraphType("Render_Pipeline"))

        pipelineRegistry.registerFieldTypes(RenderingPipelineType)
        pipelineRegistry.registerFieldTypes(CameraType)
        pipelineRegistry.registerFieldTypes(ColorType)
        pipelineRegistry.registerFieldTypes(Matrix4Type)
        pipelineRegistry.registerFieldTypes(Vector2Type)
        pipelineRegistry.registerFieldTypes(Vector3Type)
        pipelineRegistry.registerFieldTypes(Vector4Type)
        pipelineRegistry.registerFieldTypes(TextureType)

        pipelineRegistry.addDefaultFieldType(Vector2Type)
        pipelineRegistry.addDefaultFieldType(Vector3Type)
        pipelineRegistry.addDefaultFieldType(Vector4Type)
        pipelineRegistry.addDefaultFieldType(ColorType)

        pipelineRegistry.register(Vector2::class, Vector2Calculator)
        pipelineRegistry.register(Vector3::class, Vector3Calculator)
        pipelineRegistry.register(Vector4::class, Vector4Calculator)
        pipelineRegistry.register(Color::class, ColorCalculator)

        pipelineRegistry.register(Vector2::class, Float::class, Vector2ToFloatCalculator)
        pipelineRegistry.register(Vector3::class, Float::class, Vector3ToFloatCalculator)
        pipelineRegistry.register(Vector4::class, Float::class, Vector4ToFloatCalculator)
        pipelineRegistry.register(Color::class, Float::class, ColorToFloatCalculator)

        pipelineRegistry.register(Float::class, Vector2::class, FloatToVector2Calculator)
        pipelineRegistry.register(Float::class, Vector3::class, FloatToVector3Calculator)
        pipelineRegistry.register(Float::class, Vector4::class, FloatToVector4Calculator)
        pipelineRegistry.register(Float::class, Color::class, FloatToColorCalculator)

        pipelineRegistry.register(Vector2::class, Vector2::class, Vector2ToVector2Calculator)
        pipelineRegistry.register(Vector3::class, Vector3::class, Vector3ToVector3Calculator)
        pipelineRegistry.register(Vector4::class, Vector4::class, Vector4ToVector4Calculator)
        pipelineRegistry.register(Color::class, Color::class, ColorToColorCalculator)

        pipelineRegistry.register(Vector2::class, Float::class, Float::class, Vector2ToFloatTripleInputCalculator)
        pipelineRegistry.register(Vector3::class, Float::class, Float::class, Vector3ToFloatTripleInputCalculator)
        pipelineRegistry.register(Vector4::class, Float::class, Float::class, Vector4ToFloatTripleInputCalculator)
        pipelineRegistry.register(Color::class, Float::class, Float::class, ColorToFloatTripleInputCalculator)
    }
}


