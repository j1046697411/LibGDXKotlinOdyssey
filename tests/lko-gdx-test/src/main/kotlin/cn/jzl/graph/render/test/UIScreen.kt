package cn.jzl.graph.render.test

import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.entitySize
import cn.jzl.ecs.update
import cn.jzl.ecs.world
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.render.renderPipelineModule
import cn.jzl.graph.shader.shaderPipelineModule
import cn.jzl.ui.*
import cn.jzl.ui.compose.ComposeLayerNode
import cn.jzl.ui.compose.composeModule
import cn.jzl.ui.compose.layerNode
import cn.jzl.ui.compose.ui
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.flexbox.AlignSelf
import cn.jzl.ui.flexbox.alignSelf
import cn.jzl.ui.flexbox.flexBox
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.style.background
import cn.jzl.ui.style.per
import cn.jzl.ui.style.px
import cn.jzl.ui.style.size
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.vis.visTable
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class UIScreen : KtxScreen {
    private val world = world {
        this.module(shaderPipelineModule())
        module(pipelineModule())
        module(renderPipelineModule())
        module(composeModule)
    }

    private val hierarchySystem by world.instance<HierarchySystem>()

    private val stage = Stage()
    private lateinit var debugWorldLabel: Label

    override fun show() {
        super.show()
        stage.actors {
            visTable {
                debugWorldLabel = label("Hello World").cell(grow = true, align = Align.topLeft)
            }.setFillParent(true)
        }

        val mutationPolicy = object : MeasurePolicy {
            override fun MeasureScore.measure(
                self: Measurable,
                measures: Sequence<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                return layout(IntSize(constraints.maxWidth, constraints.maxHeight), mapOf()) {
                }
            }

        }
        CoroutineScope(Dispatchers.Unconfined).launch {
            ui(world) {
                flexBox(Modifier.background(Color.CYAN)) {
                    layerNode(Modifier.size(20.per, 35.per).background(Color.YELLOW), mutationPolicy) {
                    }
                    layerNode(Modifier.size(200.px, 200.px).background(Color.RED), mutationPolicy) {
                    }
                    layerNode(Modifier.size(300.px, 300.px).background(Color.BLUE), mutationPolicy) {
                    }
                }
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        world.update(delta.toDouble().toDuration(DurationUnit.SECONDS))
        updateWorld()
        stage.act(delta)
        stage.draw()
    }

    private fun updateWorld() {
        val debug = buildString {
            append("Debug World size: ${world.entitySize}").appendLine()
            with(hierarchySystem) {
                val rootEntities = rootEntities
                rootEntities.forEach { rootEntity -> printHierarchy(rootEntity, this@buildString, 0) }
            }
        }
        debugWorldLabel.setText(debug)
    }

    private fun HierarchySystem.printHierarchy(entity: Entity, builder: StringBuilder, hierarchy: Int) {
        printEntity(entity, builder, hierarchy)
        val children = getChildren(entity)
        children.forEach { entity -> printHierarchy(entity, builder, hierarchy + 1) }
    }

    private fun HierarchySystem.printEntity(entity: Entity, builder: StringBuilder, hierarchy: Int) {
        val interval = "    ".repeat(hierarchy)
        builder.append(interval).append("Entity: $entity").appendLine()
        builder.append(interval).append("components:").appendLine()
        val composeLayerNode = entity.getOrNull(ComposeLayerNode)
    }
}
