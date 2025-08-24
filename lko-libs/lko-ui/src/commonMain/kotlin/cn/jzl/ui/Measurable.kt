package cn.jzl.ui

import cn.jzl.ui.node.Constraints

interface Measurable {
    fun measure(constraints: Constraints): Placeable
}