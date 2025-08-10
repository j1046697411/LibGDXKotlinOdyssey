package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNodeInputSide
import cn.jzl.graph.impl.NamedGraphNodeInput

interface GraphNodeEditorInput : GraphNodeEditorIO {
    val graphNodeInput: NamedGraphNodeInput
    override val fieldId: String get() = graphNodeInput.fieldId
    override val fieldName: String get() = graphNodeInput.fieldName
    val side: GraphNodeInputSide get() = graphNodeInput.side
}