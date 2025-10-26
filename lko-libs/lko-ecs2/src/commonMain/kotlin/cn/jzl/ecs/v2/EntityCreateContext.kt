package cn.jzl.ecs.v2

import cn.jzl.di.DIProvider

interface EntityCreateContext : EntityComponentContext {

    operator fun <C : Any> Entity.set(componentType: ComponentType<C>, component: C): C?

    fun <C : Any> Entity.getOrPut(componentType: ComponentType<C>, provider: DIProvider<C>): C

    fun Entity.tags(vararg tags: EntityTag) {
        if (tags.isEmpty()) return
        tags.forEach { this[it] = true }
    }

    operator fun Entity.plusAssign(tag: EntityTag) {
        this[tag] = true
    }

    operator fun <C : Component<C>> Entity.plusAssign(component: C) {
        this[component.type] = component
    }
}