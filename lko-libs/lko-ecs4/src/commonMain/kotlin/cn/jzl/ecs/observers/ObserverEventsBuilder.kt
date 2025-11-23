package cn.jzl.ecs.observers

import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.Entity
import cn.jzl.ecs.EntityType
import cn.jzl.ecs.World
import cn.jzl.ecs.query.QueriedEntity
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.QueryShorthands

abstract class ObserverEventsBuilder<Context> : ExecutableObserver<Context> {
    abstract val world: World
    abstract val listenToEvents: Sequence<ComponentId>
    abstract val mustHoldData: Boolean
    abstract val onBuild: (Observer) -> Entity

    abstract fun provideContext(entity: Entity, event: Any?): Context

    override fun filter(vararg query: Query<out QueriedEntity>): ObserverBuilder<Context> {
        return ObserverBuilder(this, EntityType.Companion.ENTITY_TYPE_EMPTY, query.toList())
    }

    override fun exec(handle: Context.() -> Unit): Observer = filter().exec(handle)

    inline fun <reified C : Any> involving(size1: QueryShorthands.Size1? = null): ObserverBuilder<Context> {
        val component = world.componentService.component<C>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component.data)),
            emptyList()
        )
    }

    inline fun <reified C1 : Any, reified C2 : Any> involving(size2: QueryShorthands.Size2? = null): ObserverBuilder<Context> {
        val component1 = world.componentService.component<C1>()
        val component2 = world.componentService.component<C2>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component1.data, component2.data)),
            emptyList()
        )
    }

    inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any> involving(size3: QueryShorthands.Size3? = null): ObserverBuilder<Context> {
        val component1 = world.componentService.component<C1>()
        val component2 = world.componentService.component<C2>()
        val component3 = world.componentService.component<C3>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component1.data, component2.data, component3.data)),
            emptyList()
        )
    }

    inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any> involving(size4: QueryShorthands.Size4? = null): ObserverBuilder<Context> {
        val component1 = world.componentService.component<C1>()
        val component2 = world.componentService.component<C2>()
        val component3 = world.componentService.component<C3>()
        val component4 = world.componentService.component<C4>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component1.data, component2.data, component3.data, component4.data)),
            emptyList()
        )
    }

    inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any, reified C5 : Any> involving(size5: QueryShorthands.Size5? = null): ObserverBuilder<Context> {
        val component1 = world.componentService.component<C1>()
        val component2 = world.componentService.component<C2>()
        val component3 = world.componentService.component<C3>()
        val component4 = world.componentService.component<C4>()
        val component5 = world.componentService.component<C5>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component1.data, component2.data, component3.data, component4.data, component5.data)),
            emptyList()
        )
    }

    inline fun <reified C1 : Any, reified C2 : Any, reified C3 : Any, reified C4 : Any, reified C5 : Any, reified C6 : Any> involving(size6: QueryShorthands.Size6? = null): ObserverBuilder<Context> {
        val component1 = world.componentService.component<C1>()
        val component2 = world.componentService.component<C2>()
        val component3 = world.componentService.component<C3>()
        val component4 = world.componentService.component<C4>()
        val component5 = world.componentService.component<C5>()
        val component6 = world.componentService.component<C6>()
        return ObserverBuilder(
            this,
            EntityType(longArrayOf(component1.data, component2.data, component3.data, component4.data, component5.data, component6.data)),
            emptyList()
        )
    }
}