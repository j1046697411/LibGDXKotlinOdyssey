package cn.jzl.graph.ui.graph

import cn.jzl.graph.impl.NamedGraphNodeInput
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class DefaultGraphNodeEditorInput(
    private val graphEditor: GraphEditor,
    private val graphNodeEditor: GraphNodeEditor,
    override val graphNodeInput: NamedGraphNodeInput
) : GraphNodeEditorInput {

    override val offset: Float get() = TODO("Not yet implemented")

    override fun getConnectorDrawable(valid: Boolean): Drawable {
        return graphEditor.getConnectorDrawable(graphNodeEditor, this, valid)
    }
}