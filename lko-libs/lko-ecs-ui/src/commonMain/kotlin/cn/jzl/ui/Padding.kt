package cn.jzl.ui

import androidx.compose.runtime.Stable
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.node.LayoutModifierNode
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.node.ModifierNodeElement
import cn.jzl.ui.unit.UIUnit

internal data class PaddingNode(
    var start: UIUnit,
    var top: UIUnit,
    var end: UIUnit,
    var bottom: UIUnit,
) : LayoutModifierNode, ModifierNode() {

    override fun MeasureScope.measure(entity: Entity, measure: Measurable, constraints: Constraints): MeasureResult {
        val start = start.toPixel(constraints.maxWidth)
        val top = top.toPixel(constraints.maxHeight)
        val end = end.toPixel(constraints.maxWidth)
        val bottom = bottom.toPixel(constraints.maxHeight)
        val horizontal = start + end
        val vertical = top + bottom
        val newConstraints = constraints.copy(
            minWidth = (constraints.minWidth - horizontal).coerceIn(0, constraints.maxWidth),
            maxWidth = (constraints.maxWidth - horizontal).coerceIn(0, constraints.maxWidth),
            minHeight = (constraints.minHeight - vertical).coerceIn(0, constraints.maxHeight),
            maxHeight = (constraints.maxHeight - vertical).coerceIn(0, constraints.maxHeight)
        )
        val placeable = measure.measure(entity, newConstraints)
        val size = placeable.size + IntSize(horizontal, vertical)
        return layout(entity, size) { placeable.place(entity, IntPoint2(start, top)) }
    }
    companion object : ComponentType<PaddingNode>()
}

internal data class PaddingElement(
    val start: UIUnit = UIUnit.Auto,
    val top: UIUnit = UIUnit.Auto,
    val end: UIUnit = UIUnit.Auto,
    val bottom: UIUnit = UIUnit.Auto,
) : ModifierNodeElement<PaddingNode> {

    override val nodeType: ComponentType<PaddingNode> get() = PaddingNode

    override fun create(): PaddingNode {
        return PaddingNode(start, top, end, bottom)
    }

    override fun update(node: PaddingNode) {
        node.start = start
        node.top = top
        node.end = end
        node.bottom = bottom
    }
}


@Stable
fun Modifier.padding(padding: UIUnit) : Modifier = this + PaddingElement(padding, padding, padding, padding)

@Stable
fun Modifier.padding(horizontal: UIUnit = UIUnit.Auto, vertical: UIUnit = UIUnit.Auto) : Modifier = this + PaddingElement(horizontal, vertical, horizontal, vertical)

@Stable
fun Modifier.padding(start: UIUnit, top: UIUnit, end: UIUnit, bottom: UIUnit) : Modifier = this + PaddingElement(start, top, end, bottom)
