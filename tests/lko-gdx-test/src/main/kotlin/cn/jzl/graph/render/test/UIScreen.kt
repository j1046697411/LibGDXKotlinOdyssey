package cn.jzl.graph.render.test

import androidx.compose.runtime.*
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
import cn.jzl.ui.*
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.flexbox.*
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.ComposeUiLayoutNode
import cn.jzl.ui.node.ComposeUiNodeSystem
import cn.jzl.ui.node.ui
import cn.jzl.ui.unit.dp
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisSelectBox
import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.context2d
import korlibs.image.color.RGBA
import korlibs.image.color.RgbaPremultipliedArray
import korlibs.image.paint.BitmapFiller
import korlibs.image.paint.BitmapPaint
import korlibs.image.paint.ColorFiller
import korlibs.image.paint.ColorPaint
import korlibs.image.paint.GradientFiller
import korlibs.image.paint.GradientPaint
import korlibs.image.paint.NoneFiller
import korlibs.image.paint.NonePaint
import korlibs.image.vector.Context2d
import korlibs.image.vector.rasterizer.Rasterizer
import korlibs.image.vector.renderer.Renderer
import korlibs.math.annotations.KormaExperimental
import korlibs.math.geom.Rectangle
import korlibs.math.geom.vector.RastScale
import korlibs.math.geom.vector.Winding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.collections.GdxArray
import ktx.collections.addAll
import ktx.log.logger
import ktx.scene2d.*
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
    private lateinit var texture: Texture
    private val spriteBatch by lazy { SpriteBatch() }

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
        val pixmap = Pixmap(100, 100, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()

        val pixmapBitmap = PixmapBitmap(pixmap)
        pixmapBitmap.context2d {
            fill(RGBA(0XFF00FF, 0xff)) {
                rect(20, 20, 40, 40)
            }
            stroke(RGBA(0xff0000, 0xff)) {
                rect(50, 50, 20, 20)
            }
        }
        texture = Texture(pixmap)
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
                Box(alignment = Alignment.Center, modifier = Modifier.padding(horizontal = 15.dp)) {
                    Spacer(Modifier.size(100.dp, 100.dp).padding(20.dp))
                    Spacer(Modifier.size(300.dp).padding(10.dp))
                    Spacer(Modifier.size(300.dp, 200.dp))
                    Spacer(Modifier.size(100.dp, 150.dp).alignment(Alignment.BottomEnd).matchParentSize(true))
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
        spriteBatch.begin()
        spriteBatch.draw(texture, 0f, 0f)
        spriteBatch.end()
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

class PixmapBitmap(
    private val pixmap: Pixmap
) : Bitmap(pixmap.width, pixmap.height, 32, true, backingArray = pixmap) {

    override fun setRgbaRaw(x: Int, y: Int, v: RGBA) {
        pixmap.drawPixel(x, y, v.value)
    }

    override fun getRgbaRaw(x: Int, y: Int): RGBA {
        return RGBA(pixmap.getPixel(x, y))
    }

    override fun getContext2d(antialiasing: Boolean): Context2d {
        return Context2d(PixmapRenderer(pixmap))
    }
    companion object {
        val log = logger<PixmapBitmap>()
    }
}

private class PixmapRenderer(
    private val pixmap: Pixmap
) : Renderer() {
    override val width: Int get() = pixmap.width
    override val height: Int get() = pixmap.height

    val colorFiller = ColorFiller()
    val gradientFiller = GradientFiller()
    val bitmapFiller = BitmapFiller()
    private val color = RgbaPremultipliedArray(width)

    val rasterizer = Rasterizer()
    @OptIn(KormaExperimental::class)
    override fun renderFinal(state: Context2d.State, fill: Boolean, winding: Winding?) {
        super.renderFinal(state, fill, winding)
        rasterizer.reset()
        val style = if (fill) state.fillStyle else state.strokeStyle

        val filler = when (style) {
            is NonePaint -> NoneFiller
            is ColorPaint -> colorFiller.set(style, state)
            is GradientPaint -> gradientFiller.set(style, state)
            is BitmapPaint -> bitmapFiller.set(style, state)
            else -> TODO()
        }

        rasterizer.path.add(state.path)
        rasterizer.rasterizeFill(Rectangle(0, 0, width, height), 1) { x0, x1, y ->
            val width1 = width - 1
            val x0 = x0.coerceIn(0, width1 * RastScale.RAST_FIXED_SCALE)
            val x1 = x1.coerceIn(0, width1 * RastScale.RAST_FIXED_SCALE)
            val a = x0 / RastScale.RAST_FIXED_SCALE
            val b = x1 / RastScale.RAST_FIXED_SCALE
            val y = y / RastScale.RAST_FIXED_SCALE
            val i0 = a.coerceIn(0, width1)
            val i1 = b.coerceIn(0, width1)
            val i0m = x0 % RastScale.RAST_FIXED_SCALE
            val i1m = x1 % RastScale.RAST_FIXED_SCALE
            filler.fill(color, 0, i0, i1, y)
            val index = width * y
            pixmap.pixels.asIntBuffer().put(index + i0, color.ints, i0, i1 - i0 + 1)
//            filler.fill(color, 0, x0, x1, y)
            log.debug { "rasterizer $x0, $x1, $y" }
        }
    }
    companion object {
        val log = logger<PixmapRenderer>()
    }
}