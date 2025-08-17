package cn.jzl.ui.compose

import cn.jzl.ui.*
import kotlin.math.max

internal object RootMeasurePolicy : MeasurePolicy {
    private val defaultSize = IntSize(-1, -1)

    override fun MeasureScore.measure(self: Measurable, measures: Sequence<Measurable>, constraints: Constraints): MeasureResult {
        val results = measures.map { it.measure(constraints) }
        val size = results.fold(defaultSize) { acc, placeable ->
            IntSize(max(acc.width, placeable.size.width), max(acc.height, placeable.size.width))
        }
        return layout(
            size = if (defaultSize == size) IntSize(constraints.maxWidth, constraints.maxHeight) else constraints.constrain(size),
            alignmentLines = mapOf()
        ) {
            results.forEach { it.placeAt(Coordinate.ZERO) }
        }
    }
}