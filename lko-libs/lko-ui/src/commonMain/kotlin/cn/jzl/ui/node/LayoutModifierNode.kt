package cn.jzl.ui.node

import cn.jzl.ui.Measurable
import cn.jzl.ui.MeasureResult
import cn.jzl.ui.MeasureScope

interface LayoutModifierNode : DelegatableNode {
    fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult
}