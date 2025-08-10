package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNodeOutputSide
import cn.jzl.graph.impl.NamedGraphNodeOutput

interface GraphNodeEditorOutput : GraphNodeEditorIO {
    val graphNodeOutput: NamedGraphNodeOutput
    override val fieldId: String get() = graphNodeOutput.fieldId
    override val fieldName: String get() = graphNodeOutput.fieldName
    val side: GraphNodeOutputSide get() = graphNodeOutput.side
}