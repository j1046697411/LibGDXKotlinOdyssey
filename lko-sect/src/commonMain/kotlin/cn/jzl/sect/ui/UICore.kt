package cn.jzl.sect.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.colorspace.Connector
import androidx.compose.ui.unit.Constraints
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

/**
 * 基础状态观察者抽象类
 *
 * 提供所有状态观察者的共同逻辑：
 * - Observer 生命周期管理
 * - 资源清理
 *
 * 子类需要实现：
 * - [createObserver] 创建具体的 Observer
 * - [onCleanup] 清理特定资源
 */
@PublishedApi
internal abstract class BaseStateObserver(world: World) : RememberObserver, EntityRelationContext(world) {

    private var observer: Observer? = null

    override fun onRemembered() {
        observer?.close()
        observer = createObserver()
    }

    override fun onForgotten() {
        observer?.close()
        observer = null
        onCleanup()
    }

    override fun onAbandoned() {
        // 默认空实现，子类可覆盖
    }

    /**
     * 创建具体的 Observer 实例
     */
    protected abstract fun createObserver(): Observer

    /**
     * 清理特定资源（在 onForgotten 时调用）
     */
    protected open fun onCleanup() {
        // 默认空实现，子类可覆盖
    }
}

/**
 * 实体列表状态观察者
 *
 * 监听查询结果的变化，自动更新实体列表
 */
@PublishedApi
internal class EntityListStateObserver(
    world: World,
    private val query: Query<out EntityQueryContext>
) : BaseStateObserver(world) {

    val entities = mutableStateListOf<Entity>()

    override fun createObserver(): Observer {
        // 初始化状态：立即填充当前查询结果
        entities.clear()
        query.forEach { entities.add(entity) }

        // 创建 Observer 监听后续变化
        return world.observe {
            yield(relations.id<Components.OnEntityCreated>())
            yield(relations.id<Components.OnEntityUpdated>())
            yield(relations.id<Components.OnEntityDestroyed>())
        }.filter(query).exec {
            entities.clear()
            query.forEach { entities.add(entity) }
        }
    }

    override fun onCleanup() {
        entities.clear()
    }
}

/**
 * 实体状态观察者
 *
 * 通过 provider 函数获取实体，监听变化并更新状态
 */
@PublishedApi
internal class EntityStateObserver(
    world: World,
    private val query: Query<*>?,
    private val provider: EntityRelationContext.() -> Entity?
) : BaseStateObserver(world) {

    val entityState = mutableStateOf(provider())

    override fun createObserver(): Observer {
        val queryFilter = this.query
        // 创建 Observer 监听后续变化
        return world.observe {
            yield(relations.id<Components.OnEntityCreated>())
            yield(relations.id<Components.OnEntityUpdated>())
            yield(relations.id<Components.OnEntityDestroyed>())
        }.let {
            if (queryFilter != null) it.filter(queryFilter) else it
        }.exec {
            val entity = entityState.value
            if (queryFilter == null || entity == null || entity !in queryFilter) {
                entityState.value = provider()
            }
        }
    }
}

/**
 * 关系状态观察者
 *
 * 监听实体与目标之间的关系变化，自动更新状态
 */
@PublishedApi
internal class RelationStateObserver<T>(
    world: World,
    val entity: Entity,
    private val relation: Relation,
    private val provider: EntityRelationContext.(Entity) -> T
) : BaseStateObserver(world) {

    val dataState = mutableStateOf(provider(entity))

    override fun createObserver(): Observer {
        return world.observe(entity) {
            yield(relations.id<Components.OnInserted>())
            yield(relations.id<Components.OnUpdated>())
            yield(relations.id<Components.OnRemoved>())
        }.involving(sequenceOf(relation)).exec {
            dataState.value = provider(entity)
        }
    }
}

// ============================================================================
// Composable 函数
// ============================================================================

/**
 * 获取服务实例
 *
 * @param T 服务类型
 * @return 服务实例
 */
@Composable
inline fun <reified T : Any> service(): T {
    val world = currentWorld
    return remember(world) {
        val service by world.di.instance<T>()
        service
    }
}

/**
 * 观察查询结果的实体列表
 *
 * 自动监听查询结果的变化并更新列表
 *
 * @param query 实体查询
 * @return 实体列表状态
 */
@Composable
fun observeEntityList(query: Query<out EntityQueryContext>): SnapshotStateList<Entity> {
    val world = currentWorld
    val observer = remember(world, query) {
        EntityListStateObserver(world, query)
    }
    return observer.entities
}

/**
 * 观察实体状态
 *
 * 通过 provider 函数获取实体，自动监听相关变化并更新实体状态
 *
 * @param query 可选的查询过滤器
 * @param provider 提供实体的函数
 * @return 实体状态
 */
@Composable
fun observeEntity(
    query: Query<*>? = null,
    vararg keys: Any?,
    provider: EntityRelationContext.() -> Entity?
): State<Entity?> {
    val worldContext = uiWorldContext
    val observer = remember(worldContext, query, *keys) {
        EntityStateObserver(worldContext.world, query, provider)
    }
    return observer.entityState
}

/**
 * 观察实体与目标之间的关系值状态
 *
 * 自动监听关系变化并更新状态
 *
 * @param target 目标实体
 * @return 关系值状态
 */
@Composable
inline fun <reified C> Entity.observeRelation(target: Entity): State<C> {
    val worldContext = uiWorldContext
    val observer = remember(worldContext, this, target) {
        val relation = worldContext.relations.relation<C>(target)
        RelationStateObserver(worldContext.world, this, relation) {
            it.getRelation<C>(target)
        }
    }
    return observer.dataState
}

/**
 * 观察实体的组件值状态
 *
 * 自动监听组件变化并更新状态
 *
 * @return 组件值状态
 */
@Composable
inline fun <reified C> Entity.observeComponent(): State<C> {
    val worldContext = uiWorldContext
    val observer = remember(worldContext, this, C::class) {
        val relation = worldContext.relations.component<C>()
        RelationStateObserver(worldContext.world, this, relation) {
            it.getComponent<C>()
        }
    }
    return observer.dataState
}

@Composable
inline fun <reified E : EntityQueryContext, reified R> Query<E>.observeState(
    vararg keys: Array<out Any?>,
    noinline transform: (Query<E>) -> R
): State<R> {
    val worldContext = uiWorldContext
    val observer = remember(worldContext, this, *keys) {
        QueryStateObserver(this, transform)
    }
    return observer.result
}

@PublishedApi
internal class QueryStateObserver<E : EntityQueryContext, R>(
    private val query: Query<E>,
    private val transform: (Query<E>) -> R
) : BaseStateObserver(query.world) {

    val result = mutableStateOf(transform(query))

    override fun createObserver(): Observer {
        return world.observe {
            yield(relations.id<Components.OnInserted>())
            yield(relations.id<Components.OnUpdated>())
            yield(relations.id<Components.OnRemoved>())
        }.filter(query).exec { result.value = transform(query) }
    }
}