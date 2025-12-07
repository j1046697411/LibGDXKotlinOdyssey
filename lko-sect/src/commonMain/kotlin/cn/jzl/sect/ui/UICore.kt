package cn.jzl.sect.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cn.jzl.ecs.*
import cn.jzl.ecs.observers.observe
import cn.jzl.sect.currentWorld

interface Updater {

    val involvingRelations: Sequence<Relation>

    fun EntityRelationContext.update(entity: Entity)
}

@Composable
inline fun <reified U : Updater> UIEntity(
    entity: Entity,
    crossinline updaterFactory: (World) -> U,
    crossinline context: @Composable (model: U) -> Unit
) {
    val world = currentWorld
    val updater = remember(world, entity) { updaterFactory(world) }
    var active by remember(entity) { mutableStateOf(true) }
    DisposableEffect(world, entity) {
        fun update() {
            if (world.isActive(entity)) {
                world.entity(entity) { updater.run { update(it) } }
            } else {
                active = false
            }
        }

        val observer = world.observe(entity) {
            yield(it.id<Components.OnInserted>())
            yield(it.id<Components.OnUpdated>())
        }.involving(updater.involvingRelations).exec { update() }
        update()
        onDispose { observer.close() }
    }
    if (active) {
        context(updater)
    }
}