package cn.jzl.ecs.v2

interface EntityUpdateContext : EntityCreateContext {
    operator fun Entity.minusAssign(componentType: ComponentWriteAccesses<*>)
}