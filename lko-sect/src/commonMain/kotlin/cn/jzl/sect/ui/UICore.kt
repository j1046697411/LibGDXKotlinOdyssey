package cn.jzl.sect.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import cn.jzl.ecs.*
import cn.jzl.ecs.observers.observe
import cn.jzl.sect.uiWorldContext

@Composable
fun Entity.observer(relation: Relation, observe: EntityRelationContext.(Entity) -> Unit) {
    val entity = this
    val uiWorldContext = uiWorldContext
    DisposableEffect(entity) {
        val observe = uiWorldContext.world.observe {
            yield(it.id<Components.OnInserted>())
            yield(it.id<Components.OnUpdated>())
            yield(it.id<Components.OnRemoved>())
        }.involving(sequenceOf(relation)).exec {
            with(uiWorldContext) {
                if (entity.hasRelation(relation)) {
                    observe(this@observer)
                }
            }
        }
        onDispose { observe.close() }
    }
}

@Composable
inline fun <reified K> Entity.observerWithData(
    target: Entity,
    crossinline observe: EntityRelationContext.(K) -> Unit
) {
    val uiWorldContext = uiWorldContext
    val relation = remember(target) {
        uiWorldContext.relations.relation<K>(target)
    }
    observer(relation) { observe(it.getRelation<K>(target)) }
}

@Composable
inline fun <reified K, reified T> Entity.observerWithData(
    crossinline observe: EntityRelationContext.(K) -> Unit
) = observerWithData(id<T>(), observe)

@PublishedApi
@Composable
internal inline fun <reified T> id(): Entity {
    val uiWorldContext = uiWorldContext
    return remember { uiWorldContext.relations.id<T>() }
}

@PublishedApi
@Composable
internal fun componentId(): Entity {
    val uiWorldContext = uiWorldContext
    return remember { uiWorldContext.components.componentId }
}

@Composable
inline fun <reified C> Entity.observerWithComponent(
    crossinline observe: EntityRelationContext.(C) -> Unit
): Unit = observerWithData(componentId(), observe)