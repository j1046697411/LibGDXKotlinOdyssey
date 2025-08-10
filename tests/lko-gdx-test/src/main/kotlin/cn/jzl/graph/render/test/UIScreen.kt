package cn.jzl.graph.render.test

import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.world
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.GraphTypeResolver
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.common.PipelineNodeProducerContainer
import cn.jzl.graph.common.PipelineNodeProducerResolver
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.impl.DefaultGraphConnection
import cn.jzl.graph.impl.DefaultGraphNode
import cn.jzl.graph.impl.DefaultNodeGroup
import cn.jzl.graph.render.renderPipelineModule
import cn.jzl.graph.shader.shaderPipelineModule
import cn.jzl.graph.ui.graph.*
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.app.KtxScreen
import ktx.log.logger
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.vis.visTable

class UIScreen : KtxScreen {
    private val world = world {
        this.module(shaderPipelineModule())
        module(pipelineModule())
        module(renderPipelineModule())
        this bind singleton { new(::DefaultGraphNodeEditorFactory) }
    }

    private val stage by lazy { Stage(ScreenViewport()) }

    private val graphEditor by lazy { GraphEditor(world, "Model_Shader") }

    private val graphTypeResolver by world.instance<GraphTypeResolver>()
    private val pipelineNodeProducerContainer by world.instance<PipelineNodeProducerContainer>()

    override fun show() {
        super.show()
        stage.actors {
            visTable {
                actor(graphEditor).cell(grow = true)
            }.setSize(stage.width, stage.height)
        }

        val graphType = graphTypeResolver.resolve<PipelineNode>(graphEditor.type)
        pipelineNodeProducerContainer.getProducers(graphType).take(10).forEachIndexed { index, producer ->
            graphEditor.addGraphNode(DefaultGraphNode("index_$index", producer.configuration.type))
        }
        Gdx.input.inputProcessor = stage
        Gdx.app.logLevel = Application.LOG_DEBUG
    }

    override fun render(delta: Float) {
        super.render(delta)
        stage.act(delta)
        stage.draw()
    }

    companion object {
        private val log = logger<UIScreen>()
    }
}

class DefaultGraphNodeEditorFactory(world: World) : GraphNodeEditorFactory {

    private val pipelineNodeProducerResolver by world.instance<PipelineNodeProducerResolver>()
    private val graphTypeResolver by world.instance<GraphTypeResolver>()

    override fun createGraphNodeEditor(graphEditor: GraphEditor, graphNode: GraphNode): GraphNodeEditor {
        val graphType = graphTypeResolver.resolve<PipelineNode>(graphEditor.type)
        val pipelineRendererNodeProducer = pipelineNodeProducerResolver.resolve(graphType, graphNode)
        return DefaultGraphNodeEditor(graphEditor, graphNode, EditorNodeConfiguration(true, pipelineRendererNodeProducer.configuration))
    }
}