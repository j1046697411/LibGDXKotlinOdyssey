package cn.jzl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cn.jzl.ecs.ComponentType
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import cn.jzl.ui.node.ComposeUiLayout
import cn.jzl.ui.node.ModifierNode
import cn.jzl.ui.node.ModifierNodeElement
import kotlin.math.max

interface BoxScope {

    fun Modifier.alignment(alignment: Alignment): Modifier = this + BoxAlignmentElement(alignment)

    fun Modifier.matchParentSize(matchParentSize: Boolean): Modifier = this + MatchParentSizeElement(matchParentSize)
}

internal data class BoxChildDataNode(
    var alignment: Alignment,
    var matchParentSize: Boolean
) : ModifierNode() {
    companion object : ComponentType<BoxChildDataNode>()
}

internal data class BoxAlignmentElement(
    val alignment: Alignment
) : ModifierNodeElement<BoxChildDataNode> {

    override val nodeType: ComponentType<BoxChildDataNode> get() = BoxChildDataNode

    override fun create(): BoxChildDataNode {
        return BoxChildDataNode(alignment, false)
    }

    override fun update(node: BoxChildDataNode) {
        node.alignment = alignment
    }
}

internal data class MatchParentSizeElement(
    val matchParentSize: Boolean
) : ModifierNodeElement<BoxChildDataNode> {

    override val nodeType: ComponentType<BoxChildDataNode> get() = BoxChildDataNode

    override fun create(): BoxChildDataNode {
        return BoxChildDataNode(Alignment.TopStart, matchParentSize)
    }

    override fun update(node: BoxChildDataNode) {
        node.matchParentSize = matchParentSize
    }
}

internal object InternalBoxScope : BoxScope

@Composable
fun Box(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val boxMeasurePolicy = remember(alignment, propagateMinConstraints) {
        BoxMeasurePolicy(alignment, propagateMinConstraints)
    }
    ComposeUiLayout(modifier, boxMeasurePolicy) { InternalBoxScope.content() }
}

internal class BoxMeasurePolicy(
    private val alignment: Alignment,
    private val propagateMinConstraints: Boolean
) : MeasurePolicy {

    override fun MeasureScope.measure(entity: Entity, children: Sequence<Entity>, constraints: Constraints): MeasureResult {
        fun Entity.boxChildDataNode(): BoxChildDataNode? = this.getOrNull(BoxChildDataNode)
        val contentConstraints = if (propagateMinConstraints) {
            constraints
        } else {
            constraints.copy(minWidth = 0, minHeight = 0)
        }
        var hasMatchParentSizeChildren = false
        var boxWidth = constraints.minWidth
        var boxHeight = constraints.minHeight
        val boxItems = children.map {
            val boxChildDataNode = it.boxChildDataNode()
            val measurable = it.measurable
            if (boxChildDataNode?.matchParentSize != true) {
                val placeable = measurable.measure(it, contentConstraints)
                boxWidth = max(placeable.size.width, boxWidth)
                boxHeight = max(placeable.size.height, boxHeight)
                BoxItem(it, measurable, boxChildDataNode, placeable)
            } else {
                hasMatchParentSizeChildren = true
                BoxItem(it, measurable, boxChildDataNode, null)
            }
        }.toList()
        return if (boxItems.isEmpty()) {
            layout(entity, IntSize(constraints.minWidth, constraints.minHeight)) {}
        } else {
            if (hasMatchParentSizeChildren) {
                val boxConstraints = Constraints.fixed(boxWidth, boxHeight)
                boxItems.forEach { item ->
                    if (item.placeable == null) {
                        item.placeable = item.measurable.measure(item.entity, boxConstraints)
                    }
                }
            }
            val space = IntSize(boxWidth, boxHeight)
            layout(entity, space) {
                boxItems.forEach { item ->
                    val placeable = item.placeable ?: return@forEach
                    val alignment = item.boxChildDataNode?.alignment ?: alignment
                    placeable.place(entity, alignment.align(placeable.size, space))
                }
            }
        }
    }

    private data class BoxItem(
        val entity: Entity,
        val measurable: Measurable,
        val boxChildDataNode: BoxChildDataNode?,
        var placeable: Placeable? = null
    )
}