package cn.jzl.ui

import cn.jzl.ui.node.Constraints

interface MeasurePolicy {
    fun MeasureScope.measure(measurables: Sequence<Measurable>, constraints: Constraints): MeasureResult
}