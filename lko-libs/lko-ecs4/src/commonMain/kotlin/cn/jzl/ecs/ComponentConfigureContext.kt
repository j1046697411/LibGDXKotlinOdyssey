package cn.jzl.ecs

import kotlin.jvm.JvmInline

@JvmInline
value class ComponentConfigureContext(private val entityCreateContext: EntityCreateContext) {

    fun Entity.tag(): Unit = with(entityCreateContext) {
        world.componentService.entityTags.set(id)
    }
}