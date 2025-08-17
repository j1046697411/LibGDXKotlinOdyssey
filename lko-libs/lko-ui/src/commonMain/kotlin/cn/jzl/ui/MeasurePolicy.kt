package cn.jzl.ui

interface MeasurePolicy {
    fun MeasureScore.measure(self: Measurable, measures: Sequence<Measurable>, constraints: Constraints): MeasureResult
}