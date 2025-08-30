package cn.jzl.ui.node

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.Snapshot
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World
import cn.jzl.ui.MeasurePolicy
import cn.jzl.ui.RootMeasurePolicy
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.unit.Density
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

internal val localeWorld = compositionLocalOf<World> { error("World not provided") }
internal val localeSystem = compositionLocalOf<ComposeUiNodeSystem> { error("System not provided") }
internal val localeDensity = compositionLocalOf<Density> { error("Density not provided") }

@Composable
fun ComposeUiLayout(
    modifier: Modifier = Modifier,
    measurePolicy: MeasurePolicy,
    content: @Composable () -> Unit,
) {
    val system = localeSystem.current
    val density = localeDensity.current
    ReusableComposeNode<Entity, ComposeUiNodeApplier>(
        factory = { system.createUiNode(modifier, measurePolicy, density) },
        update = {
            set(modifier, system.setters.modifierSetter)
            set(measurePolicy, system.setters.measurePolicySetter)
            set(density, system.setters.densitySetter)
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
    val rootEntity = composeUiNodeSystem.createUiRootNode(modifier, measurePolicy, density)
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
                localeSystem provides composeUiNodeSystem,
                localeDensity provides density,
                content = content
            )
        }
        sendApplyNotificationsJob.join()
        runRecomposeAndApplyChangesJob.cancel()
    }
}

