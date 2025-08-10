package cn.jzl.graph.ui.graph

import cn.jzl.graph.common.data.GraphProperty
import com.badlogic.gdx.scenes.scene2d.Actor

interface PropertyEditor : GraphProperty {
    val actor: Actor
}