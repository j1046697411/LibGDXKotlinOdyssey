package cn.jzl.ui

import cn.jzl.lko.math.IntSize

interface MeasureResult {
    val size: IntSize
    val alignmentLines: Map<AlignmentLine, Int>
    fun placeChildren()
}