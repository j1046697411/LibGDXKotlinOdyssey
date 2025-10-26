package cn.jzl.ecs.v2

interface EntityUpdateContext : EntityCreateContext {

    operator fun Entity.minusAssign(componentType: ComponentType<*>)

    operator fun <C : Component<C>> Entity.minusAssign(component: Component<C>)
}