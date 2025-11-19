package cn.jzl.ecs

@PublishedApi
internal class Components(componentProvider: ComponentProvider) {

    val any: ComponentId = componentProvider.id<Any>()

    val childOf: ComponentId = componentProvider.configure<ChildOf> { it.tag() }

    val componentId: ComponentId = componentProvider.id<ComponentOf>()

    sealed class ComponentOf
    sealed class ChildOf
    sealed class EventOf
}