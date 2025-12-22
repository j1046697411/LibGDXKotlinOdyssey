package cn.jzl.sect.ecs

import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId

@JvmInline
value class Named(val name: String)

@JvmInline
value class Description(val description: String)

sealed class OwnedBy

sealed class UsedBy

val coreAddon = createAddon("core", {}) {
    components {
        world.componentId<Named>()
        world.componentId<OwnedBy> {
            it.singleRelation()
            it.tag()
        }
        world.componentId<UsedBy> {
            it.singleRelation()
            it.tag()
        }
    }
}
