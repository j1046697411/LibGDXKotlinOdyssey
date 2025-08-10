package cn.jzl.graph.ui.graph

import com.badlogic.gdx.scenes.scene2d.utils.Drawable

interface GraphNodeEditorIO {
    val offset: Float
    val fieldId: String
    val fieldName: String
    fun getConnectorDrawable(valid: Boolean): Drawable
}