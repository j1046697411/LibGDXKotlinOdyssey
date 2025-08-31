package cn.jzl.ui.node

import androidx.compose.runtime.BroadcastFrameClock
import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.lko.math.IntPoint2
import cn.jzl.ui.Constraints
import cn.jzl.ui.EntityHierarchyContext
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.unit.Density
import kotlin.time.Duration

class ComposeUiNodeSystem(world: World) : System(world), Updatable, EntityHierarchyContext {

    private val hierarchySystem by world.instance<HierarchySystem>()
    private val broadcastFrameClock by world.instance<BroadcastFrameClock>()
    private val constraints = Constraints(0, 100, 0, 100)

    internal val setters = ComposeUiNodeSetters(world)

    private val modifierNodes = world.family {
        it.all(ComposeUiLayoutNode, ComposeUiModifierComponent, ModifierUpdateTag)
    }
    private val densityNodes = world.family {
        it.all(ComposeUiLayoutNode, ComposeUiDensityComponent, DensityUpdateTag)
    }

    private val uiRootNodes = world.family {
        it.all(ComposeUiLayoutNode, ComposeUiRootNode)
    }

    internal fun createUiRootNode(modifier: Modifier, measurePolicy: MeasurePolicy, density: Density): Entity = createUiNode(modifier, measurePolicy, density) {
        it += ComposeUiRootNode
    }

    internal fun createUiNode(
        modifier: Modifier,
        measurePolicy: MeasurePolicy,
        density: Density,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = world.create {
        it += ComposeUiModifierComponent(modifier)
        it += ComposeUiLayoutNode(this@ComposeUiNodeSystem, density)
        it += ComposeUiMeasurePolicyComponent(measurePolicy)
        it += ComposeUiDensityComponent(density)
        it += ModifierUpdateTag
        configuration(it)
    }

    override fun update(deltaTime: Duration) {
        broadcastFrameClock.sendFrame(deltaTime.inWholeNanoseconds)
        densityNodes.forEach(::updateDensity)
        modifierNodes.forEach(::updateModifier)
        uiRootNodes.forEach { it[ComposeUiLayoutNode].measure(it, constraints).place(it, IntPoint2.Zero) }
    }


    private fun updateDensity(entity: Entity): Unit = world.configure(entity) {
        it[ComposeUiLayoutNode].density = it[ComposeUiDensityComponent].density
        it -= DensityUpdateTag
    }

    private fun updateModifier(entity: Entity): Unit = world.configure(entity) {
        val modifiers = it[ComposeUiModifierComponent]
        modifiers.modifierNodes.forEach { element ->
            val nodeType = element.nodeType
            if (nodeType in it) {
                element.update(it[nodeType])
            } else {
                it[nodeType] = element.create()
            }
        }
        it -= ModifierUpdateTag
        syncCoordinators(entity, modifiers)
    }

    private fun syncCoordinators(entity: Entity, modifiers: ComposeUiModifierComponent) {
        val layoutNode = entity[ComposeUiLayoutNode]
        layoutNode.outerCoordinator = modifiers.modifierNodes.fold(layoutNode.innerCoordinator) { acc, element ->
            val node = entity[element.nodeType]
            val layoutModifierNode = node.asLayoutModifierNode()
            val newCoordinator = if (layoutModifierNode != null) {
                val nodeCoordinator = if (node.coordinator != null) {
                    val layoutModifierNodeCoordinator = node.coordinator as LayoutModifierNodeCoordinator
                    layoutModifierNodeCoordinator.layoutModifierNode = layoutModifierNode
                    layoutModifierNodeCoordinator
                } else {
                    val layoutModifierNodeCoordinator = LayoutModifierNodeCoordinator(
                        this@ComposeUiNodeSystem,
                        layoutModifierNode,
                        layoutNode
                    )
                    node.updateCoordinator(layoutModifierNodeCoordinator)
                    layoutModifierNodeCoordinator
                }
                nodeCoordinator.next = acc
                acc.previous = nodeCoordinator
                nodeCoordinator
            } else {
                node.updateCoordinator(acc)
                acc
            }
            newCoordinator
        }
    }

    private fun ModifierNode.asLayoutModifierNode(): LayoutModifierNode? {
        return this as? LayoutModifierNode
    }

    override val Entity.children: Sequence<Entity> get() = hierarchySystem.getChildren(this)
    override val Entity.parent: Entity? get() = hierarchySystem.getParent(this)

    internal data object ComposeUiRootNode : EntityTag()
    internal object ModifierUpdateTag : EntityTag()
    internal object DensityUpdateTag : EntityTag()
}
