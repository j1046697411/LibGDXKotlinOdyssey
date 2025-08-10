package cn.jzl.graph.ui.graph

import com.badlogic.gdx.scenes.scene2d.Actor

interface GraphNodeEditorPart {
    val input: GraphNodeEditorInput?
    val output: GraphNodeEditorOutput?
    val actor: Actor
}

