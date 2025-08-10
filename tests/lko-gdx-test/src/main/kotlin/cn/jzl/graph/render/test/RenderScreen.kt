package cn.jzl.graph.render.test

import cn.jzl.di.instance
import cn.jzl.ecs.world
import cn.jzl.graph.common.config.DefaultGraphPipelineConfiguration
import cn.jzl.graph.common.config.DefaultPropertyContainer
import cn.jzl.graph.common.data.DefaultGraphWithProperties
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.common.time.DefaultTimeKeeper
import cn.jzl.graph.impl.DefaultGraphConnection
import cn.jzl.graph.impl.DefaultGraphNode
import cn.jzl.graph.render.PipelineRendererLoader
import cn.jzl.graph.render.RenderOutput
import cn.jzl.graph.render.field.TextureType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector3Type
import cn.jzl.graph.render.renderPipelineModule
import cn.jzl.graph.shader.ModelShaderLoader
import cn.jzl.graph.shader.builder.property.PropertyLocation
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import cn.jzl.graph.shader.core.SimpleShaderRendererConfiguration
import cn.jzl.graph.shader.shaderPipelineModule
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.profiling.GLErrorListener
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxScreen
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.verticalGroup
import ktx.scene2d.vis.visLabel
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class RenderScreen : KtxScreen {

    private val world = world {
        module(renderPipelineModule())
        module(pipelineModule())
        module(shaderPipelineModule())
    }
    private val timeProvider = DefaultTimeKeeper()
    private val pipelineRendererLoader by world.instance<PipelineRendererLoader>()
    private val modelShaderLoader by world.instance<ModelShaderLoader>()
    private val screenViewport by lazy { ScreenViewport() }
    private val texture by lazy { Texture("test.jpeg") }

    private val graph by lazy {

        val graph = DefaultGraphWithProperties("Render_Pipeline")
        graph.addGraphNode(DefaultGraphNode("Start", "PipelineStart"))
        graph.addGraphNode(DefaultGraphNode("end", "end"))
        graph.addGraphNode(DefaultGraphNode("Color", "constant", hashMapOf("constant" to Color.BLACK)))
        graph.addGraphNode(DefaultGraphNode("camera", "constant", hashMapOf("constant" to screenViewport.camera)))

        graph.addGraphNode(
            DefaultGraphNode(
                id = "GraphShaderRenderer",
                type = "GraphShaderRenderer",
                payloads = mapOf("shaders" to shaderGraph, "tag" to "test", "endNodeId" to "end")
            )
        )

        graph.addGraphConnection(DefaultGraphConnection("camera", "output", "GraphShaderRenderer", "camera"))
        graph.addGraphConnection(DefaultGraphConnection("Color", "output", "Start", "background"))
        graph.addGraphConnection(DefaultGraphConnection("Start", "output", "GraphShaderRenderer", "input"))
        graph.addGraphConnection(DefaultGraphConnection("GraphShaderRenderer", "output", "end", "input"))

        graph
    }

    private val shaderGraph by lazy {
        val graph = DefaultGraphWithProperties("Model_Shader")
        graph.addGraphNode(DefaultGraphNode("end", "ShaderEnd"))
        graph.addGraphNode(DefaultGraphNode("Sampler2D", "Sampler2D"))

        graph.addGraphNode(
            DefaultGraphNode(
                id = "position",
                type = "Property",
                payloads = mapOf("fieldType" to Vector3Type, "propertyLocation" to PropertyLocation.Attribute, "propertyName" to "position"),
            )
        )
        graph.addGraphNode(
            DefaultGraphNode(
                id = "uv",
                type = "Property",
                payloads = mapOf("fieldType" to Vector2Type, "propertyLocation" to PropertyLocation.Attribute, "propertyName" to "uv"),
            )
        )

        graph.addGraphNode(
            DefaultGraphNode(
                id = "texture",
                type = "Property",
                payloads = mapOf("fieldType" to TextureType, "propertyLocation" to PropertyLocation.Uniform, "propertyName" to "texture"),
            )
        )

        graph.addGraphConnection(DefaultGraphConnection("texture", "output", "Sampler2D", "texture"))
        graph.addGraphConnection(DefaultGraphConnection("uv", "output", "Sampler2D", "uv"))

        graph.addGraphConnection(DefaultGraphConnection("Sampler2D", "color", "end", "color"))
        graph.addGraphConnection(DefaultGraphConnection("position", "output", "end", "position"))
        graph
    }

    private val pipelineRenderer by lazy {
        val configuration = DefaultGraphPipelineConfiguration(timeProvider)
        val propertyContainer = DefaultPropertyContainer()
        propertyContainer[TextureType.createPropertyKey("texture")] = texture
        val shaderRendererConfiguration = SimpleShaderRendererConfiguration(propertyContainer)
        // 添加三角形模型到渲染配置
        shaderRendererConfiguration += TestRenderableModel()
        configuration.setConfiguration(ShaderRendererConfiguration::class, shaderRendererConfiguration)
        pipelineRendererLoader.loadPipelineRenderer(graph, configuration, "end")
    }
    private val stage by lazy { Stage(ScreenViewport()) }

    private lateinit var profiler: GLProfiler
    private lateinit var debugInfo: Label

    private val spriteBatch = SpriteBatch()

    override fun show() {
        super.show()
        Gdx.app.logLevel = Application.LOG_DEBUG
        this.profiler = GLProfiler(Gdx.app.graphics)
        profiler.listener = GLErrorListener { log.debug { "error code $it" } }
        stage.actors {
            verticalGroup {
                setFillParent(true)
                align(Align.topRight)
                debugInfo = visLabel("Hello World")
            }
        }
        screenViewport.setScreenSize(Gdx.graphics.width, Gdx.graphics.height)
        screenViewport.apply(true)
    }

    override fun render(delta: Float) {
        if (!profiler.isEnabled) {
            profiler.enable()
        }
        screenViewport.apply(true)
        timeProvider.update(delta.toDouble().toDuration(DurationUnit.SECONDS))
        pipelineRenderer.render(RenderOutput.Companion)
        if (profiler.isEnabled) {
            debugInfo.setText(
                """
                Time: ${timeProvider.time.toString(DurationUnit.SECONDS, 2)}
                GL Calls: ${profiler.calls}
                Draw calls: ${profiler.drawCalls}
                Shader switches: ${profiler.shaderSwitches}
                Texture bindings: ${profiler.textureBindings}
                Vertex calls: ${profiler.vertexCount.total}
                fps: ${Gdx.graphics.framesPerSecond}
                memory: ${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)} MB
            """.trimIndent()
            )
            stage.act(delta)
            stage.draw()
            profiler.reset()
        }
    }

    companion object {
        private val log = logger<RenderScreen>()
    }
}