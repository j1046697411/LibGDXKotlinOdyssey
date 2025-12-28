package cn.jzl.sect

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.system.update
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.ai.aiAddon
import cn.jzl.sect.ecs.attribute.attributeAddon
import cn.jzl.sect.ecs.character.characterAddon
import cn.jzl.sect.ecs.inventory.inventoryAddon
import cn.jzl.sect.ecs.logger.LogLevel
import cn.jzl.sect.ecs.logger.logAddon
import cn.jzl.sect.ecs.planning.planningAddon
import cn.jzl.sect.ecs.resources.resourcesAddon
import cn.jzl.sect.ecs.sect.SectService
import cn.jzl.sect.ecs.sect.sectAddon
import cn.jzl.sect.ecs.time.timeAddon
import cn.jzl.sect.ui.MainUI
import cn.jzl.sect.ui.service
import kotlin.time.Duration.Companion.milliseconds

@Composable
expect fun PlatformApplication(context: @Composable () -> Unit)

class UIWorldContext(override val world: World) : EntityRelationContext(world)

private val compositionLocalWorld = compositionLocalOf { createWorld() }
private val compositionLocalUIWorldContext = compositionLocalOf<UIWorldContext> { TODO() }
val currentWorld: World @Composable get() = compositionLocalWorld.current
val uiWorldContext: UIWorldContext @Composable get() = compositionLocalUIWorldContext.current

private fun createWorld(): World = world {
    install(logAddon) {
        level = LogLevel.VERBOSE
    }
    install(characterAddon)
    install(attributeAddon)
    install(sectAddon)
    install(timeAddon)
    install(inventoryAddon)
    install(resourcesAddon)
    install(planningAddon)
    install(aiAddon)
}

@Composable
internal fun MainWorld(context: @Composable () -> Unit) {
    val world = remember { createWorld() }
    LaunchedEffect(world) {
        var lastFrameTime = System.currentTimeMillis()
        while (true) {
            val currentTime = System.currentTimeMillis()
            val delta = currentTime - lastFrameTime
            withFrameMillis { world.update(delta.milliseconds) }
            lastFrameTime = currentTime
        }
    }
    CompositionLocalProvider(
        compositionLocalWorld provides world,
        compositionLocalUIWorldContext provides UIWorldContext(world)
    ) {
        context()
    }
}

@Preview
@Composable
fun App(): Unit = PlatformApplication { MainWorld { MainUI() } }