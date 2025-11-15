package cn.jzl.ecs.component

import cn.jzl.ecs.EntityId

typealias ComponentId = EntityId

class Components(componentProvider: ComponentProvider) {

    val any: ComponentId = componentProvider.id<Any>()

    @PublishedApi
    internal val tag: ComponentId = componentProvider.id<Tag>()

    @PublishedApi
    internal val component: ComponentId = componentProvider.id<Component>()

    @PublishedApi
    internal sealed class Component

    @PublishedApi
    internal sealed class Tag
}

inline fun <reified C> ComponentProvider.id(): ComponentId = getOrRegisterComponentIdForClass(C::class)
