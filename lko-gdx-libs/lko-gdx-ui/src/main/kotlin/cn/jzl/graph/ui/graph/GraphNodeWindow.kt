package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.scene2d.KTable
import ktx.scene2d.actor
import kotlin.math.max

class  GraphNodeWindow(
    private val graphEditor: GraphEditor,
    val graphNodeEditor: GraphNodeEditor
) : VisWindow(graphNodeEditor.configuration.name), KTable, GraphNode by graphNodeEditor {

    init {
        val configuration = graphNodeEditor.configuration
        titleLabel.setAlignment(Align.center)
        setKeepWithinStage(false)
        sizeBy(max(150f, prefWidth), prefHeight)
        actor(graphNodeEditor.actor).cell(grow = true)
        if (configuration.closeable) {
            addCloseButton()
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        graphNodeEditor.graphNodeEditorParts.forEach { graphNodeEditorPart ->
            val input = graphNodeEditorPart.input
            if (input != null) {
                val drawable = input.getConnectorDrawable(true)
                val x = x - drawable.minWidth
                val y = y + graphNodeEditorPart.actor.y + (graphNodeEditorPart.actor.height - drawable.minHeight) / 2f
                drawable.draw(batch, x, y, drawable.minWidth, drawable.minHeight)
            }
            val output = graphNodeEditorPart.output
            if (output != null) {
                val drawable = output.getConnectorDrawable(false)
                val y = y + graphNodeEditorPart.actor.y + (graphNodeEditorPart.actor.height - drawable.minHeight) / 2f
                val x = x + width
                drawable.draw(batch, x, y, drawable.minWidth, drawable.minHeight)
            }
        }
    }
}