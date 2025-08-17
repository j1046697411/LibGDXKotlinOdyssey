@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ui.flexbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import cn.jzl.ui.*
import cn.jzl.ui.compose.layerNode
import cn.jzl.ui.flex.AlignSelfValue
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.style.UIUnit
import cn.jzl.ui.style.get
import cn.jzl.ui.style.height
import cn.jzl.ui.style.width
import kotlin.math.max

enum class FlexDirection(val reverse: Boolean, val isRow: Boolean) {
    Row(false, true),
    Column(false, false),
    RowReverse(true, true),
    ColumnReverse(true, false),
}

enum class FlexWrap { NoWrap, Wrap, WrapReverse }
enum class JustifyContent { FlexStart, FlexEnd, Center, SpaceBetween, SpaceAround }
enum class AlignItems { Stretch, FlexStart, FlexEnd, Center, BaseLine }
enum class AlignContent { Stretch, FlexStart, FlexEnd, Center, SpaceBetween, SpaceAround, }

// ######### 子元素 #########

@JvmInline
value class Order(val order: Int = 0) : Modifier.Element

@JvmInline
value class FlexGrow(val flexGrow: Float = 0f) : Modifier.Element

@JvmInline
value class FlexShrink(val flexShrink: Float = 1f) : Modifier.Element

@JvmInline
value class FlexBasis(val basis: UIUnit) : Modifier.Element

enum class AlignSelf { Auto, FlexStart, FlexEnd, Center, BaseLine, Stretch }

@PublishedApi
@JvmInline
internal value class AlignSelfModifier(val alignSelf: AlignSelf) : Modifier.Element

@Stable
inline fun Modifier.order(order: Int = 0) = this + Order(order)

@Stable
inline fun Modifier.flexGrow(flexGrow: Float = 0f) = this + FlexGrow(flexGrow)

@Stable
inline fun Modifier.flexShrink(flexShrink: Float = 1f) = this + FlexShrink(flexShrink)

@Stable
inline fun Modifier.flexBasis(basis: UIUnit = UIUnit.Auto) = this + FlexBasis(basis)

@Stable
inline fun Modifier.alignSelf(alignSelf: AlignSelf) = this + AlignSelfModifier(alignSelf)

private data class FlexBoxLine(
    val items: List<FlexBoxItem>,
    var axisSize: AxisSize,
    var axisPosition: AxisPosition = AxisPosition.ZERO
)

private data class FlexBoxItem(
    val measurable: Measurable,
    val placeable: Placeable,
    var axisSize: AxisSize,
    var axisPosition: AxisPosition = AxisPosition.ZERO
)

data class AxisSize(val mainSize: Int = 0, val crossSize: Int = 0) {

    operator fun plus(axisSize: AxisSize): AxisSize = AxisSize(mainSize + axisSize.mainSize, crossSize + axisSize.crossSize)
    operator fun minus(axisSize: AxisSize): AxisSize = AxisSize(mainSize - axisSize.mainSize, crossSize - axisSize.crossSize)
    operator fun times(axisSize: AxisSize): AxisSize = AxisSize((mainSize * axisSize.mainSize), (crossSize * axisSize.crossSize))
    operator fun div(axisSize: AxisSize): AxisSize = AxisSize(mainSize / axisSize.mainSize, crossSize / axisSize.crossSize)

    companion object {
        val ZERO = AxisSize(0, 0)
    }
}

data class AxisPosition(val mainOffset: Int = 0, val crossOffset: Int = 0) {
    companion object {
        val ZERO = AxisPosition(0, 0)
    }
}

class FlexBoxMeasurePolicy(
    private val flexDirection: FlexDirection = FlexDirection.Row,
    private val flexWrap: FlexWrap = FlexWrap.NoWrap,
    private val justifyContent: JustifyContent = JustifyContent.FlexStart,
    private val alignItems: AlignItems = AlignItems.Stretch,
    private val alignContent: AlignContent = AlignContent.Stretch,
) : MeasurePolicy {

    private val Measurable.order: Int get() = styleSheet.get<Order>()?.order ?: 0
    private val Measurable.flexGrow: Float get() = styleSheet.get<FlexGrow>()?.flexGrow ?: 0f
    private val Measurable.flexShrink: Float get() = styleSheet.get<FlexShrink>()?.flexShrink ?: 1f
    private val Measurable.flexBasis: UIUnit? get() = styleSheet.get<FlexBasis>()?.basis
    private val Measurable.alignSelf: AlignSelf get() = styleSheet.get<AlignSelfModifier>()?.alignSelf ?: AlignSelf.Auto

    private fun Measurable.mainSizeUnit(): UIUnit = flexBasis ?: if (flexDirection.isRow) styleSheet.width else styleSheet.height
    private fun Measurable.crossSizeUnit(): UIUnit = if (flexDirection.isRow) styleSheet.height else styleSheet.width

    private fun Measurable.createChildConstraints(constraints: Constraints, remainingAxisSize: AxisSize): Constraints {
        val mainSizeUnit = mainSizeUnit()
        val crossSizeUnit = crossSizeUnit()
        val parentMainSize = constraints.mainSize
        val parentCrossSize = constraints.crossSize
        val mainSize = if (mainSizeUnit == UIUnit.Auto) remainingAxisSize.mainSize else mainSizeUnit.pixel(parentMainSize.toFloat()).toInt()
        val crossSize = if (crossSizeUnit == UIUnit.Auto) remainingAxisSize.crossSize else crossSizeUnit.pixel(parentCrossSize.toFloat()).toInt()
        val width = if (flexDirection.isRow) mainSize else crossSize
        val height = if (flexDirection.isRow) crossSize else mainSize
        val widthFixed = if (flexDirection.isRow) mainSizeUnit != UIUnit.Auto else crossSizeUnit != UIUnit.Auto
        val heightFixed = if (flexDirection.isRow) crossSizeUnit != UIUnit.Auto else mainSizeUnit != UIUnit.Auto
        return Constraints(
            minWidth = if (widthFixed) width else 0,
            maxWidth = width,
            minHeight = if (heightFixed) height else 0,
            maxHeight = height,
        )
    }

    fun size(mainSize: Int, crossSize: Int): IntSize = IntSize(if (flexDirection.isRow) mainSize else crossSize, if (flexDirection.isRow) crossSize else mainSize)

    private val Constraints.mainSize: Int get() = if (flexDirection.isRow) maxWidth else maxHeight
    private val Constraints.crossSize: Int get() = if (flexDirection.isRow) maxHeight else maxWidth

    override fun MeasureScore.measure(self: Measurable, measures: Sequence<Measurable>, constraints: Constraints): MeasureResult {
        val lines = measures.flexItems(constraints).layoutLines(constraints).toMutableList()
        if (flexWrap == FlexWrap.WrapReverse) lines.reverse()
        resolveFlexibleLengths(lines, constraints)
        determinePositions(lines, constraints)
        val mainSize = if (self.mainSizeUnit() == UIUnit.Auto) lines.fold(0) {  acc, line -> acc + line.axisSize.mainSize } else constraints.mainSize
        val crossSize = if (self.crossSizeUnit() == UIUnit.Auto) lines.maxOf { it.axisSize.crossSize } else constraints.crossSize
        return layout(size(mainSize, crossSize), mapOf()) {
            lines.asSequence().flatMap { it.items.asSequence() }.forEach {
                it.placeable.size = size(it.axisSize.mainSize, it.axisSize.crossSize)
                it.placeable.placeAt(Coordinate(
                    x = if (flexDirection.isRow) it.axisPosition.mainOffset else it.axisPosition.crossOffset,
                    y = if (flexDirection.isRow) it.axisPosition.crossOffset else it.axisPosition.mainOffset,
                ))
            }
        }
    }

    private fun determinePositions(lines: List<FlexBoxLine>, constraints: Constraints) {
        if (lines.isEmpty()) return
        val containerCrossSize = if (flexDirection.isRow) constraints.maxHeight else constraints.maxWidth
        if (lines.size == 1) {
            val line = lines.single()
            if (alignItems == AlignItems.Stretch) {
                line.axisSize = line.axisSize.copy(crossSize = containerCrossSize)
            }
            determineMainAxisPositions(line, constraints) { mainOffset ->
                val alignSelf = measurable.alignSelf
                val crossOffset = when(alignSelf) {
                    AlignSelf.Auto -> {
                        when(alignItems) {
                            AlignItems.FlexStart -> 0
                            AlignItems.FlexEnd -> containerCrossSize - axisSize.crossSize
                            AlignItems.Center -> (containerCrossSize - axisSize.crossSize) / 2
                            AlignItems.Stretch -> {
                                if (measurable.mainSizeUnit() == UIUnit.Auto) {
                                    axisSize = axisSize.copy(crossSize = line.axisSize.crossSize)
                                }
                                0
                            }
                            else -> 0
                        }
                    }
                    AlignSelf.FlexStart -> 0
                    AlignSelf.FlexEnd -> containerCrossSize - axisSize.crossSize
                    AlignSelf.Center -> (containerCrossSize - axisSize.crossSize) / 2
                    AlignSelf.Stretch -> {
                        if (measurable.mainSizeUnit() == UIUnit.Auto) {
                            axisSize = axisSize.copy(crossSize = line.axisSize.crossSize)
                        }
                        0
                    }
                    else -> 0
                }
                this.axisPosition = AxisPosition(mainOffset, crossOffset)
            }
        } else {
            val totalCrossSize = lines.fold(0f) { acc, line -> acc + line.axisSize.crossSize }
            val remainingSpace = containerCrossSize - totalCrossSize
            val startSpace = when (alignContent) {
                AlignContent.FlexEnd -> remainingSpace
                AlignContent.Center -> remainingSpace / 2
                AlignContent.SpaceAround -> remainingSpace / (lines.size shl 1)
                else -> 0f
            }
            val space = when (alignContent) {
                AlignContent.SpaceAround -> remainingSpace / (lines.size - 1)
                AlignContent.SpaceBetween -> remainingSpace / lines.size
                else -> 0f
            }
            val stretch = if (alignContent == AlignContent.Stretch) remainingSpace / lines.size else 0f
            lines.fold(startSpace) { acc, line ->
                line.axisSize = line.axisSize.copy(crossSize = line.axisSize.crossSize + stretch.toInt())
                determineMainAxisPositions(line, constraints) { mainOffset ->
                    this.axisPosition = AxisPosition(mainOffset, acc.toInt())
                    if (measurable.mainSizeUnit() == UIUnit.Auto) {
                        axisSize = axisSize.copy(crossSize = line.axisSize.crossSize)
                    }
                }
                acc + space + line.axisSize.crossSize
            }
        }

    }

    private fun determineMainAxisPositions(line: FlexBoxLine, constraints: Constraints, updateItemBlock: FlexBoxItem.(mainOffset: Int) -> Unit) {
        val containerMainSize = if (flexDirection.isRow) constraints.maxWidth else constraints.maxHeight
        val totalMainSize = line.items.fold(0f) { acc, item -> acc + item.axisSize.mainSize }
        val remainingSpace = containerMainSize - totalMainSize
        val startSpace = when (justifyContent) {
            JustifyContent.FlexEnd -> remainingSpace
            JustifyContent.Center -> remainingSpace / 2
            JustifyContent.SpaceAround -> remainingSpace / (line.items.size shl 1)
            else -> 0f
        }
        val space = when (alignContent) {
            AlignContent.SpaceBetween -> remainingSpace / (line.items.size - 1)
            AlignContent.SpaceAround -> remainingSpace / line.items.size
            else -> 0f
        }
        line.items.fold(startSpace) { acc, item ->
            item.updateItemBlock(acc.toInt())
            acc + space + item.axisSize.mainSize
        }
    }

    private fun resolveFlexibleLengths(lines: List<FlexBoxLine>, constraints: Constraints) {
        if (lines.isEmpty()) return
        val mainSize = if (flexDirection.isRow) constraints.maxWidth else constraints.maxHeight
        val containerMainSize = max(mainSize, lines.maxOf { line -> line.axisSize.mainSize })
        lines.forEach { line ->
            if (line.axisSize.mainSize == containerMainSize) return@forEach
            val remainingSpace = containerMainSize - line.axisSize.mainSize
            if (remainingSpace > 0) {
                val totalGrow = line.items.fold(0f) { acc, item -> acc + item.measurable.flexGrow }
                if (totalGrow > 0) return@forEach
                line.items.forEach { item ->
                    val itemMainSize = item.axisSize.mainSize
                    item.axisSize = item.axisSize.copy(mainSize = itemMainSize + (remainingSpace * item.measurable.flexGrow / totalGrow).toInt())
                }
                line.axisSize = line.axisSize.copy(mainSize = containerMainSize)
            } else {
                val totalShrink = line.items.fold(0f) { acc, item -> acc + item.measurable.flexShrink }
                val scaledShrinkFactor = line.items.fold(0f) { acc, item -> acc + item.measurable.flexShrink / totalShrink }
                line.items.forEach { item ->
                    val itemMainSize = item.axisSize.mainSize
                    item.axisSize = item.axisSize.copy(mainSize = itemMainSize + (remainingSpace * item.measurable.flexShrink * itemMainSize / scaledShrinkFactor).toInt())
                }
                line.axisSize = line.axisSize.copy(mainSize = containerMainSize)
            }
        }
    }

    private fun Sequence<FlexBoxItem>.layoutLines(constraints: Constraints): Sequence<FlexBoxLine> = sequence {
        val currentLineItems = mutableListOf<FlexBoxItem>()
        var currentAxisSize = AxisSize.ZERO
        val mainSize = if (flexDirection.isRow) constraints.maxWidth else constraints.maxHeight
        forEach { item ->
            if (item.axisSize.mainSize + currentAxisSize.mainSize >= mainSize && flexWrap != FlexWrap.NoWrap && currentLineItems.isNotEmpty()) {
                if (flexDirection.reverse) currentLineItems.reverse()
                yield(FlexBoxLine(currentLineItems.toList(), currentAxisSize))
                currentLineItems.clear()
                currentAxisSize = AxisSize.ZERO
            }
            currentLineItems.add(item)
            currentAxisSize = AxisSize(
                currentAxisSize.mainSize + item.axisSize.mainSize,
                max(currentAxisSize.crossSize, item.axisSize.crossSize)
            )
        }
        if (flexDirection.reverse) currentLineItems.reverse()
        yield(FlexBoxLine(currentLineItems, currentAxisSize))
    }

    private fun Sequence<Measurable>.flexItems(constraints: Constraints): Sequence<FlexBoxItem> {
        var remainingAxisSize = AxisSize(
            mainSize = if (flexDirection.isRow) constraints.maxWidth else constraints.maxHeight,
            crossSize = if (flexDirection.isRow) constraints.maxHeight else constraints.maxWidth
        )
        return map { measurable ->
            val placeable = measurable.measure(measurable.createChildConstraints(constraints, remainingAxisSize))
            val axisSize = AxisSize(
                if (flexDirection.isRow) placeable.size.width else placeable.size.height,
                if (flexDirection.isRow) placeable.size.height else placeable.size.width
            )
            remainingAxisSize = remainingAxisSize.copy(mainSize = max(remainingAxisSize.mainSize - axisSize.mainSize, 0))
            FlexBoxItem(measurable, placeable, axisSize)
        }
    }
}

@Composable
fun flexBox(
    modifier: Modifier,
    flexDirection: FlexDirection = FlexDirection.Row,
    flexWrap: FlexWrap = FlexWrap.Wrap,
    justifyContent: JustifyContent = JustifyContent.FlexStart,
    alignItems: AlignItems = AlignItems.FlexStart,
    alignContent: AlignContent = AlignContent.FlexStart,
    content: @Composable () -> Unit
) {
    val measurePolicy = remember(flexDirection, flexWrap, justifyContent, alignItems, alignContent) {
        FlexBoxMeasurePolicy(flexDirection, flexWrap, justifyContent, alignItems, alignContent)
    }
    layerNode(modifier, measurePolicy, content)
}