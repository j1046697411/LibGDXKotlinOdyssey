//package cn.jzl.ui.flex
//
//import cn.jzl.ui.*
//import cn.jzl.ui.style.Height
//import cn.jzl.ui.style.UIUnit
//import cn.jzl.ui.style.Width
//import cn.jzl.ui.style.get
//import kotlin.math.max
//import kotlin.math.min
//
//
//class FlexBoxMeasurePolicy : MeasurePolicy {
//    private inline val Measurable.flexDirection: FlexDirectionValue get() = styleSheet.get<FlexDirection>()?.flexDirection ?: FlexDirectionValue.Row
//    private inline val Measurable.flexWrap: FlexWrapValue get() = styleSheet.get<FlexWrap>()?.wrap ?: FlexWrapValue.Nowrap
//    private inline val Measurable.justifyContent: JustifyContentValue
//        get() = styleSheet.get<JustifyContent>()?.justifyContent ?: JustifyContentValue.FlexStart
//    private inline val Measurable.alignContent: AlignContentValue get() = styleSheet.get<AlignContent>()?.alignContent ?: AlignContentValue.Stretch
//    private inline val Measurable.alignItems: AlignItemsValue get() = styleSheet.get<AlignItems>()?.alignItems ?: AlignItemsValue.Stretch
//    private inline val Measurable.alignSelf: AlignSelfValue get() = styleSheet.get<AlignSelf>()?.alignSelf ?: AlignSelfValue.Auto
//    private inline val Measurable.flexGrow: Float get() = styleSheet.get<FlexGrow>()?.grow ?: 0f
//    private inline val Measurable.flexShrink: Float get() = styleSheet.get<FlexShrink>()?.shrink ?: 1f
//    private inline val Measurable.height: UIUnit get() = styleSheet.get<Height>()?.value ?: UIUnit.Auto
//    private inline val Measurable.width: UIUnit get() = styleSheet.get<Width>()?.value ?: UIUnit.Auto
//    private inline val Measurable.flexBasis: UIUnit get() = styleSheet.get<FlexBasis>()?.basis ?: flexDirection.let { if (it.isRow) width else height }
//    private fun FlexDirectionValue.mainSizeUnit(measurable: Measurable): UIUnit {
//        val flexBasis = measurable.flexBasis
//        if (flexBasis != UIUnit.Auto) return flexBasis
//        return if (isRow) measurable.width else measurable.height
//    }
//
//    private fun FlexDirectionValue.mainSize(measurable: Measurable, constraints: Constraints): Float = mainSizeUnit(measurable).pixel(mainSize(constraints))
//    private fun FlexDirectionValue.crossSize(measurable: Measurable, constraints: Constraints): Float = crossSizeUnit(measurable).pixel(crossSize(constraints))
//    private fun FlexDirectionValue.crossSizeUnit(measurable: Measurable): UIUnit = if (isRow) measurable.height else measurable.width
//    private fun FlexDirectionValue.mainSize(constraints: Constraints): Float = if (isRow) constraints.maxWidth else constraints.maxHeight
//    private fun FlexDirectionValue.crossSize(constraints: Constraints): Float = if (isRow) constraints.maxHeight else constraints.maxWidth
//
//    private fun FlexDirectionValue.maxAxisSize(constraints: Constraints): AxisSize {
//        return if (isRow) AxisSize(constraints.maxWidth, constraints.maxHeight) else AxisSize(constraints.maxWidth, constraints.maxHeight)
//    }
//
//    private fun FlexDirectionValue.minAxisSize(constraints: Constraints): AxisSize {
//        return if (isRow) AxisSize(constraints.minWidth, constraints.minHeight) else AxisSize(constraints.minWidth, constraints.minHeight)
//    }
//
//    private fun FlexDirectionValue.mainSize(size: IntSize): Int = if (isRow) size.width else size.height
//    private fun FlexDirectionValue.crossSize(size: IntSize): Int = if (isRow) size.height else size.width
//
//
//    private fun FlexDirectionValue.axisSize(size: IntSize): AxisSize {
//        return if (isRow) {
//            AxisSize(size.width, size.height)
//        } else {
//            AxisSize(size.height, size.width)
//        }
//    }
//
//    private fun Constraints.constrain(direction: FlexDirectionValue, axisSize: AxisSize): AxisSize {
//        val maxSize = direction.maxAxisSize(this)
//        val minSize = direction.minAxisSize(this)
//        return if (direction.isRow) {
//            AxisSize(
//                mainSize = axisSize.mainSize.coerceIn(minSize.mainSize, maxSize.mainSize),
//                crossSize = axisSize.crossSize.coerceIn(minSize.crossSize, maxSize.crossSize)
//            )
//        } else {
//            AxisSize(
//                mainSize = axisSize.mainSize.coerceIn(minSize.mainSize, maxSize.mainSize),
//                crossSize = axisSize.crossSize.coerceIn(minSize.crossSize, maxSize.crossSize)
//            )
//        }
//    }
//
//    private fun FlexDirectionValue.axisSize(measurable: Measurable, constraints: Constraints): AxisSize {
//        val axisSize = maxAxisSize(constraints)
//        val mainSize = mainSizeUnit(measurable).pixel(axisSize.mainSize.toFloat())
//        val crossSize = crossSizeUnit(measurable).pixel(axisSize.crossSize.toFloat())
//        return constraints.constrain(this, AxisSize(mainSize.toInt(), crossSize.toInt()))
//    }
//
//    override fun MeasureScore.measure(self: Measurable, measures: Sequence<Measurable>, constraints: Constraints): MeasureResult {
//        val flexDirection = self.flexDirection
//        val flexWrap = self.flexWrap
//        val axisSize = flexDirection.axisSize(self, constraints)
//        val actualConstraints = constraints.copy(
//            maxWidth = if (flexDirection.isRow) min(axisSize.mainSize, constraints.maxWidth) else min(axisSize.crossSize, constraints.maxWidth),
//            maxHeight = if (flexDirection.isRow) min(axisSize.crossSize, constraints.maxHeight) else min(axisSize.mainSize, constraints.maxHeight),
//        )
//        val lines = measures.flexItems(flexDirection, actualConstraints).layoutLines(self, constraints).toMutableList()
//            .apply { if (flexWrap == FlexWrapValue.WrapReverse) reverse() }
//        resolveFlexibleLengths(flexDirection, constraints, lines)
//        determinePositions(self, flexDirection, constraints, lines)
//        val actualAxisSize = lines.fold(AxisSize.ZERO) { acc, line ->
//            AxisSize(
//                mainSize = acc.mainSize + line.axisSize.mainSize,
//                crossSize = max(acc.mainSize, line.axisSize.crossSize),
//            )
//        }
//        return layout(
//            IntSize(constraints.maxWidth, constraints.maxHeight),
//            emptyMap()
//        ) {
//            lines.flatMap { line -> line.items }.forEach { item ->
//                val placeable = item.measurable.measure(
//                    Constraints(
//                        maxWidth = if (flexDirection.isRow) item.mainSize else item.crossSize,
//                        maxHeight = if (flexDirection.isRow) item.crossSize else item.mainSize,
//                    )
//                )
//                placeable.placeAt(
//                    if (flexDirection.isRow) item.mainOffset else item.crossOffset,
//                    if (flexDirection.isRow) item.crossOffset else item.mainOffset
//                )
//            }
//        }
//    }
//
//    private fun determinePositions(self: Measurable, flexDirection: FlexDirectionValue, constraints: Constraints, lines: List<FlexLine>) {
//        if (lines.isEmpty()) return
//        val containerCrossSize = flexDirection.crossSize(constraints)
//        if (lines.size == 1) {
//            val alignItems = self.alignItems
//            val line = lines.component1()
//            if (alignItems == AlignItemsValue.Stretch) {
//                line.crossSize = containerCrossSize
//            }
//            determineMainAxisPositions(self, flexDirection, constraints, line) { mainOffset ->
//                val alignSelf = measurable.alignSelf
//                this.mainOffset = mainOffset
//                if (alignSelf == AlignSelfValue.Auto) {
//                    this.crossOffset = when (alignItems) {
//                        AlignItemsValue.FlexStart -> 0f
//                        AlignItemsValue.Center -> (containerCrossSize - crossSize) / 2f
//                        AlignItemsValue.FlexEnd -> containerCrossSize - crossSize
//                        AlignItemsValue.Stretch -> {
//                            if (flexDirection.crossSizeUnit(measurable) == UIUnit.Auto) {
//                                crossSize = line.crossSize
//                            }
//                            0f
//                        }
//
//                        AlignItemsValue.Baseline -> 0f
//                    }
//                } else {
//                    this.crossOffset = when (alignSelf) {
//                        AlignSelfValue.FlexStart -> 0f
//                        AlignSelfValue.Center -> (containerCrossSize - crossSize) / 2f
//                        AlignSelfValue.FlexEnd -> containerCrossSize - crossSize
//                        AlignSelfValue.Stretch -> {
//                            if (flexDirection.crossSizeUnit(measurable) == UIUnit.Auto) {
//                                crossSize = line.crossSize
//                            }
//                            0f
//                        }
//
//                        AlignSelfValue.Baseline -> 0f
//                        else -> 0f
//                    }
//                }
//
//            }
//        } else {
//            val alignContent = self.alignContent
//            val totalCrossSize = lines.fold(0f) { acc, flexLine -> acc + flexLine.crossSize }
//            val remainingSpace = containerCrossSize - totalCrossSize
//            val startSpace = when (alignContent) {
//                AlignContentValue.FlexEnd -> remainingSpace
//                AlignContentValue.Center -> remainingSpace / 2
//                AlignContentValue.SpaceAround -> remainingSpace / (lines.size shl 1)
//                else -> 0f
//            }
//            val space = when (alignContent) {
//                AlignContentValue.SpaceAround -> remainingSpace / lines.size
//                AlignContentValue.SpaceBetween -> remainingSpace / (lines.size - 1)
//                else -> 0f
//            }
//            val stretch = when (alignContent) {
//                AlignContentValue.Stretch -> remainingSpace / lines.size
//                else -> 0f
//            }
//            lines.fold(startSpace) { acc, flexLine ->
//                flexLine.crossSize += stretch
//                determineMainAxisPositions(self, flexDirection, constraints, flexLine) { mainOffset ->
//                    this.mainOffset = mainOffset
//                    if (flexDirection.crossSizeUnit(measurable) == UIUnit.Auto) {
//                        crossSize = flexLine.crossSize
//                    }
//                    this.crossOffset = acc
//                }
//                acc + space + flexLine.crossSize
//            }
//        }
//    }
//
//    private fun determineMainAxisPositions(
//        self: Measurable,
//        flexDirection: FlexDirectionValue,
//        constraints: Constraints,
//        line: FlexLine,
//        updateItemBlock: FlexItem.(mainOffset: Float) -> Unit
//    ) {
//        val containerMainSize = flexDirection.mainSize(constraints)
//        val justifyContent = self.justifyContent
//        val totalMainSize = line.items.fold(0f) { acc, flexItem -> acc + flexItem.mainSize }
//        val remainingSpace = containerMainSize - totalMainSize
//        val startSpace = when (justifyContent) {
//            JustifyContentValue.FlexEnd -> remainingSpace
//            JustifyContentValue.Center -> remainingSpace / 2
//            JustifyContentValue.SpaceBetween -> remainingSpace / (line.items.size shl 1)
//            else -> 0f
//        }
//        val space = when (justifyContent) {
//            JustifyContentValue.SpaceBetween -> remainingSpace / line.items.size
//            JustifyContentValue.SpaceAround -> remainingSpace / (line.items.size - 1)
//            else -> 0f
//        }
//        line.items.fold(startSpace) { acc, flexItem ->
//            flexItem.updateItemBlock(acc)
//            acc + space + flexItem.mainSize
//        }
//    }
//
//    private fun resolveFlexibleLengths(flexDirection: FlexDirectionValue, constraints: Constraints, lines: List<FlexLine>) {
//        if (lines.isEmpty()) return
//        lines.forEach { line ->
//            val containerMainSize = flexDirection.mainSize(constraints)
//            if (line.mainSize == containerMainSize) return@forEach
//            val remainingSpace = containerMainSize - line.mainSize
//            if (remainingSpace > 0) {
//                val totalGrow = line.items.fold(0f) { acc, flexItem -> acc + flexItem.measurable.flexGrow }
//                if (totalGrow > 0) {
//                    line.items.forEach {
//                        it.mainSize += (remainingSpace * it.measurable.flexGrow / totalGrow)
//                    }
//                    line.mainSize = containerMainSize
//                }
//            } else {
//                val totalShrink = line.items.fold(0f) { acc, flexItem -> acc + flexItem.measurable.flexShrink }
//                val scaledShrinkFactor = line.items.fold(0f) { acc, flexItem -> acc + flexItem.mainSize * flexItem.measurable.flexShrink }
//                if (totalShrink > 0) {
//                    line.items.forEach {
//                        it.mainSize += (remainingSpace * (it.measurable.flexShrink * it.mainSize) / scaledShrinkFactor)
//                    }
//                    line.mainSize = containerMainSize
//                }
//            }
//        }
//    }
//
//    private fun Sequence<FlexItem>.layoutLines(self: Measurable, constraints: Constraints): Sequence<FlexLine> {
//        return sequence {
//            val flexDirection = self.flexDirection
//            val flexWrap = self.flexWrap
//            val currentLine = mutableListOf<FlexItem>()
//            val mainSize = flexDirection.mainSize(constraints)
//            var currentAxisSize = AxisSize.ZERO
//            forEach {
//                if (flexWrap != FlexWrapValue.Nowrap && currentAxisSize.mainSize + it.axisSize.mainSize > mainSize && currentLine.isNotEmpty()) {
//                    val items = currentLine.toMutableList()
//                    if (flexDirection.reverse) {
//                        items.reverse()
//                    }
//                    yield(FlexLine(items, currentAxisSize))
//                    currentLine.clear()
//                    currentAxisSize = AxisSize.ZERO
//                }
//                currentLine.add(it)
//                currentAxisSize = AxisSize(
//                    mainSize = currentAxisSize.mainSize + it.axisSize.mainSize,
//                    crossSize = max(currentAxisSize.crossSize, it.axisSize.crossSize)
//                )
//            }
//            if (flexDirection.reverse) {
//                currentLine.reverse()
//            }
//            yield(FlexLine(currentLine, currentAxisSize))
//        }
//    }
//
//    private fun Sequence<Measurable>.flexItems(flexDirection: FlexDirectionValue, constraints: Constraints): Sequence<FlexItem> {
//        var remainingConstraints = constraints
//        return map { measurable ->
//            val placeable = measurable.measure(remainingConstraints)
//            remainingConstraints = if (flexDirection.isRow) {
//                remainingConstraints.copy(maxWidth = max(remainingConstraints.maxWidth - placeable.size.width, 0))
//            } else {
//                remainingConstraints.copy(maxHeight = max(remainingConstraints.maxHeight - placeable.size.height, 0))
//            }
//            val axisSize = flexDirection.axisSize(placeable.size)
//            FlexItem(measurable, placeable, axisSize, AxisPosition.ZERO)
//        }
//    }
//
//    private fun createChildConstraints(flexDirection: FlexDirectionValue, measurable: Measurable, parentConstraints: Constraints): Constraints {
//        return parentConstraints.copy(
//            minWidth = 0f,
//            maxWidth = flexDirection.mainSize(measurable, parentConstraints),
//            minHeight = 0f,
//            maxHeight = flexDirection.crossSize(measurable, parentConstraints)
//        )
//    }
//
//    private data class FlexLine(
//        val items: List<FlexItem>,
//        var axisSize: AxisSize,
//        var axisPosition: AxisPosition = AxisPosition.ZERO
//    )
//
//    private data class FlexItem(
//        val measurable: Measurable,
//        val placeable: Placeable,
//        val axisSize: AxisSize,
//        val axisPosition: AxisPosition
//    )
//
//    data class AxisSize(val mainSize: Int = 0, val crossSize: Int = 0) {
//
//        operator fun plus(axisSize: AxisSize): AxisSize = AxisSize(mainSize + axisSize.mainSize, crossSize + axisSize.crossSize)
//        operator fun minus(axisSize: AxisSize): AxisSize = AxisSize(mainSize - axisSize.mainSize, crossSize - axisSize.crossSize)
//        operator fun times(axisSize: AxisSize): AxisSize = AxisSize((mainSize * axisSize.mainSize), (crossSize * axisSize.crossSize))
//        operator fun div(axisSize: AxisSize): AxisSize = AxisSize(mainSize / axisSize.mainSize, crossSize / axisSize.crossSize)
//
//        companion object {
//            val ZERO = AxisSize(0, 0)
//        }
//    }
//
//    data class AxisPosition(val mainOffset: Int = 0, val crossOffset: Int = 0) {
//        companion object {
//            val ZERO = AxisPosition(0, 0)
//        }
//    }
//}