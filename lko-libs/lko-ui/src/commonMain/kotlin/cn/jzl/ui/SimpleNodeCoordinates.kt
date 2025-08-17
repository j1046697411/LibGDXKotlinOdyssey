package cn.jzl.ui

import cn.jzl.ecs.EntityComponentContext
import cn.jzl.ui.style.StyleSheet
import cn.jzl.ui.style.TestColor
import cn.jzl.ui.style.get
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class SimpleNodeCoordinates(private val layerNode: LayerNode) : NodeCoordinates, Placeable {

    override val styleSheet: StyleSheet get() = layerNode.styleSheet
    private var position: Coordinate = Coordinate.ZERO
    override var size: IntSize = IntSize.ZERO

    override val parentLayoutCoordinates: LayoutCoordinates? get() = layerNode.parentLayoutCoordinates

    override val isAttached: Boolean get() = parentLayoutCoordinates != null

    private var _result: MeasureResult? = null
        set(value) {
            if (value != null && value != field) {
                this.size = value.size
            }
            field = value
        }

    val result: MeasureResult get() = checkNotNull(_result) { "MeasureResult was not set" }

    override fun measure(constraints: Constraints): Placeable {
        this._result = with(layerNode.measurePolicy) { measure(this@SimpleNodeCoordinates, layerNode.measures, constraints) }
        return this
    }

    override fun get(alignmentLine: AlignmentLine): Int = 0

    override fun layout(
        size: IntSize,
        alignmentLines: Map<AlignmentLine, Int>,
        placementBlock: PlacementScope.() -> Unit
    ): MeasureResult = object : MeasureResult, PlacementScope {
        override val parentWidth: Int get() = this@SimpleNodeCoordinates.size.width
        override val size: IntSize = size
        override val alignmentLines: Map<AlignmentLine, Int> get() = alignmentLines
        override fun placeChildren() = placementBlock()
    }

    override fun placeAt(position: Coordinate) {
        this.position = position
        result.placeChildren()
    }

    override fun EntityComponentContext.draw(shape: ShapeRenderer) {
        val oldColor = shape.color
        val color = styleSheet.get<TestColor>()?.color ?: oldColor
        shape.color = color
        shape.rect(position.x.toFloat(), position.y.toFloat(), size.width.toFloat(), size.height.toFloat())
        shape.color = oldColor
    }
}