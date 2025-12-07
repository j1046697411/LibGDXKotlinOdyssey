package cn.jzl.sect

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import cn.jzl.ecs.World
import cn.jzl.ecs.system.update
import cn.jzl.ecs.world
import cn.jzl.sect.ecs.market.marketAddon
import cn.jzl.sect.ecs.sectAddon
import cn.jzl.sect.ui.market.MarketPreview
import kotlin.time.Duration.Companion.milliseconds

@Composable
expect fun PlatformApplication(context: @Composable () -> Unit)

private val compositionLocalWorld = compositionLocalOf { createWorld() }
val currentWorld: World @Composable get() = compositionLocalWorld.current

private fun createWorld(): World = world {
    install(marketAddon)
    install(sectAddon)
}

@Composable
internal fun MainWorld(context: @Composable () -> Unit) {
    val world = remember { createWorld() }
    LaunchedEffect(Unit) {
        var lastFrameTime = System.currentTimeMillis()
        while (true) {
            val currentTime = System.currentTimeMillis()
            val delta = currentTime - lastFrameTime
            withFrameMillis { world.update(delta.milliseconds) }
            lastFrameTime = currentTime
        }
    }
    CompositionLocalProvider(compositionLocalWorld provides world) {
        context()
    }
}

@Preview
@Composable
fun App(): Unit = PlatformApplication {
    MaterialTheme {
        MainWorld { MarketPreview() }
    }
}