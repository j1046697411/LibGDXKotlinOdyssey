package cn.jzl.sect.ecs

import cn.jzl.di.new
import cn.jzl.di.singleton
import cn.jzl.ecs.EntityRelationContext
import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon

val timeAddon = createAddon("timeAddon") {
    injects { this bind singleton { new(::TimeService) } }
}

class TimeService(world: World) : EntityRelationContext(world) {
}