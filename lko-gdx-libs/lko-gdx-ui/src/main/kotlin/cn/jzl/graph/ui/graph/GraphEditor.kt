package cn.jzl.graph.ui.graph

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.*
import cn.jzl.graph.common.data.GraphWithProperties
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import ktx.log.logger
import ktx.scene2d.KTable
import ktx.scene2d.KVerticalGroup
import ktx.scene2d.label
import ktx.scene2d.vis.visScrollPane
import ktx.scene2d.vis.visSplitPane
import ktx.scene2d.vis.visTable
import ktx.scene2d.vis.visTextButton
import kotlin.math.max
import kotlin.math.min

class GraphEditor(world: World, override val type: String) : VisTable(), GraphWithProperties, MutableGraph, KTable {

    private val graphNodeEditorFactory by world.instance<GraphNodeEditorFactory>()

    private val graphNodes = mutableMapOf<String, GraphNodeWindow>()
    private val graphConnections = mutableSetOf<GraphConnection>()
    private val nodeGroups = mutableMapOf<String, RectangleNodeGroup>()
    private val propertyEditors = mutableMapOf<String, PropertyEditor>()

    private val shapeRenderer = ShapeRenderer()

    private val graphRoundedBackground by lazy { skin.getDrawable("graph-rounded-background") }
    private val bitmapFont by lazy { skin.getFont("default-font") }

    private val tem1 = Vector2()
    private val tem2 = Vector2()

    override val nodes: Sequence<GraphNodeWindow> = graphNodes.values.asSequence()
    override val connections: Sequence<GraphConnection> = graphConnections.asSequence()
    override val groups: Sequence<RectangleNodeGroup> = nodeGroups.values.asSequence()
    override val properties: Sequence<PropertyEditor> = propertyEditors.values.asSequence()

    private val container = object : VisTable(false), KTable {

        override fun draw(batch: Batch, parentAlpha: Float) {
            super.draw(batch, parentAlpha)
            batch.end()
            val shapeRenderer = this@GraphEditor.shapeRenderer
            shapeRenderer.projectionMatrix = batch.projectionMatrix
            shapeRenderer.transformMatrix = batch.transformMatrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            this@GraphEditor.connections.forEach(this@GraphEditor::drawConnection)
            shapeRenderer.end()
            batch.begin()
        }

        override fun drawBackground(batch: Batch, parentAlpha: Float, x: Float, y: Float) {
            super.drawBackground(batch, parentAlpha, x, y)
            this@GraphEditor.groups.forEach { group -> this@GraphEditor.drawNodeGroup(batch, group, x, y) }
            this@GraphEditor.groups.forEach { group -> this@GraphEditor.drawNodeGroupName(batch, group, x, y) }
        }
    }
    private val propertyContainer = KVerticalGroup()

    init {
        visSplitPane {
            setMinSplitAmount(0.05f)
            setMaxSplitAmount(0.2f)
            setSplitAmount(0.2f)
            visTable {
                align(Align.top)
                visTable {
                    label("Properties").cell(growX = true)
                    visTextButton("add")
                }.cell(growX = true, row = true)
                visScrollPane {
                    fadeScrollBars = false
                    addActor(this@GraphEditor.propertyContainer)
                }.cell(grow = true)
            }
            addActor(this@GraphEditor.container)
        }.cell(grow = true, row = true)
    }


    override fun act(delta: Float) {
        super.act(delta)
        groups.forEach(::actNodeGroup)
    }

    private fun actNodeGroup(group: RectangleNodeGroup) : Unit = with(container) {
        val tem1 = this@GraphEditor.tem1
        val tem2 = this@GraphEditor.tem2
        group.forEachIndexed { index, nodeId ->
            val window = this@GraphEditor.graphNodes.getValue(nodeId)
            if (index == 0) {
                tem1.set(window.x, window.y)
                tem2.set(tem1).add(window.width, window.height)
            } else {
                tem1.set(min(tem1.x, window.x), min(tem1.y, window.y))
                tem2.set(max(tem2.x, window.x + window.width), max(tem2.y, window.y + window.height))
            }
        }
        tem1.sub(10f, 10f)
        tem2.add(10f, 10f + this@GraphEditor.bitmapFont.lineHeight)
        group.rectangle.set(tem1.x, tem1.y, tem2.x - tem1.x, tem2.y - tem1.y)
    }

    private fun drawNodeGroupName(batch: Batch, group: RectangleNodeGroup, x: Float, y: Float) {
        val rectangle = group.rectangle
        bitmapFont.draw(
            batch,
            group.name,
            x + rectangle.x + 4f,
            y + rectangle.y + rectangle.height - 4f,
            0,
            group.name.length,
            rectangle.width - 8,
            Align.center,
            false
        )
    }

    private fun drawNodeGroup(batch: Batch, nodeGroup: RectangleNodeGroup, x: Float, y: Float) {
        val rectangle = nodeGroup.rectangle
        graphRoundedBackground.draw(batch, rectangle.x + x, rectangle.y + y, rectangle.width, rectangle.height)
    }

    private fun drawConnection(connection: GraphConnection): Unit = with(container) {
        val tem1 = this@GraphEditor.tem1
        val tem2 = this@GraphEditor.tem2
        val fromWindow = this@GraphEditor.graphNodes.getValue(connection.nodeFrom)
        val toWindow = this@GraphEditor.graphNodes.getValue(connection.nodeTo)
        val fromNode = fromWindow.graphNodeEditor
        val output = fromNode.graphNodeEditorParts.firstOrNull { it.output?.fieldId == connection.fieldFrom }
        val toNode = toWindow.graphNodeEditor
        val input = toNode.graphNodeEditorParts.firstOrNull { it.input?.fieldId == connection.fieldTo }
        if (output != null && input != null) {
            val outputWidth = output.output?.getConnectorDrawable(true)?.let { it.minWidth / 2f } ?: 0f
            val inputWidth = input.input?.getConnectorDrawable(true)?.let { it.minWidth / 2f } ?: 0f
            tem1.set(output.actor.x, output.actor.y).add(fromWindow.width + outputWidth, output.actor.height / 2f)
            tem2.set(input.actor.x, input.actor.y).add(-inputWidth, input.actor.height / 2f)
            fromWindow.localToActorCoordinates(this, tem1).add(x, y)
            toWindow.localToActorCoordinates(this, tem2).add(x, y)
            this@GraphEditor.shapeRenderer.curve(tem1.x, tem1.y, (tem1.x + tem2.x) / 2f, tem1.y, (tem1.x + tem2.x) / 2f, tem2.y, tem2.x, tem2.y, 100)
        }
    }

    override fun addGraphNode(graphNode: GraphNode) {
        val graphNodeEditor = graphNodes.getOrPut(graphNode.id) {
            val graphNodeEditor = graphNodeEditorFactory.createGraphNodeEditor(this, graphNode)
            val graphNodeWindow = GraphNodeWindow(this, graphNodeEditor)
            container.addActor(graphNodeWindow)
            graphNodeWindow
        }
        graphNodes[graphNode.id] = graphNodeEditor
    }

    override fun removeGraphNode(graphNode: GraphNode) {
        val graphNodeEditor = graphNodes.remove(graphNode.id)
        if (graphNodeEditor != null) {
            removeActor(graphNodeEditor)
        }
    }

    override fun addGraphConnection(graphConnection: GraphConnection) {
        log.debug { "addConnection from ${graphConnection.nodeFrom} to ${graphConnection.nodeTo}" }
        graphConnections.add(graphConnection)
    }

    override fun removeGraphConnection(graphConnection: GraphConnection) {
        graphConnections.remove(graphConnection)
    }

    override fun addNodeGroup(nodeGroup: NodeGroup) {
        nodeGroups[nodeGroup.name] = RectangleNodeGroup(nodeGroup)
    }

    override fun removeNodeGroup(nodeGroup: NodeGroup) {
        nodeGroups.remove(nodeGroup.name)
    }

    fun getConnectorDrawable(graphNodeEditor: GraphNodeEditor, input: GraphNodeEditorInput, valid: Boolean = true): Drawable {
        val required = input.graphNodeInput.required
        val name = buildString {
            val prefix = when (input.side) {
                GraphNodeInputSide.Top -> "connector-top"
                GraphNodeInputSide.Left -> "connector-left"
            }
            append(prefix)
            if (required) append("-required")
            if (!valid) append("-invalid")
        }
        return skin.getDrawable(name)
    }

    fun getConnectorDrawable(graphNodeEditor: GraphNodeEditor, output: GraphNodeEditorOutput, valid: Boolean = true): Drawable {
        val required = output.graphNodeOutput.required
        val name = buildString {
            val prefix = when (output.side) {
                GraphNodeOutputSide.Right -> "connector-right"
                GraphNodeOutputSide.Bottom -> "connector-bottom"
            }
            append(prefix)
            if (required) append("-required")
            if (!valid) append("-invalid")
        }
        return skin.getDrawable(name)
    }

    override fun getNodeById(nodeId: String): GraphNodeEditor? {
        return graphNodes[nodeId]?.graphNodeEditor
    }

    companion object {
        private val log = logger<GraphEditor>()
    }
}
