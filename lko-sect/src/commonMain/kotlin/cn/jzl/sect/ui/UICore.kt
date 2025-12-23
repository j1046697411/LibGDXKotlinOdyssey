package cn.jzl.sect.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.State
import cn.jzl.di.instance
import cn.jzl.ecs.Components
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.Relation
import cn.jzl.ecs.World
import cn.jzl.ecs.observers.Observer
import cn.jzl.ecs.observers.observe
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.forEach
import cn.jzl.ecs.relations
import cn.jzl.sect.currentWorld
import cn.jzl.sect.uiWorldContext

@Composable
inline fun <reified T : Any> service(): T {
    val world = currentWorld
    return remember(world) {
        val service by world.di.instance<T>()
        service
    }
}

@Composable
fun entityList(query: Query<out EntityQueryContext>): SnapshotStateList<Entity> {
    val world = currentWorld
    val entityListObserver = remember(query) { EntityListObserver(world, query) }
    return entityListObserver.entities
}

@PublishedApi
internal class EntityListObserver(world: World, private val query: Query<out EntityQueryContext>) : RememberObserver, EntityRelationContext(world) {

    val entities = mutableStateListOf<Entity>()
    private var observer: Observer? = null

    override fun onRemembered() {
        observer?.close()
        observer = world.observe {
            yield(relations.id<Components.OnEntityCreated>())
            yield(relations.id<Components.OnEntityUpdated>())
            yield(relations.id<Components.OnEntityDestroyed>())
        }.filter(query).exec {
            entities.clear()
            query.forEach { entities.add(entity) }
        }
    }

    override fun onForgotten() {
        entities.clear()
        observer?.close()
        observer = null
    }

    override fun onAbandoned() {
    }
}

@Composable
fun entityProvider(query: Query<*>? = null, provider: EntityRelationContext.() -> Entity?): State<Entity> {
    val uiWorldContext = uiWorldContext
    val entityObserver = remember(query) {
        EntityObserver(uiWorldContext.world, query, provider)
    }
    return entityObserver.entity
}

@PublishedApi
internal class EntityObserver(
    world: World,
    private val query: Query<*>?,
    private val provider: EntityRelationContext.() -> Entity?
) : RememberObserver, EntityRelationContext(world) {

    val entity = mutableStateOf(Entity.ENTITY_INVALID)
    private var observer: Observer? = null
    override fun onRemembered() {
        observer?.close()
        val query = this.query
        observer = world.observe {
            yield(relations.id<Components.OnEntityCreated>())
            yield(relations.id<Components.OnEntityUpdated>())
            yield(relations.id<Components.OnEntityDestroyed>())
        }.let {
            if (query != null) it.filter(query) else it
        }.exec {
            if (query == null || this@EntityObserver.entity.value !in query) {
                this@EntityObserver.entity.value = provider() ?: Entity.ENTITY_INVALID
                println("onRemembered update ${this.entity}")
            }
        }
        println("onRemembered")
    }

    override fun onForgotten() {
        println("onForgotten")
        observer?.close()
        observer = null
    }

    override fun onAbandoned() {
    }
}


@Composable
inline fun <reified C> Entity.relation(target: Entity, defaultValue: C): State<C> {
    val worldContext = uiWorldContext
    val relationObserver = remember(this, target) {
        val relation = worldContext.relations.relation<C>(target)
        RelationObserver(worldContext.world, this, relation, defaultValue) {
            it.getRelation<C>(target)
        }
    }
    return relationObserver.data
}

@PublishedApi
@JvmInline
internal value class EmptyState<T>(override val value: T) : State<T>

@Composable
inline fun <reified C> Entity.component(defaultValue: C): androidx.compose.runtime.State<C> {
    if (this == Entity.ENTITY_INVALID) return EmptyState(defaultValue)
    val worldContext = uiWorldContext
    val relationObserver = remember(this, C::class) {
        val relation = worldContext.relations.component<C>()
        RelationObserver(worldContext.world, this, relation, defaultValue) { it.getComponent() }
    }
    return relationObserver.data
}

@PublishedApi
internal class RelationObserver<T>(
    world: World,
    val entity: Entity,
    private val relation: Relation,
    defaultValue: T,
    private val provider: EntityRelationContext.(Entity) -> T
) : RememberObserver, EntityRelationContext(world) {

    val data = mutableStateOf(defaultValue)
    private var observer: Observer? = null
    override fun onRemembered() {
        observer?.close()
        data.value = provider(entity)
        observer = world.observe(entity) {
            yield(relations.id<Components.OnInserted>())
            yield(relations.id<Components.OnUpdated>())
            yield(relations.id<Components.OnRemoved>())
        }.involving(sequenceOf(relation)).exec {
            data.value = provider(entity)
        }
    }

    override fun onForgotten() {
        observer?.close()
        observer = null
    }

    override fun onAbandoned() {
    }
}