package cn.jzl.ecs

@PublishedApi
internal class Components(componentProvider: ComponentProvider) {

    val any: ComponentId = componentProvider.id<Any>()

    val childOf: ComponentId = componentProvider.configure<ChildOf> {
        it.tag()
        it.singleRelation()
    }

    val componentId: ComponentId = componentProvider.id<ComponentOf>()
    val shadedId: ComponentId = componentProvider.id<ShadedOf>()

    val onInserted: ComponentId = componentProvider.id<OnInserted>()
    val onRemoved: ComponentId = componentProvider.id<OnRemoved>()
    val onUpdated: ComponentId = componentProvider.id<OnUpdated>()

    sealed class ShadedOf

    sealed class ComponentOf
    sealed class ChildOf
    sealed class EventOf

    sealed class OnInserted
    sealed class OnRemoved
    sealed class OnUpdated
}