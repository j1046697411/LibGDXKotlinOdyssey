package cn.jzl.graph.ui.graph

import cn.jzl.graph.NodeGroup
import com.badlogic.gdx.math.Rectangle

class RectangleNodeGroup(group: NodeGroup) : NodeGroup by group {
    val rectangle = Rectangle()
}