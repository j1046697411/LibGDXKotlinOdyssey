package cn.jzl.ui

import androidx.compose.runtime.BroadcastFrameClock
import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.*
import cn.jzl.ui.node.coordinator.ComposeUiLayoutNode
import cn.jzl.ui.node.coordinator.LayoutModifierNodeCoordinator
import cn.jzl.ui.unit.Density
import com.badlogic.gdx.Gdx
import ktx.log.logger
import kotlin.time.Duration

internal data object ComposeUiNodeRoot : EntityTag()
internal data object ComposeUiNode : EntityTag()

class ComposeUiNodeSystem(world: World) : System(world), Updatable {

    private val broadcastFrameClock by world.instance<BroadcastFrameClock>()
    private val hierarchySystem by world.instance<HierarchySystem>()

    private val updateModifiers = world.family {
        it.all(ModifierComponent, ComposeUiLayoutNode, ModifierUpdateTag)
    }
    private val uiRootNodes = world.family {
        it.all(ComposeUiNodeRoot, ComposeUiLayoutNode)
    }

    internal fun createComposeUiRootNode(
        modifier: Modifier,
        measurePolicy: MeasurePolicy,
        density: Density
    ): Entity = hierarchySystem.createRoot {
        config(it, modifier, measurePolicy, density)
        it += ComposeUiNodeRoot
    }

    private fun EntityCreateContext.config(
        entity: Entity,
        modifier: Modifier,
        measurePolicy: MeasurePolicy,
        density: Density
    ) {
        entity += ModifierComponent(modifier)
        entity += MeasurePolicyComponent(measurePolicy)
        entity += DensityComponent(density)
        entity += ModifierUpdateTag
        entity += ComposeUiNode
        entity += ComposeUiLayoutNode(this@ComposeUiNodeSystem, entity)
    }

    internal fun createComposeUNode(
        modifier: Modifier,
        measurePolicy: MeasurePolicy,
        density: Density,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = world.create {
        config(it, modifier, measurePolicy, density)
        configuration(it)
    }

    override fun update(deltaTime: Duration) {
        if (updateModifiers.size != 0) {
            updateModifiers.forEach(::updateModifier)
        }
        broadcastFrameClock.sendFrame(deltaTime.inWholeNanoseconds)
        measure(Constraints.fixed(Gdx.graphics.width, Gdx.graphics.height))
    }

    private fun measure(constraints: Constraints) {
        uiRootNodes.forEach { entity -> entity[ComposeUiLayoutNode].measure(constraints) }
    }

    private fun updateModifier(entity: Entity) = world.configure(entity) {
        val modifiers = it[ModifierComponent]
        modifiers.modifierNodes.forEach { element ->
            val nodeType = element.nodeType
            if (nodeType in entity) {
                element.update(it[nodeType])
            } else {
                it[nodeType] = element.create()
            }
        }
        it -= ModifierUpdateTag
        syncCoordinators(entity, modifiers)
    }

    private fun syncCoordinators(entity: Entity, modifiers: ModifierComponent) {
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
                    val layoutModifierNodeCoordinator = LayoutModifierNodeCoordinator(layoutNode, layoutModifierNode)
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

    internal fun getMeasures(entity: Entity): Sequence<Measurable> {
        return hierarchySystem.getChildren(entity).mapNotNull {
            it.getOrNull(ComposeUiLayoutNode)
        }
    }

    internal fun getMeasurePolicy(entity: Entity): MeasurePolicy = entity[MeasurePolicyComponent].measurePolicy

    internal fun getModifierNodes(entity: Entity): Sequence<ModifierNode> {
        return entity[ModifierComponent].modifierNodes.map { entity[it.nodeType] }
    }

    internal fun getDensity(entity: Entity): Density = entity[DensityComponent].density

    companion object {
        val log = logger<ComposeUiNodeSystem>()
    }
}