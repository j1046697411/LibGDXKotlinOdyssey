package cn.jzl.ui.node

import androidx.compose.runtime.Stable
import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.lko.math.IntPoint2
import cn.jzl.ui.Measurable
import cn.jzl.ui.MeasureResult
import cn.jzl.ui.MeasureScope
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.unit.UIUnit
import kotlin.math.roundToInt

private data class FillNode(
    var direction: Int = DIRECTION_BOTH,
    var fraction: Float = 0f,
) : LayoutModifierNode, Component<FillNode>, ModifierNode() {

    override val type: ComponentType<FillNode> get() = FillNode

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val minWidth: Int
        val maxWidth: Int
        if (direction and DIRECTION_HORIZONTAL == DIRECTION_HORIZONTAL) {
            val width = constraints.constrainWidth((constraints.maxWidth * fraction).roundToInt())
            minWidth = width
            maxWidth = width
        } else {
            minWidth = constraints.minWidth
            maxWidth = constraints.maxWidth
        }
        val minHeight: Int
        val maxHeight: Int
        if (direction and DIRECTION_VERTICAL == DIRECTION_VERTICAL) {
            val height = constraints.constrainHeight((constraints.maxHeight * fraction).roundToInt())
            minHeight = height
            maxHeight = height
        } else {
            minHeight = constraints.minHeight
            maxHeight = constraints.maxHeight
        }
        val placeable = measurable.measure(Constraints(minWidth = minWidth, maxWidth = maxWidth, minHeight = minHeight, maxHeight = maxHeight))
        return layout(placeable.size) { placeable.place(IntPoint2.Zero) }
    }

    companion object : ComponentType<FillNode>() {
        const val DIRECTION_VERTICAL = 1 shl 0
        const val DIRECTION_HORIZONTAL = 1 shl 1
        const val DIRECTION_BOTH = DIRECTION_VERTICAL or DIRECTION_HORIZONTAL
    }
}

private data class FillElement(
    var direction: Int = FillNode.DIRECTION_BOTH,
    var fraction: Float = 0f,
) : ModifierNodeElement<FillNode> {
    override val nodeType: ComponentType<FillNode> get() = FillNode
    override fun create(): FillNode {
        return FillNode(direction, fraction)
    }

    override fun update(node: FillNode) {
        node.direction = direction
        node.fraction = fraction
    }

    companion object : ComponentType<FillElement>()
}

private data class SizeElement(
    private val minWidth: UIUnit = UIUnit.Auto,
    private val minHeight: UIUnit = UIUnit.Auto,
    private val maxWidth: UIUnit = UIUnit.Auto,
    private val maxHeight: UIUnit = UIUnit.Auto,
) : ModifierNodeElement<SizeNode> {
    override val nodeType: ComponentType<SizeNode> get() = SizeNode

    override fun create(): SizeNode {
        return SizeNode(minWidth, minHeight, maxWidth, maxHeight)
    }

    override fun update(node: SizeNode) {
        node.minWidth = minWidth
        node.minHeight = minHeight
        node.maxWidth = maxWidth
        node.maxHeight = maxHeight
    }
}

private data class SizeNode(
    var minWidth: UIUnit,
    var minHeight: UIUnit,
    var maxWidth: UIUnit,
    var maxHeight: UIUnit,
) : LayoutModifierNode, Component<SizeNode>, ModifierNode() {

    override val type: ComponentType<SizeNode> get() = SizeNode

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val constraintsMinWidth = if (minWidth == UIUnit.Auto) constraints.minWidth else minWidth.pixel(this, constraints.minWidth)
        val constraintsMinHeight = if (minHeight == UIUnit.Auto) constraints.minHeight else minHeight.pixel(this, constraints.minHeight)
        val constraintsMaxWidth = if (maxWidth == UIUnit.Auto) constraints.maxWidth else maxWidth.pixel(this, constraints.maxWidth)
        val constraintsMaxHeight = if (maxHeight == UIUnit.Auto) constraints.maxHeight else maxHeight.pixel(this, constraints.maxHeight)
        val newConstraints = Constraints(
            minWidth = constraintsMinWidth,
            maxWidth = constraintsMaxWidth,
            minHeight = constraintsMinHeight,
            maxHeight = constraintsMaxHeight,
        )
        val placeable = measurable.measure(newConstraints)
        return layout(placeable.size) { placeable.place(IntPoint2.Zero) }
    }

    companion object : ComponentType<SizeNode>()
}

@Stable
fun Modifier.size(size: UIUnit): Modifier = this + SizeElement(size, size, size, size)

@Stable
fun Modifier.size(width: UIUnit, height: UIUnit): Modifier = this + SizeElement(width, height, width, height)

@Stable
fun Modifier.height(height: UIUnit): Modifier = this + SizeElement(minHeight = height, maxHeight = height)

@Stable
fun Modifier.width(width: UIUnit): Modifier = this + SizeElement(minWidth = width, maxWidth = width)

@Stable
fun Modifier.widthIn(minWidth: UIUnit, maxWidth: UIUnit): Modifier = this + SizeElement(minWidth = minWidth, maxWidth = maxWidth)

@Stable
fun Modifier.heightIn(minHeight: UIUnit, maxHeight: UIUnit): Modifier = this + SizeElement(minHeight = minHeight, maxHeight = maxHeight)

@Stable
fun Modifier.sizeIn(
    minWidth: UIUnit,
    maxWidth: UIUnit,
    minHeight: UIUnit,
    maxHeight: UIUnit
): Modifier = this + SizeElement(minWidth = minWidth, maxWidth = maxWidth, minHeight = minHeight, maxHeight = maxHeight)

@Stable
fun Modifier.fillMaxSize(fraction: Float = 1f) = this + FillElement(FillNode.DIRECTION_BOTH, fraction)

@Stable
fun Modifier.fillMaxWidth(fraction: Float = 1f) = this + FillElement(FillNode.DIRECTION_HORIZONTAL, fraction)

@Stable
fun Modifier.fillMaxHeight(fraction: Float = 1f) = this + FillElement(FillNode.DIRECTION_VERTICAL, fraction)
