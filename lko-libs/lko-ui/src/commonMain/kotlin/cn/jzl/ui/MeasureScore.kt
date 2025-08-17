package cn.jzl.ui

interface MeasureScore {
    fun layout(
        size: IntSize,
        alignmentLines: Map<AlignmentLine, Int>,
        placementBlock: PlacementScope.() -> Unit
    ): MeasureResult
}