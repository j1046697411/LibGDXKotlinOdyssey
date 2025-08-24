package cn.jzl.graph.render.test

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cn.jzl.di.instance
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.Entity
import cn.jzl.ecs.entitySize
import cn.jzl.ecs.update
import cn.jzl.ecs.world
import cn.jzl.graph.common.rendering.pipelineModule
import cn.jzl.graph.render.renderPipelineModule
import cn.jzl.graph.shader.shaderPipelineModule
import cn.jzl.lko.math.IntPoint2
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.ComposeUiNodeSystem
import cn.jzl.ui.Measurable
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.MeasureResult
import cn.jzl.ui.MeasureScope
import cn.jzl.ui.Placeable
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.ComposeUiLayout
import cn.jzl.ui.node.Constraints
import cn.jzl.ui.node.size
import cn.jzl.ui.node.ui
import cn.jzl.ui.unit.UIUnit
import cn.jzl.ui.unit.dp
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        this bind singleton { BroadcastFrameClock() }
        this bind singleton { new(::ComposeUiNodeSystem) }
        this bind singleton { new(::HierarchySystem) }
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
        val test = object : MeasurePolicy {
            override fun MeasureScope.measure(measurables: Sequence<Measurable>, constraints: Constraints): MeasureResult {
                return layout(IntSize(constraints.maxWidth, constraints.maxHeight)) {
                }
            }
        }

        CoroutineScope(Dispatchers.Unconfined).launch {
            ui(world) {
                var size by remember { mutableStateOf(0) }
                ComposeUiLayout(modifier = Modifier.size(size.dp, UIUnit.Auto), measurePolicy = test) {
                }
                ComposeUiLayout(modifier = Modifier.size(size.dp), measurePolicy = test) {
                }

                if (size % 4 >= 2) {
                    ComposeUiLayout(modifier = Modifier.size(10.dp), measurePolicy = test) {}
                }
                LaunchedEffect(Unit) {
                    while (true) {
                        size += 1
                        delay(5000)
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
    }
}
