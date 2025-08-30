package cn.jzl.ui.flexbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.fromLowHigh
import cn.jzl.lko.high
import cn.jzl.lko.low
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.*
import cn.jzl.ui.modifier.Merge
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.node.ComposeUiLayout
import cn.jzl.ui.node.ComposeUiLayoutNode
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.node.ModifierNodeElement
import cn.jzl.ui.unit.UIUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

internal const val FLAG_DIRECTION_ROW = 1 shl 0
internal const val FLAG_DIRECTION_COLUMN = 1 shl 1
internal const val FLAG_REVERSE = 1 shl 20
internal const val FLAG_WRAP = 1 shl 3

enum class FlexDirection(internal val flags: Int) {
    Row(FLAG_DIRECTION_ROW),
    RowReverse(FLAG_DIRECTION_ROW or FLAG_REVERSE),
    Column(FLAG_DIRECTION_COLUMN),
    ColumnReverse(FLAG_DIRECTION_COLUMN or FLAG_REVERSE)
}

enum class FlexWrap(internal val flags: Int) {
    Nowrap(0), Wrap(FLAG_WRAP), WrapReverse(FLAG_WRAP or FLAG_REVERSE)
}

enum class JustifyContent {
    Start, End, Center, Between, Around
}

enum class AlignItems {
    Stretch, Start, End, Center, Baseline
}

enum class AlignContent {
    Stretch, Start, End, Center, Between, Around
}

enum class AlignSelf(internal val alignItems: AlignItems?) {
    Auto(null),
    Start(AlignItems.Start),
    End(AlignItems.End),
    Center(AlignItems.Center),
    Baseline(AlignItems.Baseline),
    Stretch(AlignItems.Stretch)
}

internal data class FlexItemNode(
    var order: Int = 0,
    var flexGrow: Float = 0f,
    var flexShrink: Float = 1f,
    var flexBasis: UIUnit = UIUnit.Auto,
    var alignSelf: AlignSelf = AlignSelf.Auto
) : ModifierNode() {
    companion object : ComponentType<FlexItemNode>()
}

@JvmInline
internal value class OrderElement(private val order: Int) : ModifierNodeElement<FlexItemNode> {
    override val nodeType: ComponentType<FlexItemNode> get() = FlexItemNode

    override fun create(): FlexItemNode {
        return FlexItemNode(order = order)
    }

    override fun update(node: FlexItemNode) {
        node.order = order
    }
}

internal data class FlexElement(
    val flexGrow: Float = 0f,
    val flexShrink: Float = 1f,
    var flexBasis: UIUnit = UIUnit.Auto
) : ModifierNodeElement<FlexItemNode>, Merge<FlexElement> {
    override val nodeType: ComponentType<FlexItemNode> get() = FlexItemNode

    override fun create(): FlexItemNode {
        return FlexItemNode(flexGrow = flexGrow, flexShrink = flexShrink, flexBasis = flexBasis)
    }

    override fun mergeWith(other: FlexElement): FlexElement {
        val flexGrow = if (flexGrow == 0f) other.flexGrow else flexGrow
        val flexShrink = if (flexShrink == 0f) other.flexShrink else flexShrink
        val flexBasis = if (flexBasis == UIUnit.Auto) other.flexBasis else flexBasis
        return FlexElement(flexGrow, flexShrink, flexBasis)
    }

    override fun update(node: FlexItemNode) {
        node.flexGrow = flexGrow
        node.flexShrink = flexShrink
        node.flexBasis = flexBasis
    }
}

@JvmInline
internal value class AlignSelfElement(
    private val alignSelf: AlignSelf = AlignSelf.Auto
) : ModifierNodeElement<FlexItemNode> {

    override val nodeType: ComponentType<FlexItemNode> get() = FlexItemNode

    override fun create(): FlexItemNode {
        return FlexItemNode(alignSelf = alignSelf)
    }

    override fun update(node: FlexItemNode) {
        node.alignSelf = alignSelf
    }
}

interface FlexBoxScore {

    @Stable
    fun Modifier.order(order: Int = 0): Modifier = this + OrderElement(order)

    @Stable
    fun Modifier.flex(
        flexGrow: Float = 0f,
        flexShrink: Float = 1f,
        flexBasis: UIUnit = UIUnit.Auto
    ): Modifier = this + FlexElement(flexGrow, flexShrink, flexBasis)

    @Stable
    fun Modifier.alignSelf(alignSelf: AlignSelf = AlignSelf.Auto): Modifier = this + AlignSelfElement(alignSelf)
}

internal data object InternalFlexBoxScore : FlexBoxScore

@Composable
fun FlexBox(
    modifier: Modifier = Modifier,
    flexDirection: FlexDirection = FlexDirection.Row,
    flexWrap: FlexWrap = FlexWrap.Nowrap,
    justifyContent: JustifyContent = JustifyContent.Start,
    alignItems: AlignItems = AlignItems.Stretch,
    alignContent: AlignContent = AlignContent.Stretch,
    context: @Composable FlexBoxScore.() -> Unit
) {
    val measurePolicy = remember(flexDirection, flexWrap, justifyContent, alignItems, alignContent) {
        FlexBoxMeasurePolicy(flexDirection, flexWrap, justifyContent, alignItems, alignContent)
    }
    ComposeUiLayout(modifier, measurePolicy) { InternalFlexBoxScore.context() }
}

data class FlexBoxMeasurePolicy(
    val flexDirection: FlexDirection,
    val flexWrap: FlexWrap,
    val justifyContent: JustifyContent,
    val alignItems: AlignItems,
    val alignContent: AlignContent
) : MeasurePolicy {

    private fun IntSize.toAxisSize(): AxisSize {
        return if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            AxisSize(width, height)
        } else {
            AxisSize(height, width)
        }
    }

    private fun Constraints.maxAxisSize(): AxisSize {
        return if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            AxisSize(maxWidth, maxHeight)
        } else {
            AxisSize(maxHeight, maxWidth)
        }
    }

    private fun Constraints.axisSize(flexLines: List<FlexLine>) : AxisSize {
        fun List<FlexLine>.maxCrossSize(): Int = fold(0) { acc, line -> acc + line.axisSize.crossSize }
        return if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            AxisSize(maxWidth, if (hasFixedHeight) maxHeight else flexLines.maxCrossSize())
        } else {
            AxisSize(maxHeight, if (hasFixedWidth) maxWidth else flexLines.maxCrossSize())
        }
    }

    private fun MeasureScope.flexBasis(constraints: Constraints, flexBasis: UIUnit): Constraints {
        if (flexBasis == UIUnit.Auto) return constraints
        return if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            val width = flexBasis.toPixel(constraints.maxWidth)
            constraints.copy(minWidth = width, maxWidth = width)
        } else {
            val height = flexBasis.toPixel(constraints.maxHeight)
            constraints.copy(minHeight = height, maxHeight = height)
        }
    }

    override fun MeasureScope.measure(entity: Entity, children: Sequence<Entity>, constraints: Constraints): MeasureResult {
        val newConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val flexLines = children.map { flexItem(it, newConstraints) }.layoutLines(newConstraints).toMutableList()
        if (flexWrap.flags and FLAG_REVERSE == FLAG_REVERSE) {
            flexLines.reverse()
        }
        val axisSize = constraints.axisSize(flexLines)
        resolveFlexibleLengths(flexLines, axisSize)
        determinePositions(flexLines, axisSize)
        val flexItems = flexLines.asSequence().flatMap { it.items }
        remeasure(flexItems)
        val size = if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            IntSize(axisSize.mainSize, axisSize.crossSize)
        } else {
            IntSize(axisSize.crossSize, axisSize.mainSize)
        }
        return layout(entity, size) {
            flexItems.forEach {
                val position = if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
                    IntPoint2(it.axisOffset.mainOffset, it.axisOffset.crossOffset)
                } else {
                    IntPoint2(it.axisOffset.crossOffset, it.axisOffset.mainOffset)
                }
                it.placeable.place(it.entity, position)
            }
        }
    }

    private fun MeasureScope.remeasure(items: Sequence<FlexItem>) {
        items.forEach {
            val size = if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
                IntSize(it.axisSize.mainSize, it.axisSize.crossSize)
            } else {
                IntSize(it.axisSize.crossSize, it.axisSize.mainSize)
            }
            if (size != it.placeable.size) {
                it.placeable = it.entity.measurable.measure(it.entity, Constraints.Companion.fixed(size.width, size.height))
            }
        }
    }

    private fun MeasureScope.determinePositions(flexLines: List<FlexLine>, axisSize: AxisSize) {
        if (flexLines.isEmpty()) return
        fun FlexItem.calculateCrossOffset(axisSize: AxisSize) : Int {
            val alignItems = flexItemNode?.alignSelf?.alignItems ?: alignItems
            return when (alignItems) {
                AlignItems.Start -> 0
                AlignItems.Center -> (axisSize.crossSize - this.axisSize.crossSize) / 2
                AlignItems.End -> axisSize.crossSize - this.axisSize.crossSize
                AlignItems.Stretch -> {
                    if (shouldStretch(this)) {
                        this.axisSize = AxisSize(this.axisSize.mainSize, axisSize.crossSize)
                    }
                    0
                }
                AlignItems.Baseline -> 0
            }
        }
        if (flexLines.size == 1) {
            val flexLine = flexLines.component1()
            determineMainAxisPositions(flexLine, axisSize) { mainOffset ->
                axisOffset = AxisOffset(mainOffset, calculateCrossOffset(axisSize))
            }
        } else {
            val totalCrossSize = flexLines.fold(0) { acc, flexLine -> acc + flexLine.axisSize.crossSize }
            val remainingSpace = axisSize.crossSize - totalCrossSize
            val startSpace = when (alignContent) {
                AlignContent.End -> remainingSpace
                AlignContent.Center -> remainingSpace / 2
                AlignContent.Around -> remainingSpace / (flexLines.size shl 1)
                else -> 0
            }
            val space = when (alignContent) {
                AlignContent.Around -> remainingSpace / flexLines.size
                AlignContent.Between -> remainingSpace / (flexLines.size - 1)
                else -> 0
            }
            val stretch = if (alignContent == AlignContent.Stretch && remainingSpace > 0) remainingSpace / flexLines.size else 0
            flexLines.fold(startSpace) { acc, flexLine ->
                if (stretch != 0) {
                    flexLine.axisSize = flexLine.axisSize.copy(crossSize = stretch + flexLine.axisSize.crossSize)
                }
                determineMainAxisPositions(flexLine, axisSize) { mainOffset ->
                    this.axisOffset = AxisOffset(mainOffset, acc + calculateCrossOffset(flexLine.axisSize))
                }
                acc + space + flexLine.axisSize.crossSize
            }
        }
    }

    private fun MeasureScope.shouldStretch(flexItem: FlexItem) : Boolean {
        val measurementConstraints = flexItem.entity[ComposeUiLayoutNode].coordinator.measurementConstraints ?: return true
        val hasFixed = if (flexDirection.flags and FLAG_DIRECTION_ROW == FLAG_DIRECTION_ROW) {
            measurementConstraints.hasFixedHeight
        } else {
            measurementConstraints.hasFixedWidth
        }
        return !hasFixed
    }

    private fun determineMainAxisPositions(flexLine: FlexLine, axisSize: AxisSize, config: FlexItem.(mainOffset: Int) -> Unit) {
        if (flexLine.items.isEmpty()) return
        val remainingSpace = axisSize.mainSize - flexLine.axisSize.mainSize
        val startSpace = when (justifyContent) {
            JustifyContent.End -> remainingSpace
            JustifyContent.Center -> remainingSpace / 2
            JustifyContent.Around -> remainingSpace / (flexLine.items.size shl 1)
            else -> 0
        }
        val space = when (justifyContent) {
            JustifyContent.Around -> remainingSpace / flexLine.items.size
            JustifyContent.Between -> if (flexLine.items.size > 1) remainingSpace / (flexLine.items.size - 1) else 0
            else -> 0
        }
        flexLine.items.fold(startSpace) { acc, flexItem ->
            flexItem.config(acc)
            acc + flexItem.axisSize.mainSize + space
        }
    }

    private fun resolveFlexibleLengths(flexLines: List<FlexLine>, axisSize: AxisSize) {
        if (flexLines.isEmpty()) return
        flexLines.forEach { flexLine ->
            val containerMainSize = axisSize.mainSize
            if (flexLine.axisSize.mainSize == containerMainSize) return@forEach
            val remainingSpace = containerMainSize - flexLine.axisSize.mainSize
            if (remainingSpace > 0) {
                val totalGrow = flexLine.items.fold(0f) { acc, flexItem -> (flexItem.flexItemNode?.flexGrow ?: 0f) + acc }
                if (totalGrow > 0) {
                    flexLine.items.forEach {
                        val flexGrow = it.flexItemNode?.flexGrow ?: 0f
                        it.axisSize = AxisSize(it.axisSize.mainSize + (remainingSpace * flexGrow / totalGrow).roundToInt(), it.axisSize.crossSize)
                    }
                    flexLine.axisSize = AxisSize(containerMainSize, flexLine.axisSize.crossSize)
                }
            } else {
                fun FlexItem.flexShrink(): Float = flexItemNode?.flexShrink ?: 1f
                val totalShrink = flexLine.items.fold(0f) { acc, flexItem -> flexItem.flexShrink() + acc }
                if (totalShrink > 0) {
                    val scaledShrinkFactor = flexLine.items.fold(0f) { acc, flexItem -> flexItem.axisSize.mainSize * flexItem.flexShrink() + acc }
                    flexLine.items.forEach { item ->
                        val scaledFactor = item.axisSize.mainSize * item.flexShrink() / scaledShrinkFactor * abs(remainingSpace)
                        item.axisSize = AxisSize(item.axisSize.mainSize - scaledFactor.roundToInt(), item.axisSize.crossSize)
                    }
                    flexLine.axisSize = AxisSize(containerMainSize, flexLine.axisSize.crossSize)
                }
            }
        }
    }

    private fun Sequence<FlexItem>.layoutLines(constraints: Constraints): Sequence<FlexLine> {
        val axisSize = constraints.maxAxisSize()
        return sequence {
            val items = mutableListOf<FlexItem>()
            var currentAxisSize: AxisSize = AxisSize.Zero
            forEach {
                if (flexWrap != FlexWrap.Nowrap && currentAxisSize.mainSize + it.axisSize.mainSize > axisSize.mainSize && items.isNotEmpty()) {
                    if (flexDirection.flags and FLAG_REVERSE == FLAG_REVERSE) {
                        items.reverse()
                    }
                    yield(FlexLine(items.toList(), currentAxisSize))
                    items.clear()
                    currentAxisSize = AxisSize.Zero
                }
                currentAxisSize = AxisSize(it.axisSize.mainSize + currentAxisSize.mainSize, max(it.axisSize.crossSize, currentAxisSize.crossSize))
                items.add(it)
            }
            if (items.isNotEmpty()) {
                if (flexDirection.flags and FLAG_REVERSE == FLAG_REVERSE) {
                    items.reverse()
                }
                yield(FlexLine(items, currentAxisSize))
            }
        }
    }

    private fun MeasureScope.flexItem(entity: Entity, constraints: Constraints): FlexItem {
        val flexItemNode = getItemNode(entity)
        val flexBasis = flexItemNode?.flexBasis ?: UIUnit.Auto
        val placeable = entity.measurable.measure(entity, flexBasis(constraints, flexBasis))
        return FlexItem(entity, flexItemNode, placeable, placeable.size.toAxisSize())
    }

    private fun MeasureScope.getItemNode(entity: Entity): FlexItemNode? = entity.getOrNull(FlexItemNode)

    internal data class FlexItem(
        val entity: Entity,
        val flexItemNode: FlexItemNode?,
        var placeable: Placeable,
        var axisSize: AxisSize = AxisSize.Zero,
        var axisOffset: AxisOffset = AxisOffset.Zero
    )

    internal data class FlexLine(
        val items: List<FlexItem>,
        var axisSize: AxisSize
    )

    @JvmInline
    internal value class AxisSize(val data: Long) {
        val mainSize: Int get() = data.low
        val crossSize: Int get() = data.high

        fun copy(mainSize: Int = this.mainSize, crossSize: Int = this.crossSize) : AxisSize {
            return AxisSize(mainSize, crossSize)
        }
        override fun toString(): String {
            return "AxisSize($mainSize, $crossSize)"
        }

        companion object {
            val Zero: AxisSize = AxisSize(0, 0)

            operator fun invoke(mainSize: Int, crossSize: Int): AxisSize {
                return AxisSize(Long.fromLowHigh(mainSize, crossSize))
            }
        }
    }

    @JvmInline
    internal value class AxisOffset(val data: Long) {
        val mainOffset: Int get() = data.low
        val crossOffset: Int get() = data.high

        override fun toString(): String {
            return "AxisOffset($mainOffset, $crossOffset)"
        }

        companion object {
            val Zero: AxisOffset = AxisOffset(0, 0)
            operator fun invoke(mainOffset: Int, crossOffset: Int): AxisOffset = AxisOffset(Long.fromLowHigh(mainOffset, crossOffset))
        }
    }
}