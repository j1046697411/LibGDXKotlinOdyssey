package cn.jzl.ecs

import cn.jzl.ecs.observers.Observer

class Components(componentProvider: ComponentProvider) {

    val any: ComponentId = componentProvider.id<Any>()

    val childOf: ComponentId = componentProvider.configure<ChildOf> {
        it.tag()
        it.singleRelation()
    }

    val prefab: ComponentId = componentProvider.configure<Prefab> { it.tag() }

    val instanceOf: ComponentId = componentProvider.configure<InstanceOf> {
        it.tag()
        it.singleRelation()
    }

    val componentId: ComponentId = componentProvider.id<ComponentOf>()
    val shadedId: ComponentId = componentProvider.id<ShadedOf>()

    val onInserted: ComponentId = componentProvider.id<OnInserted>()
    val onRemoved: ComponentId = componentProvider.id<OnRemoved>()
    val onUpdated: ComponentId = componentProvider.id<OnUpdated>()

    val onEntityCreated: ComponentId = componentProvider.id<OnEntityCreated>()
    val onEntityUpdated: ComponentId = componentProvider.id<OnEntityUpdated>()
    val onEntityDestroyed: ComponentId = componentProvider.id<OnEntityDestroyed>()

    val observerId: ComponentId = componentProvider.id<Observer>()
    val eventId: ComponentId = componentProvider.configure<EventOf> { it.tag() }

    sealed class ShadedOf

    sealed class ComponentOf
    sealed class ChildOf
    sealed class EventOf

    sealed class Prefab
    sealed class InstanceOf

    sealed class OnInserted
    sealed class OnRemoved
    sealed class OnUpdated

    sealed class OnEntityCreated
    sealed class OnEntityUpdated
    sealed class OnEntityDestroyed
}