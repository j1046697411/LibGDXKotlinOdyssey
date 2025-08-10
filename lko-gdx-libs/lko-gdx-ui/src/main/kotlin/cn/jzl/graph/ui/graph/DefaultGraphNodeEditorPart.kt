package cn.jzl.graph.ui.graph

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import ktx.scene2d.KTable
import ktx.scene2d.vis.visLabel

class DefaultGraphNodeEditorPart(
    override val input: GraphNodeEditorInput?,
    override val output: GraphNodeEditorOutput?
) : VisTable(false), GraphNodeEditorPart, KTable {

    init {
        var actor: Actor? = null
        if (input != null) {
            actor = visLabel(input.fieldName, "default").cell(growX = true)
        }
        if (output != null) {
            actor = visLabel(output.fieldName, "default"){
                setAlignment(Align.right)
            }.cell(growX = actor == null)
        }
        actor?.cell(row = true)
    }
    override val actor: Actor get() = this
}