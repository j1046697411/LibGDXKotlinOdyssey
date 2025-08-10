package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNode

interface GraphNodeEditorFactory {
    fun createGraphNodeEditor(graphEditor: GraphEditor, graphNode: GraphNode): GraphNodeEditor
}