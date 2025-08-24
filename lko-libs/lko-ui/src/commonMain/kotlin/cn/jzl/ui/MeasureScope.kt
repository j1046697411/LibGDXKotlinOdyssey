package cn.jzl.ui

import cn.jzl.lko.math.IntSize
import cn.jzl.ui.unit.Density

interface MeasureScope : Density {
    fun layout(
        size: IntSize,
        alignmentLines: Map<AlignmentLine, Int> = mapOf(),
        placementBlock: PlacementScope.()-> Unit
    ): MeasureResult
}