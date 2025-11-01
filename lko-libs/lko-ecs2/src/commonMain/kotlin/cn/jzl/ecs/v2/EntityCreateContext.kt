package cn.jzl.ecs.v2

import cn.jzl.di.DIProvider

interface EntityCreateContext : EntityComponentContext {

    operator fun <C : Any> Entity.set(componentType: ComponentWriteAccesses<C>, component: C): C?

    fun <C : Any> Entity.getOrPut(componentType: ComponentWriteAccesses<C>, provider: DIProvider<C>): C

    operator fun Entity.plusAssign(tag: ComponentWriteAccesses<Boolean>)
}