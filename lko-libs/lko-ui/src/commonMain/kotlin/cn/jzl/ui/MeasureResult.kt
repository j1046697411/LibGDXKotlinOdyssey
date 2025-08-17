package cn.jzl.ui

interface MeasureResult {
    val size: IntSize

    val alignmentLines: Map<AlignmentLine, Int>

    fun placeChildren()
}