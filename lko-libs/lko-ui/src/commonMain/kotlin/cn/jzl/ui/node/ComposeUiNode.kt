package cn.jzl.ui.node

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ui.*
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.unit.Density
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal val localeWorld = compositionLocalOf<World> { error("World not provided") }
internal val localeDensity = compositionLocalOf<Density> { Density.Default }

@Composable
fun ComposeUiLayout(
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy,
    content: @Composable () -> Unit,
) {
    val world = localeWorld.current
    val density = localeDensity.current
    val composeUiNodeSystem = remember(world) {
        val composeUiNodeSystem by world.instance<ComposeUiNodeSystem>()
        composeUiNodeSystem
    }
    val composeUiNodeSetters = remember(world) { ComposeUiNodeSetters(world) }
    ReusableComposeNode<Entity, ComposeUiNodeApplier>(
        factory = { composeUiNodeSystem.createComposeUNode(modifier, measurePolicy, density) },
        update = {
            set(modifier, composeUiNodeSetters.modifierSetter)
            set(measurePolicy, composeUiNodeSetters.measurePolicySetter)
            set(density, composeUiNodeSetters.densitySetter)
        },
        content = content
    )
}

suspend fun ui(
    world: World,
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy = RootMeasurePolicy,
    density: Density = Density.Default,
    content: @Composable () -> Unit
) {
    val broadcastFrameClock by world.instance<BroadcastFrameClock>()
    val composeUiNodeSystem by world.instance<ComposeUiNodeSystem>()
    val rootEntity = composeUiNodeSystem.createComposeUiRootNode(modifier, measurePolicy, density)
    withContext(broadcastFrameClock) {
        val layerNodeApplier = ComposeUiNodeApplier(world, rootEntity)
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
                localeWorld provides world,
                localeDensity provides density,
                content = content
            )
        }
        sendApplyNotificationsJob.join()
        runRecomposeAndApplyChangesJob.cancel()
    }
}

