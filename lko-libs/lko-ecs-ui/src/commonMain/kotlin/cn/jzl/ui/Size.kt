package cn.jzl.ui

import androidx.compose.runtime.Stable
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.node.LayoutModifierNode
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.node.ModifierNodeElement
import cn.jzl.ui.unit.UIUnit
import kotlin.math.roundToInt

internal const val DIRECTION_HORIZONTAL = 1 shl 0
internal const val DIRECTION_VERTICAL = 1 shl 1
internal const val DIRECTION_BOTH = DIRECTION_VERTICAL or DIRECTION_HORIZONTAL

internal data class FillSize(
    var direction: Int,
    var fraction: Float
) : LayoutModifierNode, ModifierNode() {

    override fun MeasureScope.measure(entity: Entity, measure: Measurable, constraints: Constraints): MeasureResult {
        val minWidth: Int
        val maxWidth: Int
        val minHeight: Int
        val maxHeight: Int
        if (direction and DIRECTION_HORIZONTAL == DIRECTION_HORIZONTAL) {
            val width = (constraints.maxWidth * fraction).roundToInt()
            minWidth = width
            maxWidth = width
        } else {
            minWidth = constraints.minWidth
            maxWidth = constraints.maxWidth
        }
        if (direction and DIRECTION_VERTICAL == DIRECTION_VERTICAL) {
            val height = (constraints.maxHeight * fraction).roundToInt()
            minHeight = height
            maxHeight = height
        } else {
            minHeight = constraints.minHeight
            maxHeight = constraints.maxHeight
        }
        val newConstraints = Constraints(minWidth, maxWidth, minHeight, maxHeight)
        val placeable = measure.measure(entity, newConstraints.constrain(constraints))
        return layout(entity, placeable.size) {
            placeable.place(entity, IntPoint2.Zero)
        }
    }

    companion object : ComponentType<FillSize>()
}

internal class FillElement(
    val direction: Int,
    val fraction: Float
) : ModifierNodeElement<FillSize> {

    override val nodeType: ComponentType<FillSize> get() = FillSize

    override fun create(): FillSize {
        return FillSize(direction, fraction)
    }

    override fun update(node: FillSize) {
        node.direction = direction
        node.fraction = fraction
    }
}

internal data class SizeNode(
    var minWidth: UIUnit,
    var maxWidth: UIUnit,
    var minHeight: UIUnit,
    var maxHeight: UIUnit
) : LayoutModifierNode, ModifierNode() {

    override fun MeasureScope.measure(entity: Entity, measure: Measurable, constraints: Constraints): MeasureResult {
        val minWidth = if (minWidth == UIUnit.Auto) constraints.minWidth else maxWidth.toPixel(constraints.minWidth)
        val maxWidth = if (maxWidth == UIUnit.Auto) constraints.maxWidth else maxWidth.toPixel(constraints.maxWidth)
        val minHeight = if (minHeight == UIUnit.Auto) constraints.minHeight else minHeight.toPixel(constraints.minHeight)
        val maxHeight = if (maxHeight == UIUnit.Auto) constraints.maxHeight else maxHeight.toPixel(constraints.maxHeight)
        val newConstraints = Constraints(minWidth, maxWidth, minHeight, maxHeight)
        val placeable = measure.measure(entity, newConstraints.constrain(constraints))
        return layout(entity, placeable.size) {
            placeable.place(entity, IntPoint2.Zero)
        }
    }

    companion object : ComponentType<SizeNode>()
}

internal data class SizeElement(
    val minWidth: UIUnit,
    val maxWidth: UIUnit,
    val minHeight: UIUnit,
    val maxHeight: UIUnit
) : ModifierNodeElement<SizeNode> {

    override val nodeType: ComponentType<SizeNode> get() = SizeNode

    override fun create(): SizeNode {
        return SizeNode(minWidth, maxWidth, minHeight, maxHeight)
    }

    override fun update(node: SizeNode) {
        node.minWidth = minWidth
        node.maxWidth = maxWidth
        node.minHeight = minHeight
        node.maxHeight = maxHeight
    }
}

@Stable
fun Modifier.fillMaxSize(fraction: Float = 1f) = this + FillElement(DIRECTION_BOTH, fraction)

@Stable
fun Modifier.fillMaxWidth(fraction: Float = 1f) = this + FillElement(DIRECTION_HORIZONTAL, fraction)

@Stable
fun Modifier.fillMaxHeight(fraction: Float = 1f) = this + FillElement(DIRECTION_VERTICAL, fraction)

@Stable
fun Modifier.size(size: UIUnit) = this + SizeElement(size, size, size, size)

@Stable
fun Modifier.size(width: UIUnit = UIUnit.Auto, height: UIUnit = UIUnit.Auto) = this + SizeElement(width, width, height, height)

@Stable
fun Modifier.width(width: UIUnit = UIUnit.Auto) = this + SizeElement(width, width, UIUnit.Auto, UIUnit.Auto)

@Stable
fun Modifier.height(height: UIUnit) = this + SizeElement(UIUnit.Auto, UIUnit.Auto, height, height)

@Stable
fun Modifier.sizeIn(
    minWidth: UIUnit = UIUnit.Auto,
    maxWidth: UIUnit = UIUnit.Auto,
    minHeight: UIUnit = UIUnit.Auto,
    maxHeight: UIUnit = UIUnit.Auto
) = this + SizeElement(minWidth, maxWidth, minHeight, maxHeight)

@Stable
fun Modifier.widthIn(minWidth: UIUnit = UIUnit.Auto, maxWidth: UIUnit = UIUnit.Auto) = this + SizeElement(minWidth, maxWidth, UIUnit.Auto, UIUnit.Auto)

@Stable
fun Modifier.heightIn(minHeight: UIUnit = UIUnit.Auto, maxHeight: UIUnit = UIUnit.Auto) = this + SizeElement(UIUnit.Auto, UIUnit.Auto, minHeight, maxHeight)
