package cn.jzl.graph.ui.graph

import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeInputSide
import cn.jzl.graph.GraphNodeOutputSide
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import ktx.scene2d.actor
import ktx.scene2d.vis.KVisTable
import java.lang.Integer.max

class DefaultGraphNodeEditor(
    private val graphEditor: GraphEditor,
    private val graphNode: GraphNode,
    override val configuration: EditorNodeConfiguration,
) : GraphNodeEditor, GraphNode by graphNode {

    private val container = KVisTable(false)
    override val graphNodeEditorParts = mutableListOf<GraphNodeEditorPart>()

    override val actor: Actor get() = container

    override val inputs: Map<String, GraphNodeEditorInput> = configuration.nodeInputs.associate {
        it.fieldId to DefaultGraphNodeEditorInput(graphEditor, this, it)
    }
    override val outputs: Map<String, GraphNodeEditorOutput> = configuration.nodeOutputs.associate {
        it.fieldId to DefaultGraphNodeEditorOutput(graphEditor,this, it)
    }
    override val type: String get() = configuration.type
    override val payloads: MutableMap<String, Any> = mutableMapOf()

    init {
        container.align(Align.top)
        val inputs = configuration.nodeInputs.filter { it.side == GraphNodeInputSide.Left }
        val outputs = configuration.nodeOutputs.filter { it.side == GraphNodeOutputSide.Right }
        for (index in 0 until max(inputs.size, outputs.size)) {
            val input = inputs.getOrNull(index)?.let { this.inputs[it.fieldId] }
            val output = outputs.getOrNull(index)?.let { this.outputs[it.fieldId] }
            addGraphNodeEditorPart(DefaultGraphNodeEditorPart(input, output))
        }
    }

    override fun payload(key: String, value: Any) {
        payloads[key] = value
    }

    fun addGraphNodeEditorPart(graphNodeEditorPart: GraphNodeEditorPart) {
        with(container) { actor(graphNodeEditorPart.actor).cell(growX = true, row = true) }
        this.graphNodeEditorParts.add(graphNodeEditorPart)
    }
}


