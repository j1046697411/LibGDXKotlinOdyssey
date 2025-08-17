package cn.jzl.ui.compose

import cn.jzl.ecs.Component
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityComponentContext
import cn.jzl.ui.*
import cn.jzl.ui.style.StyleSheet
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class ComposeLayerNode(
    val composeSystem: UIComposeSystem,
    val entity: Entity,
    override var measurePolicy: MeasurePolicy,
    override var styleSheet: StyleSheet
) : Component<ComposeLayerNode>, Measurable, LayerNode, DrawScope {

    override val parentLayoutCoordinates: LayoutCoordinates? get() = composeSystem.getParentComposeLayerNode(entity)?.nodeCoordinates

    override val nodeCoordinates: NodeCoordinates = SimpleNodeCoordinates(this)

    override val measures: Sequence<Measurable> get() = composeSystem.getMeasures(entity)

    override val type: ComponentType<ComposeLayerNode> get() = ComposeLayerNode

    override fun get(alignmentLine: AlignmentLine): Int = nodeCoordinates[alignmentLine]

    override fun measure(constraints: Constraints): Placeable = nodeCoordinates.measure(constraints)

    override fun EntityComponentContext.draw(shape: ShapeRenderer) {
        nodeCoordinates.run { draw(shape) }
    }

    companion object : ComponentType<ComposeLayerNode>()
}