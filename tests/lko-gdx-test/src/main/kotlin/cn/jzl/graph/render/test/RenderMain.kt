package cn.jzl.graph.render.test

import cn.jzl.di.instance
import cn.jzl.ecs.world
import cn.jzl.graph.common.config.DefaultGraphPipelineConfiguration
import cn.jzl.graph.common.data.DefaultGraphWithProperties
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.common.time.DefaultTimeKeeper
import cn.jzl.graph.impl.DefaultGraphConnection
import cn.jzl.graph.impl.DefaultGraphNode
import cn.jzl.graph.render.PipelineRendererLoader
import cn.jzl.graph.render.RenderOutput
import cn.jzl.graph.render.renderPipelineModule
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.math.Vector2
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
    }
    private val timeProvider = DefaultTimeKeeper()
    private val pipelineRendererLoader by world.instance<PipelineRendererLoader>()

    private val graph by lazy {
        val graph = DefaultGraphWithProperties("Render_Pipeline")
        graph.addGraphNode(DefaultGraphNode("Start", "PipelineStart"))

        graph.addGraphNode(DefaultGraphNode("background", "constant", hashMapOf("constant" to Color.WHITE)))

        graph.addGraphNode(DefaultGraphNode("background1", "constant", hashMapOf("constant" to Color.BLACK)))
        graph.addGraphNode(DefaultGraphNode("position", "constant", hashMapOf("constant" to Vector2(100f, 100f))))
        graph.addGraphNode(DefaultGraphNode("size", "constant", hashMapOf("constant" to Vector2(200f, 200f))))

        graph.addGraphNode(DefaultGraphNode("time", "time"))
        graph.addGraphNode(DefaultGraphNode("times", "times"))

        graph.addGraphNode(DefaultGraphNode("Start1", "PipelineStart"))
        graph.addGraphNode(DefaultGraphNode("PipelineRenderer", "PipelineRenderer"))

        graph.addGraphNode(DefaultGraphNode("End", "end"))

        graph.addGraphConnection(DefaultGraphConnection("Start", "output", "PipelineRenderer", "input"))
        graph.addGraphConnection(DefaultGraphConnection("Start1", "output", "PipelineRenderer", "pipeline"))
        graph.addGraphConnection(DefaultGraphConnection("background1", "output", "Start1", "background"))
        graph.addGraphConnection(DefaultGraphConnection("PipelineRenderer", "output", "End", "input"))
        graph.addGraphConnection(DefaultGraphConnection("position", "output", "PipelineRenderer", "position"))
        graph.addGraphConnection(DefaultGraphConnection("size", "output", "PipelineRenderer", "size"))

        graph.addGraphConnection(DefaultGraphConnection("background", "output", "times", "inputs"))
        graph.addGraphConnection(DefaultGraphConnection("time", "sinTime", "times", "inputs"))

        graph.addGraphConnection(DefaultGraphConnection("times", "output", "Start", "background"))

        graph.addGraphConnection(DefaultGraphConnection("PipelineRenderer", "output", "End", "input"))

        graph
    }

    private val pipelineRenderer by lazy {
        val configuration = DefaultGraphPipelineConfiguration(timeProvider)
        pipelineRendererLoader.loadPipelineRenderer(graph, configuration, "End")
    }
    private val stage by lazy { Stage(ScreenViewport()) }

    private lateinit var profiler: GLProfiler
    private lateinit var debugInfo: Label
    override fun show() {
        super.show()
        Gdx.app.logLevel = Application.LOG_DEBUG
        this.profiler = GLProfiler(Gdx.app.graphics)
        VisUI.load(VisUI.SkinScale.X2)
        stage.actors {
            verticalGroup {
                setFillParent(true)
                align(Align.topRight)
                debugInfo = visLabel("Hello World")
            }
        }
        stage.isDebugAll = true
    }

    override fun render(delta: Float) {
        if (!profiler.isEnabled) {
            profiler.enable()
        }
        timeProvider.update(delta.toDouble().toDuration(DurationUnit.SECONDS))
        pipelineRenderer.render(RenderOutput)
        if (profiler.isEnabled) {
            debugInfo.setText("""
                Time: ${timeProvider.time.toString(DurationUnit.SECONDS, 2)}
                GL Calls: ${profiler.calls}
                Draw calls: ${profiler.drawCalls}
                Shader switches: ${profiler.shaderSwitches}
                Texture bindings: ${profiler.textureBindings}
                Vertex calls: ${profiler.vertexCount.total}
                fps: ${Gdx.graphics.framesPerSecond}
                memory: ${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)} MB
            """.trimIndent())
            stage.act(delta)
            stage.draw()
            profiler.reset()
        }
    }

    companion object {
        private val log = logger<RenderScreen>()
    }
}
