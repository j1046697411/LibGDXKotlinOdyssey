package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNode
import com.badlogic.gdx.scenes.scene2d.Actor

interface GraphNodeEditor : GraphNode, PipelineNode {

    val configuration: EditorNodeConfiguration

    val actor: Actor

    val inputs: Map<String, GraphNodeEditorInput>

    val outputs: Map<String, GraphNodeEditorOutput>

    val graphNodeEditorParts: List<GraphNodeEditorPart>

    fun payload(key: String, value: Any)
}