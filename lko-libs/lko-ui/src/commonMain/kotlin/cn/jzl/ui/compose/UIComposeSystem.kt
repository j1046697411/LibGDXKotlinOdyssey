package cn.jzl.ui.compose

import androidx.compose.runtime.BroadcastFrameClock
import cn.jzl.di.instance
import cn.jzl.ecs.*
import cn.jzl.ui.Constraints
import cn.jzl.ui.Coordinate
import cn.jzl.ui.Measurable
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.ecs.RootNode
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.style.StyleSheet
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.log.logger
import kotlin.time.Duration

class UIComposeSystem(world: World) : IntervalSystem(world, EachFrame), Updatable {

    private val hierarchySystem by world.instance<HierarchySystem>()
    private val broadcastFrameClock by world.instance<BroadcastFrameClock>()
    private val composeRootNodes = world.family { it.all(ComposeRootNode, ComposeLayerNode) }

    private val constraints = Constraints(maxWidth = Gdx.graphics.width, maxHeight = Gdx.graphics.height)
    private val shapeRenderer = ShapeRenderer()

    val setterModifier = { entity: Entity, modifier: Modifier ->
        world.configure(entity) {
            it[ComposeLayerNode].styleSheet = StyleSheet.Companion(modifier)
        }
    }
    val setterMeasurePolicy = { entity: Entity, measurePolicy: MeasurePolicy ->
        world.configure(entity) { it[ComposeLayerNode].measurePolicy = measurePolicy }
    }

    internal fun createRootNode(modifier: Modifier = Modifier): Entity = createNode(RootMeasurePolicy, modifier) {
        it += RootNode
        it += ComposeRootNode
    }

    fun createNode(
        measurePolicy: MeasurePolicy,
        modifier: Modifier,
        configuration: EntityCreateContext.(Entity) -> Unit = {}
    ): Entity = world.create {
        it += ComposeLayerNode(this@UIComposeSystem, it, measurePolicy, StyleSheet.Companion(modifier))
        configuration(it)
    }

    fun getMeasures(entity: Entity): Sequence<Measurable> {
        return hierarchySystem.getChildren(entity).map { it[ComposeLayerNode] }
    }

    fun getParentComposeLayerNode(entity: Entity): ComposeLayerNode? {
        return hierarchySystem.getParent(entity)?.getOrNull(ComposeLayerNode)
    }

    override fun onTick(deltaTime: Duration) {
        composeRootNodes.forEach { it[ComposeLayerNode].measure(constraints).placeAt(Coordinate.ZERO) }
        broadcastFrameClock.sendFrame(deltaTime.inWholeNanoseconds)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        composeRootNodes.forEach { draw(shapeRenderer, it) }
        shapeRenderer.end()
    }

    private fun draw(shapeRenderer: ShapeRenderer, entity: Entity) {
        with(entity[ComposeLayerNode]) {
            draw(shapeRenderer)
        }
        val children = hierarchySystem.getChildren(entity)
        children.forEach { draw(shapeRenderer, it) }
    }

    private object ComposeRootNode : EntityTag()

    companion object {
        private val log = logger<UIComposeSystem>()
    }
}