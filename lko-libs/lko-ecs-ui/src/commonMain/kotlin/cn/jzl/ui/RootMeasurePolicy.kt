package cn.jzl.ui

import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize

internal data object RootMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(entity: Entity, children: Sequence<Entity>, constraints: Constraints): MeasureResult {
        val places = children.map { it.measurable.measure(it, constraints) }.toList()
        return if (places.isEmpty()) {
            layout(entity, IntSize.Companion(constraints.minWidth, constraints.minHeight)) {
            }
        } else {
            layout(entity, IntSize.Companion(constraints.maxWidth, constraints.maxWidth)) {
                places.forEach { placeable -> placeable.place(entity, IntPoint2.Zero) }
            }
        }
    }
}