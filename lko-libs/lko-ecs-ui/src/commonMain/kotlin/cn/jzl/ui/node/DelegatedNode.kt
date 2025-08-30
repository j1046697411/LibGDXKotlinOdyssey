package cn.jzl.ui.node

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.*
import cn.jzl.ui.modifier.Merge
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.unit.Density
import cn.jzl.ui.unit.UIUnitScope
import kotlin.reflect.KClass

internal data class ComposeUiDensityComponent(var density: Density) : Component<ComposeUiDensityComponent> {
    override val type: ComponentType<ComposeUiDensityComponent> get() = ComposeUiDensityComponent

    companion object : ComponentType<ComposeUiDensityComponent>()
}

internal data class ComposeUiMeasurePolicyComponent(
    var measurePolicy: MeasurePolicy
) : Component<ComposeUiMeasurePolicyComponent> {
    override val type: ComponentType<ComposeUiMeasurePolicyComponent> get() = ComposeUiMeasurePolicyComponent

    companion object : ComponentType<ComposeUiMeasurePolicyComponent>()
}

internal class ComposeUiModifierComponent(
    modifier: Modifier
) : Component<ComposeUiModifierComponent> {

    private val elements = mutableMapOf<KClass<*>, Modifier.Element>()
    private val modifierNodeElements = mutableListOf<ModifierNodeElement<ModifierNode>>()

    internal val modifierNodes: Sequence<ModifierNodeElement<ModifierNode>> = modifierNodeElements.asSequence()

    var modifier: Modifier = modifier
        set(value) {
            updateModifier(value)
            field = value
        }
    override val type: ComponentType<ComposeUiModifierComponent> get() = ComposeUiModifierComponent

    @Suppress("UNCHECKED_CAST")
    fun updateModifier(modifier: Modifier) {
        elements.clear()
        modifierNodeElements.clear()
        modifier.foldIn(elements) { acc, element ->
            val elementType = element::class
            val oldElement = acc[elementType]
            val newElement = if (oldElement != null && element is Merge<*>) {
                element.unsafeMergeWith(oldElement)
            } else {
                element
            }
            acc[elementType] = newElement
            if (element is ModifierNodeElement<*>) {
                if (oldElement != null) modifierNodeElements.remove(oldElement)
                modifierNodeElements.add(element as ModifierNodeElement<ModifierNode>)
            }
            acc
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <E : Modifier.Element> get(elementType: KClass<E>): E? = elements[elementType] as? E

    companion object : ComponentType<ComposeUiModifierComponent>()
}

interface DelegatedNode {
    val node: ModifierNode
}

abstract class ModifierNode : DelegatedNode {
    override val node: ModifierNode get() = this

    internal var coordinator: NodeCoordinator? = null
        private set

    fun updateCoordinator(coordinator: NodeCoordinator) {
        this.coordinator = coordinator
    }
}

interface ModifierNodeElement<N : ModifierNode> : Modifier.Element {
    val nodeType: ComponentType<N>
    fun create(): N
    fun update(node: N)
}

interface LayoutModifierNode : DelegatedNode {
    fun MeasureScope.measure(entity: Entity, measure: Measurable, constraints: Constraints): MeasureResult
}

data class ComposeUiLayoutNode(
    private val entityHierarchyContext: EntityHierarchyContext,
    override var density: Density
) : Measurable, Component<ComposeUiLayoutNode>, UIUnitScope {

    override val type: ComponentType<ComposeUiLayoutNode> get() = ComposeUiLayoutNode

    val coordinator: NodeCoordinator get() = innerCoordinator

    internal val innerCoordinator: NodeCoordinator = InnerNodeCoordinator(entityHierarchyContext, this)
    internal var outerCoordinator: NodeCoordinator = innerCoordinator

    override fun measure(entity: Entity, constraints: Constraints): Placeable {
        return outerCoordinator.measure(entity, constraints)
    }

    companion object : ComponentType<ComposeUiLayoutNode>()

    private class InnerNodeCoordinator(
        entityHierarchyContext: EntityHierarchyContext,
        unitScope: UIUnitScope
    ) : NodeCoordinator(entityHierarchyContext, unitScope) {
        override fun measure(entity: Entity, constraints: Constraints): Placeable = performingMeasure(constraints) {
            val measurePolicy = entity[ComposeUiMeasurePolicyComponent].measurePolicy
            with(measurePolicy) {
                measure(entity, entity.children, constraints)
            }
        }
    }
}

internal class LayoutModifierNodeCoordinator(
    entityHierarchyContext: EntityHierarchyContext,
    var layoutModifierNode: LayoutModifierNode,
    unitScope: UIUnitScope
) : NodeCoordinator(entityHierarchyContext, unitScope) {
    override fun measure(entity: Entity, constraints: Constraints): Placeable = performingMeasure(constraints) {
        val next = checkNotNull(this.next) { "LayoutModifierNodeCoordinator next is null" }
        with(layoutModifierNode) {
            measure(entity, next, constraints)
        }
    }

}

interface LayoutCoordinates {
    val size: IntSize
}

abstract class NodeCoordinator(
    hierarchyContext: EntityHierarchyContext,
    unitScope: UIUnitScope
) : LayoutCoordinates, Measurable, MeasureScope, Placeable, EntityHierarchyContext by hierarchyContext, UIUnitScope by unitScope {

    internal var previous: NodeCoordinator? = null
    internal var next: NodeCoordinator? = null

    var measurementConstraints: Constraints? = null
        private set

    internal var measurementResult: MeasureResult? = null

    final override val size: IntSize get() = measurementResult?.size ?: IntSize.Zero

    internal var position: IntPoint2 = IntPoint2.Zero
        private set

    override val Entity.measurable: Measurable get() = this[ComposeUiLayoutNode]

    protected fun performingMeasure(constraints: Constraints, block: () -> MeasureResult): Placeable {
        measurementConstraints = constraints
        measurementResult = block()
        return this
    }

    override fun layout(entity: Entity, size: IntSize, alignmentLines: Map<AlignmentLine, Int>, placementBlock: PlacementScope.() -> Unit): MeasureResult {
        return object : MeasureResult, PlacementScope {
            override val size: IntSize = size
            override val alignmentLines: Map<AlignmentLine, Int> = alignmentLines
            override fun placeChildren(): Unit = placementBlock()
        }
    }

    override fun place(entity: Entity, position: IntPoint2) {
        this.position = position
        measurementResult?.placeChildren()
    }
}
