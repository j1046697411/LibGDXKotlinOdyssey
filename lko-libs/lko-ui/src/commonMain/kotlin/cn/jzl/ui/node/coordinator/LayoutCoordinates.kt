package cn.jzl.ui.node.coordinator

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.*
import cn.jzl.ui.node.Constraints
import cn.jzl.ui.node.LayoutModifierNode
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.unit.Density
import ktx.log.logger

class ComposeUiLayoutNode(
    private val composeUiNodeSystem: ComposeUiNodeSystem,
    internal val entity: Entity
) : Measurable, Component<ComposeUiLayoutNode> {

    private val innerNodeCoordinator = InnerNodeCoordinator(this)
    override val type: ComponentType<ComposeUiLayoutNode> get() = ComposeUiLayoutNode
    val measures: Sequence<Measurable> get() = composeUiNodeSystem.getMeasures(entity)
    val measurePolicy: MeasurePolicy get() = composeUiNodeSystem.getMeasurePolicy(entity)
    val modifierNodes: Sequence<ModifierNode> get() = composeUiNodeSystem.getModifierNodes(entity)
    val density: Density get() = composeUiNodeSystem.getDensity(entity)
    val innerCoordinator: NodeCoordinator get() = innerNodeCoordinator
    internal var outerCoordinator: NodeCoordinator = innerCoordinator

    override fun measure(constraints: Constraints): Placeable = outerCoordinator.measure(constraints)

    companion object : ComponentType<ComposeUiLayoutNode>()
}

interface LayoutCoordinates {
    val size: IntSize
}

abstract class NodeCoordinator(
    protected val composeUiLayoutNode: ComposeUiLayoutNode
) : LayoutCoordinates, Measurable, MeasureScope, Placeable {

    protected var measurementConstraints: Constraints? = null
    protected var measurementResult: MeasureResult? = null

    abstract val tail: ModifierNode

    internal var previous: NodeCoordinator? = null
    internal var next: NodeCoordinator? = null

    override val size: IntSize = IntSize.Zero
    override val density: Float get() = composeUiLayoutNode.density.density
    override val fontScale: Float get() = composeUiLayoutNode.density.fontScale

    override fun layout(size: IntSize, alignmentLines: Map<AlignmentLine, Int>, placementBlock: PlacementScope.() -> Unit): MeasureResult {
        return object : MeasureResult, PlacementScope {
            override val size: IntSize = size
            override val alignmentLines: Map<AlignmentLine, Int> = alignmentLines
            override fun placeChildren(): Unit = placementBlock()
        }
    }

    protected fun performingMeasure(constraints: Constraints, block: () -> MeasureResult): Placeable {
        this.measurementConstraints = constraints
        this.measurementResult = block()
        return this
    }

    override fun place(position: IntPoint2) {
        measurementResult?.placeChildren()
    }
}

internal class InnerNodeCoordinator(
    composeUiLayoutNode: ComposeUiLayoutNode
) : NodeCoordinator(composeUiLayoutNode), Measurable {

    override val tail: ModifierNode = TailModifierNode()

    init {
        tail.updateCoordinator(this)
    }

    override fun measure(constraints: Constraints): Placeable = performingMeasure(constraints) {
        val measurePolicy = composeUiLayoutNode.measurePolicy
        with(measurePolicy) {
            measure(composeUiLayoutNode.measures, constraints)
        }
    }

    private class TailModifierNode : ModifierNode()

    companion object {
        val log = logger<InnerNodeCoordinator>()
    }
}


internal class LayoutModifierNodeCoordinator(
    composeUiLayoutNode: ComposeUiLayoutNode,
    var layoutModifierNode: LayoutModifierNode,
) : NodeCoordinator(composeUiLayoutNode), MeasureScope, Placeable {

    override val tail: ModifierNode get() = layoutModifierNode.node

    override fun measure(constraints: Constraints): Placeable = performingMeasure(constraints) {
        val next = checkNotNull(next) { "LayoutModifierNodeCoordinator next is null" }
        with(layoutModifierNode) { measure(next, constraints) }
    }

    companion object {
        val log = logger<LayoutModifierNodeCoordinator>()
    }
}
