package cn.jzl.graph.render.test

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameMillis
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
import cn.jzl.ui.Alignment
import cn.jzl.ui.Box
import cn.jzl.ui.Spacer
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.flexbox.*
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.ComposeUiLayoutNode
import cn.jzl.ui.node.ComposeUiNodeSystem
import cn.jzl.ui.node.color
import cn.jzl.ui.node.ui
import cn.jzl.ui.size
import cn.jzl.ui.unit.dp
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisSelectBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.collections.GdxArray
import ktx.collections.addAll
import ktx.scene2d.KWidget
import ktx.scene2d.actors
import ktx.scene2d.horizontalGroup
import ktx.scene2d.label
import ktx.scene2d.verticalGroup
import ktx.scene2d.vis.visSelectBox
import kotlin.enums.EnumEntries
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
        val justifyContent = mutableStateOf(JustifyContent.Start)
        val justifyContentSelectBox: VisSelectBox<JustifyContent>
        val flexDirection = mutableStateOf(FlexDirection.Column)
        val flexDirectionSelectBox: VisSelectBox<FlexDirection>
        val flexWrap = mutableStateOf(FlexWrap.Nowrap)
        val flexWrapSelectBox: VisSelectBox<FlexWrap>

        val alignContent = mutableStateOf(AlignContent.Center)
        val alignContentSelectBox: VisSelectBox<AlignContent>

        val alignItems = mutableStateOf(AlignItems.Center)
        val alignItemsSelectBox: VisSelectBox<AlignItems>

        fun <E : Enum<E>> KWidget<*>.addSelectBox(values: EnumEntries<E>, state: MutableState<E>): VisSelectBox<E> {
            horizontalGroup {
                label("${values.first()::class.simpleName}")
                return visSelectBox { items = GdxArray<E>().apply { addAll(values) } }
            }
        }

        stage.actors {
            verticalGroup {
                flexDirectionSelectBox = addSelectBox(FlexDirection.entries, flexDirection)
                justifyContentSelectBox = addSelectBox(JustifyContent.entries, justifyContent)
                flexWrapSelectBox = addSelectBox(FlexWrap.entries, flexWrap)
                alignItemsSelectBox = addSelectBox(AlignItems.entries, alignItems)
                alignContentSelectBox = addSelectBox(AlignContent.entries, alignContent)
                debugWorldLabel = label("Debug World") {
                    style.fontColor = Color.BLACK
                }
                align(Align.top)
            }.setFillParent(true)
        }
        Gdx.input.inputProcessor = stage
        CoroutineScope(Dispatchers.Unconfined).launch {
            ui(world) {
                Box(alignment = Alignment.Center) {
                    Spacer(Modifier.size(100.dp, 100.dp).color(Color.MAGENTA))
                    Spacer(Modifier.size(300.dp).color(Color.RED))
                    Spacer(Modifier.size(300.dp, 200.dp).color(Color.GREEN))
                    Spacer(Modifier.size(100.dp, 150.dp).alignment(Alignment.BottomEnd).matchParentSize(true).color(Color.BLUE))
                }
                LaunchedEffect(1) {
                    while (true) {
                        justifyContent.value = justifyContentSelectBox.selected
                        flexWrap.value = flexWrapSelectBox.selected
                        flexDirection.value = flexDirectionSelectBox.selected
                        alignItems.value = alignItemsSelectBox.selected
                        alignContent.value = alignContentSelectBox.selected
                        withFrameMillis { }
                    }
                }
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        world.update(delta.toDouble().toDuration(DurationUnit.SECONDS))
        stage.act(delta)
        updateWorld()
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
        val composeUiLayoutNode = entity[ComposeUiLayoutNode]
        builder.append(interval).appendLine("node => ${composeUiLayoutNode.coordinator.size}")
    }
}
