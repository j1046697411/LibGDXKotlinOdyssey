package cn.jzl.ecs.observers

import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.Entity
import cn.jzl.ecs.World

data class ObserverWithoutData(
    override val world: World,
    override val listenToEvents: Sequence<ComponentId>,
    override val onBuild: (Observer) -> Entity
) : ObserverEventsBuilder<ObserverContext>() {

    override val mustHoldData: Boolean get() = false

    private val observerContext = object : ObserverContext {
        override val world: World get() = this@ObserverWithoutData.world
        override var entity: Entity = Entity.Companion.ENTITY_INVALID
    }

    @Suppress("UNCHECKED_CAST")
    override fun provideContext(entity: Entity, event: Any?): ObserverContext {
        observerContext.entity = entity
        return observerContext
    }
}