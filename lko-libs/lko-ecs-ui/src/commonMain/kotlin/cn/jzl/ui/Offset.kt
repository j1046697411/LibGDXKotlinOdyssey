package cn.jzl.ui

import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.node.LayoutModifierNode
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.node.ModifierNodeElement
import cn.jzl.ui.unit.UIUnit

internal data class OffsetNode(
    var offsetX: UIUnit,
    var offsetY: UIUnit
) : LayoutModifierNode, ModifierNode() {

    override fun MeasureScope.measure(entity: Entity, measure: Measurable, constraints: Constraints): MeasureResult {
        val placeable = measure.measure(entity,constraints)
        return layout(entity, placeable.size) {
            placeable.place(entity, IntPoint2(offsetX.toPixel(constraints.maxWidth), constraints.maxHeight))
        }
    }

    companion object : ComponentType<OffsetNode>()
}

internal data class OffsetElement(
    var offsetX: UIUnit,
    var offsetY: UIUnit
) : ModifierNodeElement<OffsetNode> {

    override val nodeType: ComponentType<OffsetNode> get() = OffsetNode
    override fun create(): OffsetNode = OffsetNode(offsetX, offsetY)
    override fun update(node: OffsetNode) {
        node.offsetX = offsetX
        node.offsetY = offsetY
    }
}


fun Modifier.offset(offsetX: UIUnit, offsetY: UIUnit): Modifier = this + OffsetElement(offsetX, offsetY)
