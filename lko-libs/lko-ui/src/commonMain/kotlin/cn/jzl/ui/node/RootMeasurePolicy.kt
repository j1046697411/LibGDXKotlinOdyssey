package cn.jzl.ui.node

import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.Measurable
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.MeasureResult
import cn.jzl.ui.MeasureScope
import kotlin.math.max

internal object RootMeasurePolicy : MeasurePolicy {

    override fun MeasureScope.measure(measurables: Sequence<Measurable>, constraints: Constraints): MeasureResult {
        val places = measurables.map { measurable -> measurable.measure(constraints) }
        val size = places.fold(IntSize.Companion.Zero) { acc, placeable ->
            IntSize.Companion(max(acc.width, placeable.size.width), max(acc.height, placeable.size.height))
        }
        return layout(constraints.constrain(size)) {
            places.forEach { placeable ->
                placeable.place(IntPoint2.Zero)
            }
        }
    }

}