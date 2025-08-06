package cn.jzl.graph.render.test

import cn.jzl.di.instance
import cn.jzl.ecs.world
import cn.jzl.graph.common.config.DefaultGraphPipelineConfiguration
import cn.jzl.graph.common.config.DefaultPropertyContainer
import cn.jzl.graph.common.config.PropertyContainer
import cn.jzl.graph.common.data.DefaultGraphWithProperties
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.common.time.DefaultTimeKeeper
import cn.jzl.graph.impl.DefaultGraphConnection
import cn.jzl.graph.impl.DefaultGraphNode
import cn.jzl.graph.render.PipelineRendererLoader
import cn.jzl.graph.render.RenderOutput
import cn.jzl.graph.render.renderPipelineModule
import cn.jzl.graph.shader.ModelShaderLoader
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.RenderableModel
import cn.jzl.graph.shader.core.ShaderRendererConfiguration
import cn.jzl.graph.shader.core.SimpleShaderRendererConfiguration
import cn.jzl.graph.shader.shaderPipelineModule
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.profiling.GLErrorListener
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.verticalGroup
import ktx.scene2d.vis.visLabel
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    val configuration = Lwjgl3ApplicationConfiguration()
    configuration.setWindowedMode(1440, 810)
    configuration.setTitle("LKO Test")
    configuration.setIdleFPS(60)
    Lwjgl3Application(LKOGame(), configuration)
}

class LKOGame : KtxGame<KtxScreen>() {
    override fun create() {
        super.create()
        addScreen(RenderScreen())
        setScreen<RenderScreen>()
    }
}

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

    private val graph by lazy {
        val graph = DefaultGraphWithProperties("Render_Pipeline")
        graph.addGraphNode(DefaultGraphNode("Start", "PipelineStart"))
        graph.addGraphNode(DefaultGraphNode("end", "end"))
        graph.addGraphNode(DefaultGraphNode("Color", "constant", hashMapOf("constant" to Color.WHITE)))
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

        graph.addGraphNode(DefaultGraphNode("position", "Constant", hashMapOf("constant" to Vector3(100f, 100f, 100f))))
        graph.addGraphNode(DefaultGraphNode("color", "Constant", hashMapOf("constant" to Color.WHITE)))

        graph.addGraphConnection(DefaultGraphConnection("position", "output", "end", "position"))
        graph.addGraphConnection(DefaultGraphConnection("color", "output", "end", "color"))

        graph
    }

    private val pipelineRenderer by lazy {
        val configuration = DefaultGraphPipelineConfiguration(timeProvider)
        val shaderRendererConfiguration = SimpleShaderRendererConfiguration(DefaultPropertyContainer())
        configuration.setConfiguration(ShaderRendererConfiguration::class, shaderRendererConfiguration)
        pipelineRendererLoader.loadPipelineRenderer(graph, configuration, "end")
    }
    private val stage by lazy { Stage(ScreenViewport()) }

    private lateinit var profiler: GLProfiler
    private lateinit var debugInfo: Label
    override fun show() {
        super.show()
        Gdx.app.logLevel = Application.LOG_DEBUG
        this.profiler = GLProfiler(Gdx.app.graphics)
        profiler.listener = GLErrorListener { log.debug { "error code $it" } }
        VisUI.load(VisUI.SkinScale.X2)
        stage.actors {
            verticalGroup {
                setFillParent(true)
                align(Align.topRight)
                debugInfo = visLabel("Hello World")
            }
        }
    }

    override fun render(delta: Float) {
        if (!profiler.isEnabled) {
            profiler.enable()
        }
        timeProvider.update(delta.toDouble().toDuration(DurationUnit.SECONDS))
        pipelineRenderer.render(RenderOutput)
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

class TestRenderableModel : RenderableModel {

    private val mesh = Mesh(true,4, 6, VertexAttribute.Position())
    private val vertices = floatArrayOf(
        0f, 0f, 0f, 1f, 0f, 0f, 0f,
        100f, 0f, 0f, 0f, 1f, 0f, 0f,
        100f, 100f, 0f, 0f, 0f, 1f, 0f,
        0f, 100f, 0f, 1f, 1f, 1f, 0f
    )
    private val indices = shortArrayOf(0, 1, 2, 2, 3, 0)
    init {
        mesh.setVertices(vertices)
        mesh.setIndices(indices)
    }


    override val position: Vector3 = Vector3.Zero
    override val worldTransform: Matrix4 = Matrix4().idt()
    override val propertyContainer: PropertyContainer = DefaultPropertyContainer()
    override fun isRendered(shader: GraphShader, camera: Camera): Boolean {
        return true
    }

    override fun render(camera: Camera, shaderProgram: ShaderProgram, propertyToLocationMapping: (String) -> Int) {
        mesh.render(shaderProgram, GL20.GL_TRIANGLES)
    }
}