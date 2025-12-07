package cn.jzl.sect.ecs.core

import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId

@JvmInline
value class Named(val name: String)

@JvmInline
value class Description(val description: String)

sealed class OwnedBy

val coreAddon = createAddon("core", {}) {
    components {
        world.componentId<Named>()
        world.componentId<OwnedBy> {
            it.singleRelation()
            it.tag()
        }
    }
}
