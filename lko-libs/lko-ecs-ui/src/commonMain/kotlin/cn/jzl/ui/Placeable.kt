package cn.jzl.ui

import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityComponentContext
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.unit.UIUnitScope

interface EntityHierarchyContext : EntityComponentContext {
    val Entity.children: Sequence<Entity>
    val Entity.parent: Entity?
}

interface AlignmentLine

interface Placeable {

    val size: IntSize

    fun place(entity: Entity, position: IntPoint2)
}

interface MeasureResult {
    val size: IntSize
    val alignmentLines: Map<AlignmentLine, Int>
    fun placeChildren()
}

interface PlacementScope

interface MeasureScope : EntityHierarchyContext, UIUnitScope {

    val Entity.measurable: Measurable

    fun layout(
        entity: Entity,
        size: IntSize,
        alignmentLines: Map<AlignmentLine, Int> = mapOf(),
        placementBlock: PlacementScope.() -> Unit
    ): MeasureResult
}

interface Measurable {
    fun measure(entity: Entity, constraints: Constraints): Placeable
}

interface MeasurePolicy {
    fun MeasureScope.measure(entity: Entity, children: Sequence<Entity>, constraints: Constraints): MeasureResult
}
