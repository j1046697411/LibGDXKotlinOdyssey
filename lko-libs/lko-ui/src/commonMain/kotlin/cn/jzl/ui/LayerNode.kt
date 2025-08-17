package cn.jzl.ui

import cn.jzl.ui.style.StyleSheet

interface LayerNode {
    val parentLayoutCoordinates: LayoutCoordinates?
    val nodeCoordinates: LayoutCoordinates
    val styleSheet: StyleSheet
    val measurePolicy: MeasurePolicy
    val measures: Sequence<Measurable>
}