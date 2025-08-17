package cn.jzl.ui

import cn.jzl.ui.style.StyleSheet

interface Measurable {

    val styleSheet: StyleSheet

    fun measure(constraints: Constraints): Placeable

    operator fun get(alignmentLine: AlignmentLine): Int
}
