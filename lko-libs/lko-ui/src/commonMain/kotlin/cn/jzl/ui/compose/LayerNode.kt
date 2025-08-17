package cn.jzl.ui.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import cn.jzl.di.instance
import cn.jzl.di.module
import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.*
import cn.jzl.ui.*
import cn.jzl.ui.ecs.HierarchySystem
import cn.jzl.ui.modifier.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.type.TypeToken

private val COMPOSITION_LOCAL_WORLD = compositionLocalOf<World> { TODO("World") }
private val COMPOSITION_LOCAL_COMPOSE_SYSTEM = compositionLocalOf<UIComposeSystem> { TODO() }

@Composable
private fun currentWorld(): World = COMPOSITION_LOCAL_WORLD.current

@Composable
private fun currentComposeSystem(): UIComposeSystem = COMPOSITION_LOCAL_COMPOSE_SYSTEM.current

@Composable
fun layerNode(modifier: Modifier, measurePolicy: MeasurePolicy, content: @Composable () -> Unit) {
    val composeSystem = currentComposeSystem()
    ReusableComposeNode<Entity, LayerNodeApplier>(
        factory = { composeSystem.createNode(measurePolicy, modifier) },
        update = {
            set(modifier, composeSystem.setterModifier)
            set(measurePolicy, composeSystem.setterMeasurePolicy)
        },
        content
    )
}

suspend fun ui(world: World, content: @Composable () -> Unit) {
    val composeSystem by world.instance<UIComposeSystem>()
    val broadcastFrameClock by world.instance<BroadcastFrameClock>()
    withContext(broadcastFrameClock) {
        val layerNodeApplier = LayerNodeApplier(world, composeSystem)
        val composer = Recomposer(coroutineContext)
        val composition = Composition(layerNodeApplier, composer)
        val applyScheduled = MutableStateFlow(false)
        val snapshotHandle = Snapshot.registerGlobalWriteObserver { applyScheduled.value = true }
        val sendApplyNotificationsJob = applyScheduled.filter { it }.onEach {
            Snapshot.sendApplyNotifications()
            applyScheduled.value = false
        }.launchIn(this)
        val runRecomposeAndApplyChangesJob = launch { composer.runRecomposeAndApplyChanges() }
        sendApplyNotificationsJob.invokeOnCompletion {
            composer.close()
            snapshotHandle.dispose()
        }
        composition.setContent {
            CompositionLocalProvider(
                COMPOSITION_LOCAL_WORLD provides world,
                COMPOSITION_LOCAL_COMPOSE_SYSTEM provides composeSystem,
                content = content
            )
        }
        sendApplyNotificationsJob.join()
        runRecomposeAndApplyChangesJob.cancel()
    }
}

val composeModule = module(TypeToken.Any, "UIComposeModule") {
    this bind singleton { new(::UIComposeSystem) }
    this bind singleton { new(::HierarchySystem) }
    this bind singleton { BroadcastFrameClock() }
}